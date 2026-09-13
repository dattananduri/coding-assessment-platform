package com.assessment.sandbox;

import com.assessment.dto.ExecutionResultDto;
import com.assessment.dto.TestCaseResultDto;
import com.assessment.model.TestCase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class JavaSandboxService {

    @Value("${app.sandbox.timeout-ms:3000}")
    private long defaultTimeoutMs;

    @Value("${app.sandbox.memory-limit-mb:128}")
    private int memoryLimitMb;

    private static final List<String> FORBIDDEN_KEYWORDS = Arrays.asList(
            "java.lang.reflect",
            "ProcessBuilder",
            "Runtime.getRuntime",
            "System.exit",
            "sun.misc.Unsafe",
            "java.net.Socket",
            "java.net.ServerSocket",
            "java.net.URL",
            "java.net.HttpURLConnection",
            "java.net.URI",
            "java.lang.management",
            "java.io.FileOutputStream",
            "java.io.RandomAccessFile",
            "java.nio.file.Files.delete",
            "java.nio.file.Files.write"
    );

    public ExecutionResultDto execute(String code, List<TestCase> testCases, int maxScore, int timeLimitMs) {
        ExecutionResultDto result = new ExecutionResultDto();
        result.setTotalTestCases(testCases != null ? testCases.size() : 0);

        if (code == null || code.trim().isEmpty()) {
            result.setStatus("COMPILE_ERROR");
            result.setPassed(false);
            result.setError("Source code cannot be empty.");
            return result;
        }

        // 1. Security Static Analysis
        String securityViolation = checkSecurityViolations(code);
        if (securityViolation != null) {
            result.setStatus("SECURITY_VIOLATION");
            result.setPassed(false);
            result.setError(securityViolation);
            return result;
        }

        // 2. Extract or Normalize Main Class Name
        String className = extractClassName(code);
        if (className == null) {
            className = "Solution";
            code = "public class " + className + " {\n" + code + "\n}";
        }

        Path tempDir = null;
        try {
            tempDir = Files.createTempDirectory("java_sandbox_" + System.currentTimeMillis());
            Path sourceFile = tempDir.resolve(className + ".java");
            Files.writeString(sourceFile, code, StandardCharsets.UTF_8);

            // 3. Compile
            CompilationResult compilation = compileJava(tempDir, className);
            if (!compilation.isSuccess()) {
                result.setStatus("COMPILATION_ERROR");
                result.setPassed(false);
                result.setError(compilation.getErrorMessage());
                return result;
            }

            // 4. Run Test Cases
            List<TestCaseResultDto> testCaseResults = new ArrayList<>();
            int passedCount = 0;
            long totalExecutionTime = 0;
            String overallStatus = "ACCEPTED";
            String firstError = null;

            int actualTimeout = timeLimitMs > 0 ? timeLimitMs : (int) defaultTimeoutMs;

            if (testCases == null || testCases.isEmpty()) {
                // Run once with empty input
                RunResult runResult = runTestCase(tempDir, className, "", actualTimeout);
                result.setStatus(runResult.getStatus());
                result.setPassed("ACCEPTED".equals(runResult.getStatus()));
                result.setOutput(runResult.getOutput());
                result.setError(runResult.getError());
                result.setExecutionTimeMs(runResult.getExecutionTimeMs());
                return result;
            }

            for (int i = 0; i < testCases.size(); i++) {
                TestCase tc = testCases.get(i);
                RunResult runResult = runTestCase(tempDir, className, tc.getInputData(), actualTimeout);
                totalExecutionTime += runResult.getExecutionTimeMs();

                boolean testPassed = false;
                String normalizedActual = normalizeOutput(runResult.getOutput());
                String normalizedExpected = normalizeOutput(tc.getExpectedOutput());

                if ("ACCEPTED".equals(runResult.getStatus())) {
                    if (normalizedActual.equals(normalizedExpected)) {
                        testPassed = true;
                        passedCount++;
                    } else {
                        if ("ACCEPTED".equals(overallStatus)) {
                            overallStatus = "WRONG_ANSWER";
                        }
                    }
                } else {
                    if ("ACCEPTED".equals(overallStatus) || "WRONG_ANSWER".equals(overallStatus)) {
                        overallStatus = runResult.getStatus();
                    }
                    if (firstError == null) {
                        firstError = runResult.getError();
                    }
                }

                TestCaseResultDto tcDto = new TestCaseResultDto(
                        i + 1,
                        tc.isHidden(),
                        tc.isHidden() ? "(Hidden Input)" : tc.getInputData(),
                        tc.isHidden() ? "(Hidden Expected Output)" : tc.getExpectedOutput(),
                        tc.isHidden() && !testPassed ? "(Output Hidden for Test Case)" : runResult.getOutput(),
                        testPassed,
                        runResult.getExecutionTimeMs(),
                        runResult.getError()
                );
                testCaseResults.add(tcDto);
            }

            result.setPassedTestCases(passedCount);
            result.setTestCaseResults(testCaseResults);
            result.setExecutionTimeMs(totalExecutionTime);
            result.setStatus(passedCount == testCases.size() ? "ACCEPTED" : overallStatus);
            result.setPassed(passedCount == testCases.size());
            result.setError(firstError);

            double score = testCases.isEmpty() ? 0 : ((double) passedCount / testCases.size()) * maxScore;
            result.setScoreAwarded(Math.round(score * 10.0) / 10.0);

        } catch (Exception e) {
            result.setStatus("RUNTIME_ERROR");
            result.setPassed(false);
            result.setError("Execution environment error: " + e.getMessage());
        } finally {
            cleanupDirectory(tempDir);
        }

        return result;
    }

    private String checkSecurityViolations(String code) {
        if (code == null) return null;
        for (String forbidden : FORBIDDEN_KEYWORDS) {
            if (code.contains(forbidden)) {
                return "Security Policy Violation: Use of restricted operation or package '" + forbidden + "' is prohibited.";
            }
        }
        if (code.contains("native ")) {
            return "Security Policy Violation: Native methods are not permitted.";
        }
        return null;
    }

    private String extractClassName(String code) {
        if (code == null) return null;
        Pattern pattern = Pattern.compile("public\\s+class\\s+([a-zA-Z0-9_]+)");
        Matcher matcher = pattern.matcher(code);
        if (matcher.find()) {
            return matcher.group(1);
        }
        Pattern patternClass = Pattern.compile("class\\s+([a-zA-Z0-9_]+)");
        Matcher matcherClass = patternClass.matcher(code);
        if (matcherClass.find()) {
            return matcherClass.group(1);
        }
        return null;
    }

    private CompilationResult compileJava(Path dir, String className) {
        try {
            ProcessBuilder pb = new ProcessBuilder("javac", "-J-Xmx128m", "-encoding", "UTF-8", className + ".java");
            pb.directory(dir.toFile());
            Process process = pb.start();

            boolean finished = process.waitFor(10, TimeUnit.SECONDS);
            if (!finished) {
                process.destroyForcibly();
                return new CompilationResult(false, "Compilation timed out after 10 seconds.");
            }

            if (process.exitValue() != 0) {
                String error = readStream(process.getErrorStream(), 16384);
                return new CompilationResult(false, error.isEmpty() ? "Compilation failed with unknown error." : error);
            }

            return new CompilationResult(true, null);
        } catch (Exception e) {
            return new CompilationResult(false, "Failed to invoke Java compiler: " + e.getMessage());
        }
    }

    private RunResult runTestCase(Path dir, String className, String inputData, int timeoutMs) {
        long startTime = System.currentTimeMillis();
        Process process = null;
        try {
            List<String> command = new ArrayList<>(Arrays.asList(
                    "java",
                    "-Xmx" + memoryLimitMb + "m",
                    "-Xms16m",
                    "-XX:+UseSerialGC",
                    "-Dfile.encoding=UTF-8",
                    "-cp", ".",
                    className
            ));

            ProcessBuilder pb = new ProcessBuilder(command);
            pb.directory(dir.toFile());
            process = pb.start();

            // Write input
            if (inputData != null && !inputData.isEmpty()) {
                try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream(), StandardCharsets.UTF_8))) {
                    writer.write(inputData);
                    if (!inputData.endsWith("\n")) {
                        writer.newLine();
                    }
                    writer.flush();
                } catch (IOException ignored) {}
            } else {
                process.getOutputStream().close();
            }

            boolean finished = process.waitFor(timeoutMs, TimeUnit.MILLISECONDS);
            long duration = System.currentTimeMillis() - startTime;

            if (!finished) {
                killProcessTree(process);
                return new RunResult("TIME_LIMIT_EXCEEDED", "", "Time Limit Exceeded (" + timeoutMs + " ms)", duration);
            }

            String stdout = readStream(process.getInputStream(), 65536);
            String stderr = readStream(process.getErrorStream(), 16384);

            if (process.exitValue() != 0) {
                if (stderr.contains("OutOfMemoryError")) {
                    return new RunResult("MEMORY_LIMIT_EXCEEDED", stdout, "Memory Limit Exceeded (" + memoryLimitMb + " MB)", duration);
                }
                return new RunResult("RUNTIME_ERROR", stdout, stderr.isEmpty() ? "Runtime Error (exit code " + process.exitValue() + ")" : stderr, duration);
            }

            return new RunResult("ACCEPTED", stdout, null, duration);

        } catch (Exception e) {
            return new RunResult("RUNTIME_ERROR", "", "Execution exception: " + e.getMessage(), System.currentTimeMillis() - startTime);
        } finally {
            if (process != null && process.isAlive()) {
                killProcessTree(process);
            }
        }
    }

    private void killProcessTree(Process process) {
        try {
            process.descendants().forEach(ProcessHandle::destroyForcibly);
            process.destroyForcibly();
        } catch (Exception ignored) {}
    }

    private String readStream(InputStream is, int maxBytes) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int read;
        int total = 0;
        while ((read = is.read(buffer)) != -1) {
            baos.write(buffer, 0, read);
            total += read;
            if (total >= maxBytes) {
                baos.write("\n... [Output Truncated]".getBytes(StandardCharsets.UTF_8));
                break;
            }
        }
        return baos.toString(StandardCharsets.UTF_8);
    }

    private String normalizeOutput(String s) {
        if (s == null) return "";
        return s.replace("\r\n", "\n").trim();
    }

    private void cleanupDirectory(Path dir) {
        if (dir == null || !Files.exists(dir)) return;
        try {
            Files.walk(dir)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        } catch (Exception ignored) {}
    }

    private static class CompilationResult {
        private final boolean success;
        private final String errorMessage;

        public CompilationResult(boolean success, String errorMessage) {
            this.success = success;
            this.errorMessage = errorMessage;
        }

        public boolean isSuccess() { return success; }
        public String getErrorMessage() { return errorMessage; }
    }

    private static class RunResult {
        private final String status;
        private final String output;
        private final String error;
        private final long executionTimeMs;

        public RunResult(String status, String output, String error, long executionTimeMs) {
            this.status = status;
            this.output = output;
            this.error = error;
            this.executionTimeMs = executionTimeMs;
        }

        public String getStatus() { return status; }
        public String getOutput() { return output; }
        public String getError() { return error; }
        public long getExecutionTimeMs() { return executionTimeMs; }
    }
}
