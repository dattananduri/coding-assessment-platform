package com.assessment;

import com.assessment.dto.ExecutionResultDto;
import com.assessment.dto.SqlExecutionResultDto;
import com.assessment.english.EnglishEvaluationService;
import com.assessment.model.SqlDataset;
import com.assessment.model.TestCase;
import com.assessment.sandbox.JavaSandboxService;
import com.assessment.sql.SqlSandboxService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CodingAssessmentApplicationTests {

    @Autowired
    private JavaSandboxService javaSandboxService;

    @Autowired
    private SqlSandboxService sqlSandboxService;

    @Autowired
    private EnglishEvaluationService englishEvaluationService;

    @Test
    void contextLoads() {
        assertNotNull(javaSandboxService);
        assertNotNull(sqlSandboxService);
        assertNotNull(englishEvaluationService);
    }

    @Test
    void testJavaSandboxValidExecution() {
        String code = """
                import java.util.Scanner;
                public class Solution {
                    public static void main(String[] args) {
                        Scanner sc = new Scanner(System.in);
                        int a = sc.nextInt();
                        int b = sc.nextInt();
                        System.out.println(a + b);
                    }
                }
                """;
        List<TestCase> testCases = Collections.singletonList(
                new TestCase(null, "5 7", "12", false, 1)
        );

        ExecutionResultDto result = javaSandboxService.execute(code, testCases, 10, 2000);
        assertEquals("ACCEPTED", result.getStatus());
        assertTrue(result.isPassed());
        assertEquals(1, result.getPassedTestCases());
        assertEquals(10.0, result.getScoreAwarded());
    }

    @Test
    void testJavaSandboxSecurityViolationBlocked() {
        String maliciousCode = """
                public class Solution {
                    public static void main(String[] args) throws Exception {
                        Runtime.getRuntime().exec("calc.exe");
                    }
                }
                """;
        List<TestCase> testCases = Collections.singletonList(
                new TestCase(null, "", "", false, 1)
        );

        ExecutionResultDto result = javaSandboxService.execute(maliciousCode, testCases, 10, 2000);
        assertEquals("SECURITY_VIOLATION", result.getStatus());
        assertFalse(result.isPassed());
    }

    @Test
    void testSqlSandboxEvaluation() {
        SqlDataset dataset = new SqlDataset();
        dataset.setSchemaDdl("CREATE TABLE candidates (id INT PRIMARY KEY, name VARCHAR(50), score INT);");
        dataset.setSeedDataSql("INSERT INTO candidates VALUES (1, 'Alice', 95), (2, 'Bob', 85);");
        dataset.setReferenceQuery("SELECT name, score FROM candidates WHERE score >= 90;");
        dataset.setOrderRequired(false);

        String candidateCorrect = "SELECT name, score FROM candidates WHERE score >= 90";
        SqlExecutionResultDto result = sqlSandboxService.evaluate(candidateCorrect, dataset, 10, true);

        assertTrue(result.isPassed());
        assertEquals("ACCEPTED", result.getStatus());
        assertEquals(10.0, result.getScoreAwarded());
    }

    @Test
    void testSqlSecurityViolationBlocked() {
        SqlDataset dataset = new SqlDataset();
        dataset.setSchemaDdl("CREATE TABLE t (id INT);");
        dataset.setSeedDataSql("INSERT INTO t VALUES (1);");
        dataset.setReferenceQuery("SELECT * FROM t;");

        String maliciousSql = "DROP TABLE t";
        SqlExecutionResultDto result = sqlSandboxService.evaluate(maliciousSql, dataset, 10, true);

        assertFalse(result.isPassed());
        assertEquals("SECURITY_VIOLATION", result.getStatus());
    }

    @Test
    void testEnglishEvaluationRubric() {
        String transcript = "For my final year project, our team designed and implemented an automated distributed microservice architecture. The primary challenge was database scalability and network latency under concurrent load. We integrated Redis caching and optimized database indexing to resolve the bottleneck.";
        var eval = englishEvaluationService.evaluate(transcript, "Explain your final-year project, technologies used, and challenges faced.", 45);

        assertNotNull(eval);
        assertTrue(eval.getTotal() > 20);
        assertTrue(eval.getGrammar() >= 6);
        assertTrue(eval.getVocabulary() >= 6);
        assertNotNull(eval.getFeedback());
    }
}
