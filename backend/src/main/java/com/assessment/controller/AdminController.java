package com.assessment.controller;

import com.assessment.dto.*;
import com.assessment.model.*;
import com.assessment.repository.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AssessmentRepository assessmentRepository;
    private final QuestionRepository questionRepository;
    private final TestCaseRepository testCaseRepository;
    private final SqlDatasetRepository sqlDatasetRepository;
    private final ExamAttemptRepository examAttemptRepository;
    private final AttemptQuestionRepository attemptQuestionRepository;
    private final ViolationLogRepository violationLogRepository;

    public AdminController(
            AssessmentRepository assessmentRepository,
            QuestionRepository questionRepository,
            TestCaseRepository testCaseRepository,
            SqlDatasetRepository sqlDatasetRepository,
            ExamAttemptRepository examAttemptRepository,
            AttemptQuestionRepository attemptQuestionRepository,
            ViolationLogRepository violationLogRepository) {
        this.assessmentRepository = assessmentRepository;
        this.questionRepository = questionRepository;
        this.testCaseRepository = testCaseRepository;
        this.sqlDatasetRepository = sqlDatasetRepository;
        this.examAttemptRepository = examAttemptRepository;
        this.attemptQuestionRepository = attemptQuestionRepository;
        this.violationLogRepository = violationLogRepository;
    }

    // --- Assessments ---

    @GetMapping("/assessments")
    public ResponseEntity<List<Assessment>> getAllAssessments() {
        return ResponseEntity.ok(assessmentRepository.findAll());
    }

    @PostMapping("/assessments")
    public ResponseEntity<Assessment> createAssessment(@RequestBody AdminAssessmentCreateDto dto) {
        Assessment assessment = new Assessment(
                dto.getTitle(),
                dto.getCode().toUpperCase(Locale.ROOT),
                dto.getDescription(),
                dto.getDurationMinutes() > 0 ? dto.getDurationMinutes() : 90
        );
        return ResponseEntity.ok(assessmentRepository.save(assessment));
    }

    // --- Questions ---

    @GetMapping("/questions")
    public ResponseEntity<List<AdminQuestionDto>> getQuestions(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String topic) {
        List<Question> questions;
        if (category != null && topic != null) {
            questions = questionRepository.findByCategoryAndTopic(category.toUpperCase(), topic.toUpperCase());
        } else if (category != null) {
            questions = questionRepository.findByCategory(category.toUpperCase());
        } else {
            questions = questionRepository.findAll();
        }

        List<AdminQuestionDto> dtos = questions.stream().map(this::toAdminQuestionDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/questions/{id}")
    public ResponseEntity<AdminQuestionDto> getQuestionById(@PathVariable Long id) {
        Question q = questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found"));
        return ResponseEntity.ok(toAdminQuestionDto(q));
    }

    @PostMapping("/questions")
    @Transactional
    public ResponseEntity<AdminQuestionDto> createQuestion(@RequestBody AdminQuestionDto dto) {
        Question q = new Question();
        q.setCategory(dto.getCategory().toUpperCase());
        q.setTopic(dto.getTopic().toUpperCase());
        q.setDifficulty(dto.getDifficulty() != null ? dto.getDifficulty().toUpperCase() : "MEDIUM");
        q.setTitle(dto.getTitle());
        q.setDescription(dto.getDescription());
        q.setInputFormat(dto.getInputFormat());
        q.setOutputFormat(dto.getOutputFormat());
        q.setConstraints(dto.getConstraints());
        q.setStarterCode(dto.getStarterCode());
        q.setTimeLimitMs(dto.getTimeLimitMs() > 0 ? dto.getTimeLimitMs() : 2000);
        q.setMemoryLimitMb(dto.getMemoryLimitMb() > 0 ? dto.getMemoryLimitMb() : 128);
        q.setMaxScore(dto.getMaxScore() > 0 ? dto.getMaxScore() : 10);

        q = questionRepository.save(q);

        if ("JAVA".equalsIgnoreCase(dto.getCategory()) && dto.getTestCases() != null) {
            for (TestCaseDto tcDto : dto.getTestCases()) {
                TestCase tc = new TestCase(q, tcDto.getInputData(), tcDto.getExpectedOutput(), tcDto.isHidden(), 1);
                q.getTestCases().add(tc);
            }
        } else if ("SQL".equalsIgnoreCase(dto.getCategory())) {
            SqlDataset ds = new SqlDataset(
                    q,
                    dto.getSchemaDescription(),
                    dto.getSchemaDdl(),
                    dto.getSeedDataSql(),
                    dto.getReferenceQuery(),
                    dto.isOrderRequired(),
                    dto.getSampleOutputJson()
            );
            q.setSqlDataset(ds);
        }

        q = questionRepository.save(q);
        return ResponseEntity.ok(toAdminQuestionDto(q));
    }

    @PutMapping("/questions/{id}")
    @Transactional
    public ResponseEntity<AdminQuestionDto> updateQuestion(@PathVariable Long id, @RequestBody AdminQuestionDto dto) {
        Question q = questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        q.setCategory(dto.getCategory().toUpperCase());
        q.setTopic(dto.getTopic().toUpperCase());
        q.setDifficulty(dto.getDifficulty() != null ? dto.getDifficulty().toUpperCase() : "MEDIUM");
        q.setTitle(dto.getTitle());
        q.setDescription(dto.getDescription());
        q.setInputFormat(dto.getInputFormat());
        q.setOutputFormat(dto.getOutputFormat());
        q.setConstraints(dto.getConstraints());
        q.setStarterCode(dto.getStarterCode());
        q.setTimeLimitMs(dto.getTimeLimitMs());
        q.setMemoryLimitMb(dto.getMemoryLimitMb());
        q.setMaxScore(dto.getMaxScore());

        if ("JAVA".equalsIgnoreCase(dto.getCategory()) && dto.getTestCases() != null) {
            q.getTestCases().clear();
            for (TestCaseDto tcDto : dto.getTestCases()) {
                TestCase tc = new TestCase(q, tcDto.getInputData(), tcDto.getExpectedOutput(), tcDto.isHidden(), 1);
                q.getTestCases().add(tc);
            }
        } else if ("SQL".equalsIgnoreCase(dto.getCategory())) {
            SqlDataset ds = q.getSqlDataset();
            if (ds == null) {
                ds = new SqlDataset();
                ds.setQuestion(q);
            }
            ds.setSchemaDescription(dto.getSchemaDescription());
            ds.setSchemaDdl(dto.getSchemaDdl());
            ds.setSeedDataSql(dto.getSeedDataSql());
            ds.setReferenceQuery(dto.getReferenceQuery());
            ds.setOrderRequired(dto.isOrderRequired());
            ds.setSampleOutputJson(dto.getSampleOutputJson());
            q.setSqlDataset(ds);
        }

        q = questionRepository.save(q);
        return ResponseEntity.ok(toAdminQuestionDto(q));
    }

    @DeleteMapping("/questions/{id}")
    public ResponseEntity<?> deleteQuestion(@PathVariable Long id) {
        questionRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Question deleted successfully"));
    }

    // --- Attempts & Submissions ---

    @GetMapping("/attempts")
    public ResponseEntity<List<Map<String, Object>>> getAttempts(
            @RequestParam(required = false) String testCode) {
        List<ExamAttempt> attempts;
        if (testCode != null) {
            attempts = examAttemptRepository.findByTestCodeOrderByStartedAtDesc(testCode);
        } else {
            attempts = examAttemptRepository.findAllByOrderByStartedAtDesc();
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (ExamAttempt a : attempts) {
            Map<String, Object> map = new HashMap<>();
            map.put("attemptId", a.getId());
            map.put("candidateName", a.getCandidateName());
            map.put("candidateEmail", a.getCandidateEmail());
            map.put("testCode", a.getTestCode());
            map.put("status", a.getStatus());
            map.put("startedAt", a.getStartedAt());
            map.put("expiresAt", a.getExpiresAt());
            map.put("submittedAt", a.getSubmittedAt());
            map.put("javaScore", a.getJavaScore());
            map.put("sqlScore", a.getSqlScore());
            map.put("englishScore", a.getEnglishScore());
            map.put("totalScore", a.getTotalScore());
            map.put("violationsCount", a.getViolations().size());
            result.add(map);
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/attempts/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<AdminAttemptDetailDto> getAttemptDetail(@PathVariable Long id) {
        ExamAttempt attempt = examAttemptRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Attempt not found"));

        AdminAttemptDetailDto dto = new AdminAttemptDetailDto();
        dto.setAttemptId(attempt.getId());
        dto.setCandidateName(attempt.getCandidateName());
        dto.setCandidateEmail(attempt.getCandidateEmail());
        dto.setTestCode(attempt.getTestCode());
        dto.setAssessmentTitle(attempt.getAssessment() != null ? attempt.getAssessment().getTitle() : "Assessment");
        dto.setStatus(attempt.getStatus());
        dto.setStartedAt(attempt.getStartedAt());
        dto.setExpiresAt(attempt.getExpiresAt());
        dto.setSubmittedAt(attempt.getSubmittedAt());
        dto.setJavaScore(attempt.getJavaScore());
        dto.setSqlScore(attempt.getSqlScore());
        dto.setEnglishScore(attempt.getEnglishScore());
        dto.setTotalScore(attempt.getTotalScore());

        List<AttemptQuestionDetailDto> qDtos = attempt.getAttemptQuestions().stream()
                .map(aq -> {
                    AttemptQuestionDetailDto qd = new AttemptQuestionDetailDto();
                    qd.setOrder(aq.getQuestionOrder());
                    qd.setQuestionId(aq.getQuestion().getId());
                    qd.setCategory(aq.getQuestion().getCategory());
                    qd.setTopic(aq.getQuestion().getTopic());
                    qd.setTitle(aq.getQuestion().getTitle());
                    qd.setStatus(aq.getStatus());
                    qd.setMaxScore(aq.getQuestion().getMaxScore());
                    qd.setScoreAwarded(aq.getScoreAwarded());
                    qd.setSubmittedCode(aq.getCurrentCode());
                    qd.setPassedTestCases(aq.getPassedTestCases());
                    qd.setTotalTestCases(aq.getTotalTestCases());
                    qd.setLastRunOutput(aq.getLastRunOutput());
                    return qd;
                })
                .collect(Collectors.toList());
        dto.setQuestions(qDtos);

        if (attempt.getEnglishEvaluation() != null) {
            EnglishEvaluation eval = attempt.getEnglishEvaluation();
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
            if (eval.getAudioFilePath() != null) {
                dto.setEnglishAudioUrl("/recordings/" + eval.getAudioFilePath());
            }
        }

        List<ViolationDto> violationDtos = attempt.getViolations().stream()
                .map(v -> new ViolationDto(v.getId(), v.getViolationType(), v.getTimestamp(), v.getDetails()))
                .collect(Collectors.toList());
        dto.setViolations(violationDtos);

        return ResponseEntity.ok(dto);
    }

    @GetMapping(value = "/export/csv", produces = "text/csv")
    public ResponseEntity<byte[]> exportCsv() {
        List<ExamAttempt> attempts = examAttemptRepository.findAllByOrderByStartedAtDesc();
        StringBuilder sb = new StringBuilder();
        sb.append("Attempt ID,Candidate Name,Candidate Email,Test Code,Status,Started At,Submitted At,Java Score,SQL Score,English Score,Total Score,Violations\n");

        for (ExamAttempt a : attempts) {
            sb.append(a.getId()).append(",")
                    .append(escapeCsv(a.getCandidateName())).append(",")
                    .append(escapeCsv(a.getCandidateEmail())).append(",")
                    .append(escapeCsv(a.getTestCode())).append(",")
                    .append(a.getStatus()).append(",")
                    .append(a.getStartedAt()).append(",")
                    .append(a.getSubmittedAt() != null ? a.getSubmittedAt() : "").append(",")
                    .append(a.getJavaScore()).append(",")
                    .append(a.getSqlScore()).append(",")
                    .append(a.getEnglishScore()).append(",")
                    .append(a.getTotalScore()).append(",")
                    .append(a.getViolations().size()).append("\n");
        }

        byte[] bytes = sb.toString().getBytes(StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=assessment_results.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(bytes);
    }

    private String escapeCsv(String val) {
        if (val == null) return "";
        if (val.contains(",") || val.contains("\"") || val.contains("\n")) {
            return "\"" + val.replace("\"", "\"\"") + "\"";
        }
        return val;
    }

    private AdminQuestionDto toAdminQuestionDto(Question q) {
        AdminQuestionDto dto = new AdminQuestionDto();
        dto.setId(q.getId());
        dto.setCategory(q.getCategory());
        dto.setTopic(q.getTopic());
        dto.setDifficulty(q.getDifficulty());
        dto.setTitle(q.getTitle());
        dto.setDescription(q.getDescription());
        dto.setInputFormat(q.getInputFormat());
        dto.setOutputFormat(q.getOutputFormat());
        dto.setConstraints(q.getConstraints());
        dto.setStarterCode(q.getStarterCode());
        dto.setTimeLimitMs(q.getTimeLimitMs());
        dto.setMemoryLimitMb(q.getMemoryLimitMb());
        dto.setMaxScore(q.getMaxScore());

        if (q.getTestCases() != null) {
            List<TestCaseDto> tcDtos = q.getTestCases().stream()
                    .map(tc -> new TestCaseDto(tc.getId(), tc.getInputData(), tc.getExpectedOutput(), tc.isHidden()))
                    .collect(Collectors.toList());
            dto.setTestCases(tcDtos);
        }

        if (q.getSqlDataset() != null) {
            SqlDataset ds = q.getSqlDataset();
            dto.setSchemaDescription(ds.getSchemaDescription());
            dto.setSchemaDdl(ds.getSchemaDdl());
            dto.setSeedDataSql(ds.getSeedDataSql());
            dto.setReferenceQuery(ds.getReferenceQuery());
            dto.setOrderRequired(ds.isOrderRequired());
            dto.setSampleOutputJson(ds.getSampleOutputJson());
        }

        return dto;
    }
}
