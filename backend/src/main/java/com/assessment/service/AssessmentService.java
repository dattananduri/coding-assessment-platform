package com.assessment.service;

import com.assessment.dto.*;
import com.assessment.english.EnglishEvaluationService;
import com.assessment.model.*;
import com.assessment.repository.*;
import com.assessment.sandbox.JavaSandboxService;
import com.assessment.security.JwtTokenProvider;
import com.assessment.sql.SqlSandboxService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AssessmentService {

    private final AssessmentRepository assessmentRepository;
    private final QuestionRepository questionRepository;
    private final ExamAttemptRepository examAttemptRepository;
    private final AttemptQuestionRepository attemptQuestionRepository;
    private final EnglishEvaluationRepository englishEvaluationRepository;
    private final ViolationLogRepository violationLogRepository;
    private final JavaSandboxService javaSandboxService;
    private final SqlSandboxService sqlSandboxService;
    private final EnglishEvaluationService englishEvaluationService;
    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper;

    public AssessmentService(
            AssessmentRepository assessmentRepository,
            QuestionRepository questionRepository,
            ExamAttemptRepository examAttemptRepository,
            AttemptQuestionRepository attemptQuestionRepository,
            EnglishEvaluationRepository englishEvaluationRepository,
            ViolationLogRepository violationLogRepository,
            JavaSandboxService javaSandboxService,
            SqlSandboxService sqlSandboxService,
            EnglishEvaluationService englishEvaluationService,
            JwtTokenProvider jwtTokenProvider,
            ObjectMapper objectMapper) {
        this.assessmentRepository = assessmentRepository;
        this.questionRepository = questionRepository;
        this.examAttemptRepository = examAttemptRepository;
        this.attemptQuestionRepository = attemptQuestionRepository;
        this.englishEvaluationRepository = englishEvaluationRepository;
        this.violationLogRepository = violationLogRepository;
        this.javaSandboxService = javaSandboxService;
        this.sqlSandboxService = sqlSandboxService;
        this.englishEvaluationService = englishEvaluationService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.objectMapper = objectMapper;
    }

    public Assessment getAssessmentByCode(String code) {
        return assessmentRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Assessment not found for code: " + code));
    }

    @Transactional
    public CandidateStartResponse startExam(CandidateStartRequest request) {
        Assessment assessment = getAssessmentByCode(request.getTestCode());
        if (!assessment.isActive()) {
            throw new RuntimeException("Assessment is no longer active.");
        }

        // Check if candidate already has an existing attempt for this test code
        Optional<ExamAttempt> existingOpt = examAttemptRepository.findByCandidateEmailAndTestCode(
                request.getCandidateEmail(), request.getTestCode());

        ExamAttempt attempt;
        if (existingOpt.isPresent()) {
            attempt = existingOpt.get();
            // Check expiration
            checkAndHandleExpiration(attempt);
        } else {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime expires = now.plusMinutes(assessment.getDurationMinutes());

            attempt = new ExamAttempt(
                    request.getCandidateName(),
                    request.getCandidateEmail(),
                    request.getTestCode(),
                    assessment,
                    now,
                    expires
            );
            attempt = examAttemptRepository.save(attempt);

            // Select deterministic questions:
            // 3 Java questions (Arrays, Strings, Data Structures)
            List<Question> javaQuestions = selectJavaQuestions();
            // 3 SQL questions (1 JOIN, 1 Aggregation, 1 Window Function)
            List<Question> sqlQuestions = selectSqlQuestions();
            // 1 English question
            Question englishQuestion = selectEnglishQuestion();

            int order = 1;
            for (Question q : javaQuestions) {
                AttemptQuestion aq = new AttemptQuestion(attempt, q, order++, q.getStarterCode());
                attempt.getAttemptQuestions().add(aq);
            }
            for (Question q : sqlQuestions) {
                AttemptQuestion aq = new AttemptQuestion(attempt, q, order++, q.getStarterCode());
                attempt.getAttemptQuestions().add(aq);
            }
            if (englishQuestion != null) {
                AttemptQuestion aq = new AttemptQuestion(attempt, englishQuestion, order, "");
                attempt.getAttemptQuestions().add(aq);
            }

            attempt = examAttemptRepository.save(attempt);
        }

        CandidateStartResponse response = new CandidateStartResponse();
        response.setAttemptId(attempt.getId());
        response.setCandidateName(attempt.getCandidateName());
        response.setCandidateEmail(attempt.getCandidateEmail());
        response.setTestCode(attempt.getTestCode());
        response.setAssessmentTitle(assessment.getTitle());
        response.setStartedAt(attempt.getStartedAt());
        response.setExpiresAt(attempt.getExpiresAt());

        long remainingSecs = Math.max(0, Duration.between(LocalDateTime.now(), attempt.getExpiresAt()).getSeconds());
        response.setRemainingSeconds(remainingSecs);

        String token = jwtTokenProvider.generateCandidateToken(attempt.getId(), attempt.getCandidateEmail(), attempt.getTestCode());
        response.setToken(token);

        return response;
    }

    private List<Question> selectJavaQuestions() {
        List<Question> arraysList = questionRepository.findByCategoryAndTopic("JAVA", "ARRAYS");
        List<Question> stringsList = questionRepository.findByCategoryAndTopic("JAVA", "STRINGS");
        List<Question> dsList = questionRepository.findByCategoryAndTopic("JAVA", "DATA_STRUCTURES");

        Collections.shuffle(arraysList);
        Collections.shuffle(stringsList);
        Collections.shuffle(dsList);

        List<Question> selected = new ArrayList<>();
        if (!arraysList.isEmpty()) selected.add(arraysList.get(0));
        if (!stringsList.isEmpty()) selected.add(stringsList.get(0));
        if (!dsList.isEmpty()) selected.add(dsList.get(0));

        // If any topic had no question, fallback to any JAVA question not already selected
        if (selected.size() < 3) {
            List<Question> allJava = questionRepository.findByCategory("JAVA");
            Collections.shuffle(allJava);
            for (Question q : allJava) {
                if (!selected.contains(q)) {
                    selected.add(q);
                    if (selected.size() == 3) break;
                }
            }
        }
        return selected;
    }

    private List<Question> selectSqlQuestions() {
        List<Question> joinsList = questionRepository.findByCategoryAndTopic("SQL", "JOINS");
        List<Question> aggList = questionRepository.findByCategoryAndTopic("SQL", "AGGREGATION");
        List<Question> winList = questionRepository.findByCategoryAndTopic("SQL", "WINDOW_FUNCTIONS");

        Collections.shuffle(joinsList);
        Collections.shuffle(aggList);
        Collections.shuffle(winList);

        List<Question> selected = new ArrayList<>();
        if (!joinsList.isEmpty()) selected.add(joinsList.get(0));
        if (!aggList.isEmpty()) selected.add(aggList.get(0));
        if (!winList.isEmpty()) selected.add(winList.get(0));

        if (selected.size() < 3) {
            List<Question> allSql = questionRepository.findByCategory("SQL");
            Collections.shuffle(allSql);
            for (Question q : allSql) {
                if (!selected.contains(q)) {
                    selected.add(q);
                    if (selected.size() == 3) break;
                }
            }
        }
        return selected;
    }

    private Question selectEnglishQuestion() {
        List<Question> list = questionRepository.findByCategory("ENGLISH");
        if (list.isEmpty()) return null;
        Collections.shuffle(list);
        return list.get(0);
    }

    @Transactional
    public AttemptStateDto getAttemptState(Long attemptId) {
        ExamAttempt attempt = examAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new RuntimeException("Attempt not found"));

        checkAndHandleExpiration(attempt);

        AttemptStateDto state = new AttemptStateDto();
        state.setAttemptId(attempt.getId());
        state.setCandidateName(attempt.getCandidateName());
        state.setCandidateEmail(attempt.getCandidateEmail());
        state.setTestCode(attempt.getTestCode());
        state.setAssessmentTitle(attempt.getAssessment() != null ? attempt.getAssessment().getTitle() : "Assessment");
        state.setStatus(attempt.getStatus());
        state.setStartedAt(attempt.getStartedAt());
        state.setExpiresAt(attempt.getExpiresAt());

        long remainingSecs = Math.max(0, Duration.between(LocalDateTime.now(), attempt.getExpiresAt()).getSeconds());
        state.setRemainingSeconds(remainingSecs);

        List<QuestionSummaryDto> summaries = attempt.getAttemptQuestions().stream()
                .map(this::toSummaryDto)
                .collect(Collectors.toList());
        state.setQuestions(summaries);

        double total = summaries.stream().mapToDouble(QuestionSummaryDto::getScoreAwarded).sum();
        state.setCurrentTotalScore(Math.round(total * 10.0) / 10.0);

        return state;
    }

    private QuestionSummaryDto toSummaryDto(AttemptQuestion aq) {
        QuestionSummaryDto dto = new QuestionSummaryDto();
        dto.setOrder(aq.getQuestionOrder());
        dto.setQuestionId(aq.getQuestion().getId());
        dto.setCategory(aq.getQuestion().getCategory());
        dto.setTopic(aq.getQuestion().getTopic());
        dto.setTitle(aq.getQuestion().getTitle());
        dto.setStatus(aq.getStatus());
        dto.setMaxScore(aq.getQuestion().getMaxScore());
        dto.setScoreAwarded(aq.getScoreAwarded());
        dto.setPassedTestCases(aq.getPassedTestCases());
        dto.setTotalTestCases(aq.getTotalTestCases());
        return dto;
    }

    @Transactional(readOnly = true)
    public QuestionDetailDto getQuestionDetail(Long attemptId, int order) {
        AttemptQuestion aq = attemptQuestionRepository.findByAttemptIdAndQuestionOrder(attemptId, order)
                .orElseThrow(() -> new RuntimeException("Question " + order + " not found for attempt"));

        Question q = aq.getQuestion();
        QuestionDetailDto dto = new QuestionDetailDto();
        dto.setOrder(aq.getQuestionOrder());
        dto.setQuestionId(q.getId());
        dto.setCategory(q.getCategory());
        dto.setTopic(q.getTopic());
        dto.setDifficulty(q.getDifficulty());
        dto.setTitle(q.getTitle());
        dto.setDescription(q.getDescription());
        dto.setInputFormat(q.getInputFormat());
        dto.setOutputFormat(q.getOutputFormat());
        dto.setConstraints(q.getConstraints());
        dto.setStarterCode(q.getStarterCode());
        dto.setCurrentCode(aq.getCurrentCode() != null ? aq.getCurrentCode() : q.getStarterCode());
        dto.setStatus(aq.getStatus());
        dto.setMaxScore(q.getMaxScore());
        dto.setScoreAwarded(aq.getScoreAwarded());
        dto.setTimeLimitMs(q.getTimeLimitMs());
        dto.setMemoryLimitMb(q.getMemoryLimitMb());

        if ("JAVA".equals(q.getCategory())) {
            List<TestCaseDto> sampleTests = q.getTestCases().stream()
                    .filter(tc -> !tc.isHidden())
                    .map(tc -> new TestCaseDto(tc.getId(), tc.getInputData(), tc.getExpectedOutput(), false))
                    .collect(Collectors.toList());
            dto.setSampleTestCases(sampleTests);
        } else if ("SQL".equals(q.getCategory())) {
            SqlDataset ds = q.getSqlDataset();
            if (ds != null) {
                dto.setSqlSchemaDescription(ds.getSchemaDescription());
                dto.setSqlSampleOutput(ds.getSampleOutputJson());
            }
        } else if ("ENGLISH".equals(q.getCategory())) {
            EnglishEvaluation eval = aq.getAttempt().getEnglishEvaluation();
            if (eval != null) {
                dto.setAudioFilePath(eval.getAudioFilePath());
                dto.setTranscript(eval.getTranscript());
                dto.setEnglishEvaluation(new EnglishSubmitResponseDto(
                        eval.getGrammar(),
                        eval.getVocabulary(),
                        eval.getFluency(),
                        eval.getPronunciation(),
                        eval.getRelevance(),
                        eval.getStructure(),
                        eval.getTotalScore(),
                        eval.getFeedback(),
                        eval.getTranscript()
                ));
            }
        }

        return dto;
    }

    @Transactional
    public void saveCode(Long attemptId, int order, String code) {
        ExamAttempt attempt = examAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new RuntimeException("Attempt not found"));

        if (!"IN_PROGRESS".equals(attempt.getStatus())) {
            return;
        }

        AttemptQuestion aq = attemptQuestionRepository.findByAttemptIdAndQuestionOrder(attemptId, order)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        aq.setCurrentCode(code);
        if ("NOT_ATTEMPTED".equals(aq.getStatus())) {
            aq.setStatus("IN_PROGRESS");
        }
        attemptQuestionRepository.save(aq);
    }

    @Transactional
    public ExecutionResultDto runJava(Long attemptId, int order, JavaRunRequest request) {
        AttemptQuestion aq = attemptQuestionRepository.findByAttemptIdAndQuestionOrder(attemptId, order)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        aq.setCurrentCode(request.getCode());
        if ("NOT_ATTEMPTED".equals(aq.getStatus())) {
            aq.setStatus("IN_PROGRESS");
        }
        attemptQuestionRepository.save(aq);

        Question q = aq.getQuestion();
        List<TestCase> sampleTests = q.getTestCases().stream()
                .filter(tc -> !tc.isHidden())
                .collect(Collectors.toList());

        if (request.getCustomInput() != null && !request.getCustomInput().trim().isEmpty()) {
            sampleTests = Collections.singletonList(new TestCase(q, request.getCustomInput().trim(), "", false, 1));
        }

        return javaSandboxService.execute(request.getCode(), sampleTests, q.getMaxScore(), q.getTimeLimitMs());
    }

    @Transactional
    public ExecutionResultDto submitJava(Long attemptId, int order, JavaSubmitRequest request) {
        ExamAttempt attempt = examAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new RuntimeException("Attempt not found"));

        checkAndHandleExpiration(attempt);
        if (!"IN_PROGRESS".equals(attempt.getStatus())) {
            throw new RuntimeException("Assessment is no longer in progress.");
        }

        AttemptQuestion aq = attemptQuestionRepository.findByAttemptIdAndQuestionOrder(attemptId, order)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        Question q = aq.getQuestion();
        aq.setCurrentCode(request.getCode());

        ExecutionResultDto result = javaSandboxService.execute(request.getCode(), q.getTestCases(), q.getMaxScore(), q.getTimeLimitMs());

        aq.setPassedTestCases(result.getPassedTestCases());
        aq.setTotalTestCases(result.getTotalTestCases());
        aq.setScoreAwarded(result.getScoreAwarded());
        aq.setStatus(result.isPassed() ? "PASSED" : "FAILED");

        try {
            aq.setLastRunOutput(objectMapper.writeValueAsString(result));
        } catch (Exception ignored) {}

        attemptQuestionRepository.save(aq);
        recalculateAttemptScore(attempt);

        return result;
    }

    @Transactional
    public SqlExecutionResultDto runSql(Long attemptId, int order, SqlRunRequest request) {
        AttemptQuestion aq = attemptQuestionRepository.findByAttemptIdAndQuestionOrder(attemptId, order)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        aq.setCurrentCode(request.getQuery());
        if ("NOT_ATTEMPTED".equals(aq.getStatus())) {
            aq.setStatus("IN_PROGRESS");
        }
        attemptQuestionRepository.save(aq);

        Question q = aq.getQuestion();
        SqlDataset dataset = q.getSqlDataset();
        if (dataset == null) {
            throw new RuntimeException("Dataset not configured for question");
        }

        return sqlSandboxService.evaluate(request.getQuery(), dataset, q.getMaxScore(), false);
    }

    @Transactional
    public SqlExecutionResultDto submitSql(Long attemptId, int order, SqlSubmitRequest request) {
        ExamAttempt attempt = examAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new RuntimeException("Attempt not found"));

        checkAndHandleExpiration(attempt);
        if (!"IN_PROGRESS".equals(attempt.getStatus())) {
            throw new RuntimeException("Assessment is no longer in progress.");
        }

        AttemptQuestion aq = attemptQuestionRepository.findByAttemptIdAndQuestionOrder(attemptId, order)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        Question q = aq.getQuestion();
        SqlDataset dataset = q.getSqlDataset();
        if (dataset == null) {
            throw new RuntimeException("Dataset not configured for question");
        }

        aq.setCurrentCode(request.getQuery());

        SqlExecutionResultDto result = sqlSandboxService.evaluate(request.getQuery(), dataset, q.getMaxScore(), true);

        aq.setScoreAwarded(result.getScoreAwarded());
        aq.setStatus(result.isPassed() ? "PASSED" : "FAILED");
        aq.setPassedTestCases(result.isPassed() ? 1 : 0);
        aq.setTotalTestCases(1);

        try {
            aq.setLastRunOutput(objectMapper.writeValueAsString(result));
        } catch (Exception ignored) {}

        attemptQuestionRepository.save(aq);
        recalculateAttemptScore(attempt);

        return result;
    }

    @Transactional
    public EnglishSubmitResponseDto submitEnglish(Long attemptId, MultipartFile audioFile, String transcript, int durationSeconds) throws Exception {
        ExamAttempt attempt = examAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new RuntimeException("Attempt not found"));

        checkAndHandleExpiration(attempt);
        if (!"IN_PROGRESS".equals(attempt.getStatus())) {
            throw new RuntimeException("Assessment is no longer in progress.");
        }

        AttemptQuestion aq = attemptQuestionRepository.findByAttemptIdAndQuestionOrder(attemptId, 7)
                .orElseThrow(() -> new RuntimeException("English question not found"));

        String savedFileName = null;
        if (audioFile != null && !audioFile.isEmpty()) {
            savedFileName = englishEvaluationService.saveAudioFile(attemptId, audioFile);
        }

        EnglishSubmitResponseDto evalDto = englishEvaluationService.evaluate(
                transcript, aq.getQuestion().getDescription(), durationSeconds);

        EnglishEvaluation evaluation = attempt.getEnglishEvaluation();
        if (evaluation == null) {
            evaluation = new EnglishEvaluation();
            evaluation.setAttempt(attempt);
        }

        if (savedFileName != null) {
            evaluation.setAudioFilePath(savedFileName);
        }
        evaluation.setTranscript(evalDto.getTranscript());
        evaluation.setGrammar(evalDto.getGrammar());
        evaluation.setVocabulary(evalDto.getVocabulary());
        evaluation.setFluency(evalDto.getFluency());
        evaluation.setPronunciation(evalDto.getPronunciation());
        evaluation.setRelevance(evalDto.getRelevance());
        evaluation.setStructure(evalDto.getStructure());
        evaluation.setTotalScore(evalDto.getTotal());
        evaluation.setFeedback(evalDto.getFeedback());
        evaluation.setEvaluatedAt(LocalDateTime.now());

        englishEvaluationRepository.save(evaluation);
        attempt.setEnglishEvaluation(evaluation);

        aq.setCurrentCode(evalDto.getTranscript());
        aq.setScoreAwarded(evalDto.getTotal());
        aq.setStatus("SUBMITTED");
        aq.setPassedTestCases(1);
        aq.setTotalTestCases(1);
        attemptQuestionRepository.save(aq);

        recalculateAttemptScore(attempt);

        return evalDto;
    }

    @Transactional
    public FinalResultDto submitFinalExam(Long attemptId) {
        ExamAttempt attempt = examAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new RuntimeException("Attempt not found"));

        recalculateAttemptScore(attempt);
        attempt.setStatus("SUBMITTED");
        attempt.setSubmittedAt(LocalDateTime.now());
        examAttemptRepository.save(attempt);

        return getFinalResult(attemptId);
    }

    @Transactional(readOnly = true)
    public FinalResultDto getFinalResult(Long attemptId) {
        ExamAttempt attempt = examAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new RuntimeException("Attempt not found"));

        FinalResultDto result = new FinalResultDto();
        result.setAttemptId(attempt.getId());
        result.setCandidateName(attempt.getCandidateName());
        result.setCandidateEmail(attempt.getCandidateEmail());
        result.setTestCode(attempt.getTestCode());
        result.setAssessmentTitle(attempt.getAssessment() != null ? attempt.getAssessment().getTitle() : "Assessment");
        result.setStatus(attempt.getStatus());
        result.setStartedAt(attempt.getStartedAt());
        result.setSubmittedAt(attempt.getSubmittedAt());
        result.setJavaScore(attempt.getJavaScore());
        result.setSqlScore(attempt.getSqlScore());
        result.setEnglishScore(attempt.getEnglishScore());
        result.setTotalScore(attempt.getTotalScore());
        result.setViolationsCount(attempt.getViolations().size());

        List<QuestionSummaryDto> summaries = attempt.getAttemptQuestions().stream()
                .map(this::toSummaryDto)
                .collect(Collectors.toList());
        result.setQuestionSummaries(summaries);

        if (attempt.getEnglishEvaluation() != null) {
            EnglishEvaluation eval = attempt.getEnglishEvaluation();
            result.setEnglishEvaluation(new EnglishSubmitResponseDto(
                    eval.getGrammar(),
                    eval.getVocabulary(),
                    eval.getFluency(),
                    eval.getPronunciation(),
                    eval.getRelevance(),
                    eval.getStructure(),
                    eval.getTotalScore(),
                    eval.getFeedback(),
                    eval.getTranscript()
            ));
        }

        return result;
    }

    private void recalculateAttemptScore(ExamAttempt attempt) {
        double java = 0.0;
        double sql = 0.0;
        double english = 0.0;

        for (AttemptQuestion aq : attempt.getAttemptQuestions()) {
            if ("JAVA".equals(aq.getQuestion().getCategory())) {
                java += aq.getScoreAwarded();
            } else if ("SQL".equals(aq.getQuestion().getCategory())) {
                sql += aq.getScoreAwarded();
            } else if ("ENGLISH".equals(aq.getQuestion().getCategory())) {
                english += aq.getScoreAwarded();
            }
        }

        attempt.setJavaScore(Math.round(java * 10.0) / 10.0);
        attempt.setSqlScore(Math.round(sql * 10.0) / 10.0);
        attempt.setEnglishScore(Math.round(english * 10.0) / 10.0);
        attempt.setTotalScore(Math.round((java + sql + english) * 10.0) / 10.0);
        examAttemptRepository.save(attempt);
    }

    private void checkAndHandleExpiration(ExamAttempt attempt) {
        if ("IN_PROGRESS".equals(attempt.getStatus()) && LocalDateTime.now().isAfter(attempt.getExpiresAt())) {
            recalculateAttemptScore(attempt);
            attempt.setStatus("SUBMITTED");
            attempt.setSubmittedAt(attempt.getExpiresAt());
            examAttemptRepository.save(attempt);
        }
    }
}
