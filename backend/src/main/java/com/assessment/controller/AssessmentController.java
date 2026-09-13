package com.assessment.controller;

import com.assessment.dto.*;
import com.assessment.model.Assessment;
import com.assessment.model.ViolationLog;
import com.assessment.service.AssessmentService;
import com.assessment.service.ViolationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/assessment")
public class AssessmentController {

    private final AssessmentService assessmentService;
    private final ViolationService violationService;

    public AssessmentController(AssessmentService assessmentService, ViolationService violationService) {
        this.assessmentService = assessmentService;
        this.violationService = violationService;
    }

    @GetMapping("/info/{code}")
    public ResponseEntity<?> getAssessmentInfo(@PathVariable String code) {
        Assessment assessment = assessmentService.getAssessmentByCode(code);
        Map<String, Object> response = new HashMap<>();
        response.put("title", assessment.getTitle());
        response.put("code", assessment.getCode());
        response.put("description", assessment.getDescription());
        response.put("durationMinutes", assessment.getDurationMinutes());
        response.put("active", assessment.isActive());
        response.put("totalQuestions", 7);
        response.put("javaQuestions", 3);
        response.put("sqlQuestions", 3);
        response.put("englishQuestions", 1);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/start")
    public ResponseEntity<CandidateStartResponse> startExam(@RequestBody CandidateStartRequest request) {
        CandidateStartResponse response = assessmentService.startExam(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/attempts/{attemptId}/state")
    public ResponseEntity<AttemptStateDto> getAttemptState(@PathVariable Long attemptId) {
        AttemptStateDto state = assessmentService.getAttemptState(attemptId);
        return ResponseEntity.ok(state);
    }

    @GetMapping("/attempts/{attemptId}/questions/{order}")
    public ResponseEntity<QuestionDetailDto> getQuestionDetail(
            @PathVariable Long attemptId,
            @PathVariable int order) {
        QuestionDetailDto detail = assessmentService.getQuestionDetail(attemptId, order);
        return ResponseEntity.ok(detail);
    }

    @PostMapping("/attempts/{attemptId}/questions/{order}/save")
    public ResponseEntity<?> saveCode(
            @PathVariable Long attemptId,
            @PathVariable int order,
            @RequestBody SaveCodeRequest request) {
        assessmentService.saveCode(attemptId, order, request.getCode());
        return ResponseEntity.ok(Map.of("message", "Saved successfully"));
    }

    @PostMapping("/attempts/{attemptId}/questions/{order}/run-java")
    public ResponseEntity<ExecutionResultDto> runJava(
            @PathVariable Long attemptId,
            @PathVariable int order,
            @RequestBody JavaRunRequest request) {
        ExecutionResultDto result = assessmentService.runJava(attemptId, order, request);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/attempts/{attemptId}/questions/{order}/submit-java")
    public ResponseEntity<ExecutionResultDto> submitJava(
            @PathVariable Long attemptId,
            @PathVariable int order,
            @RequestBody JavaSubmitRequest request) {
        ExecutionResultDto result = assessmentService.submitJava(attemptId, order, request);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/attempts/{attemptId}/questions/{order}/run-sql")
    public ResponseEntity<SqlExecutionResultDto> runSql(
            @PathVariable Long attemptId,
            @PathVariable int order,
            @RequestBody SqlRunRequest request) {
        SqlExecutionResultDto result = assessmentService.runSql(attemptId, order, request);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/attempts/{attemptId}/questions/{order}/submit-sql")
    public ResponseEntity<SqlExecutionResultDto> submitSql(
            @PathVariable Long attemptId,
            @PathVariable int order,
            @RequestBody SqlSubmitRequest request) {
        SqlExecutionResultDto result = assessmentService.submitSql(attemptId, order, request);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/attempts/{attemptId}/english")
    public ResponseEntity<EnglishSubmitResponseDto> submitEnglish(
            @PathVariable Long attemptId,
            @RequestParam(value = "audio", required = false) MultipartFile audioFile,
            @RequestParam(value = "transcript", required = false, defaultValue = "") String transcript,
            @RequestParam(value = "durationSeconds", required = false, defaultValue = "0") int durationSeconds) throws Exception {
        EnglishSubmitResponseDto result = assessmentService.submitEnglish(attemptId, audioFile, transcript, durationSeconds);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/attempts/{attemptId}/violations")
    public ResponseEntity<?> recordViolation(
            @PathVariable Long attemptId,
            @RequestBody ViolationReportRequest request,
            @RequestParam(value = "terminate", defaultValue = "false") boolean terminate) {
        ViolationLog log = violationService.recordViolation(attemptId, request, terminate);
        return ResponseEntity.ok(Map.of(
                "violationId", log.getId(),
                "violationType", log.getViolationType(),
                "timestamp", log.getTimestamp(),
                "terminated", terminate
        ));
    }

    @PostMapping("/attempts/{attemptId}/final-submit")
    public ResponseEntity<FinalResultDto> submitFinalExam(@PathVariable Long attemptId) {
        FinalResultDto result = assessmentService.submitFinalExam(attemptId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/attempts/{attemptId}/result")
    public ResponseEntity<FinalResultDto> getFinalResult(@PathVariable Long attemptId) {
        FinalResultDto result = assessmentService.getFinalResult(attemptId);
        return ResponseEntity.ok(result);
    }
}
