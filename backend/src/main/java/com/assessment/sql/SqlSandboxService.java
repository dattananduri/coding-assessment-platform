package com.assessment.sql;

import com.assessment.dto.SqlExecutionResultDto;
import com.assessment.model.SqlDataset;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class SqlSandboxService {

    private static final Pattern FORBIDDEN_SQL = Pattern.compile(
            "\\b(DROP|ALTER|TRUNCATE|CREATE|INSERT|UPDATE|DELETE|GRANT|REVOKE|SHUTDOWN|SCRIPT|FILE_READ|FILE_WRITE|CSVREAD|CSVWRITE|LINK_SCHEMA|EXEC|CALL)\\b",
            Pattern.CASE_INSENSITIVE
    );

    public SqlExecutionResultDto evaluate(String candidateQuery, SqlDataset dataset, int maxScore, boolean isSubmit) {
        SqlExecutionResultDto result = new SqlExecutionResultDto();

        if (candidateQuery == null || candidateQuery.trim().isEmpty()) {
            result.setStatus("SYNTAX_ERROR");
            result.setPassed(false);
            result.setError("Query cannot be empty.");
            return result;
        }

        // 1. Security Check
        String securityViolation = validateQuerySecurity(candidateQuery);
        if (securityViolation != null) {
            result.setStatus("SECURITY_VIOLATION");
            result.setPassed(false);
            result.setError(securityViolation);
            return result;
        }

        long startTime = System.currentTimeMillis();
        String sandboxDbName = "sql_sandbox_" + UUID.randomUUID().toString().replace("-", "");
        String jdbcUrl = "jdbc:h2:mem:" + sandboxDbName + ";MODE=PostgreSQL;DB_CLOSE_DELAY=-1";

        try (Connection conn = DriverManager.getConnection(jdbcUrl, "sa", "")) {
            // Apply Schema DDL
            try (Statement stmt = conn.createStatement()) {
                stmt.setQueryTimeout(5);
                executeMultiStatement(stmt, dataset.getSchemaDdl());
                executeMultiStatement(stmt, dataset.getSeedDataSql());
            } catch (SQLException e) {
                result.setStatus("SYNTAX_ERROR");
                result.setPassed(false);
                result.setError("Failed to initialize test dataset: " + e.getMessage());
                return result;
            }

            // Execute Expected Reference Query
            QueryResult expectedResult;
            try (Statement stmt = conn.createStatement()) {
                stmt.setQueryTimeout(5);
                try (ResultSet rs = stmt.executeQuery(dataset.getReferenceQuery())) {
                    expectedResult = extractQueryResult(rs);
                }
            } catch (SQLException e) {
                result.setStatus("SYNTAX_ERROR");
                result.setPassed(false);
                result.setError("Error executing reference query: " + e.getMessage());
                return result;
            }

            // Execute Candidate Query
            QueryResult candidateResult;
            try (Statement stmt = conn.createStatement()) {
                stmt.setQueryTimeout(3);
                try (ResultSet rs = stmt.executeQuery(candidateQuery)) {
                    candidateResult = extractQueryResult(rs);
                }
            } catch (SQLException e) {
                result.setStatus("SYNTAX_ERROR");
                result.setPassed(false);
                result.setError("SQL execution error: " + e.getMessage());
                result.setExecutionTimeMs(System.currentTimeMillis() - startTime);
                return result;
            }

            long executionTime = System.currentTimeMillis() - startTime;
            result.setExecutionTimeMs(executionTime);

            result.setCandidateColumns(candidateResult.getColumns());
            result.setCandidateRows(candidateResult.getRows());
            result.setExpectedColumns(expectedResult.getColumns());
            result.setExpectedRows(expectedResult.getRows());

            // Compare Results
            boolean passed = compareResults(candidateResult, expectedResult, dataset.isOrderRequired());
            result.setPassed(passed);

            if (passed) {
                result.setStatus("ACCEPTED");
                result.setMessage("Correct! Your query produced the expected result set.");
                if (isSubmit) {
                    result.setScoreAwarded(maxScore);
                }
            } else {
                result.setStatus("WRONG_ANSWER");
                result.setMessage(generateMismatchMessage(candidateResult, expectedResult));
                result.setScoreAwarded(0);
            }

        } catch (SQLException e) {
            result.setStatus("SYNTAX_ERROR");
            result.setPassed(false);
            result.setError("Database engine error: " + e.getMessage());
            result.setExecutionTimeMs(System.currentTimeMillis() - startTime);
        }

        return result;
    }

    private String validateQuerySecurity(String query) {
        String trimmed = query.trim();

        // Check for multiple queries separated by semicolon
        String withoutStrings = trimmed.replaceAll("'[^']*'", "''");
        int firstSemi = withoutStrings.indexOf(';');
        if (firstSemi != -1 && firstSemi < withoutStrings.length() - 1) {
            String remainder = withoutStrings.substring(firstSemi + 1).trim();
            if (!remainder.isEmpty()) {
                return "Security Violation: Multiple SQL statements are not permitted.";
            }
        }

        // Check for forbidden keywords
        if (FORBIDDEN_SQL.matcher(query).find()) {
            return "Security Violation: Data modification, DDL, or file manipulation statements are prohibited. Only SELECT queries are permitted.";
        }

        // Strip single line comments and multi-line comments for start check
        String stripped = trimmed.replaceAll("(?m)^--.*$", "").replaceAll("/\\*.*?\\*/", "").trim();
        String upper = stripped.toUpperCase(Locale.ROOT);
        if (!upper.startsWith("SELECT") && !upper.startsWith("WITH")) {
            return "Security Violation: Only SELECT or WITH queries are permitted.";
        }

        return null;
    }

    private void executeMultiStatement(Statement stmt, String sqlScript) throws SQLException {
        if (sqlScript == null || sqlScript.trim().isEmpty()) return;
        String[] statements = sqlScript.split(";");
        for (String sql : statements) {
            String clean = sql.trim();
            if (!clean.isEmpty()) {
                stmt.execute(clean);
            }
        }
    }

    private QueryResult extractQueryResult(ResultSet rs) throws SQLException {
        ResultSetMetaData meta = rs.getMetaData();
        int colCount = meta.getColumnCount();
        List<String> columns = new ArrayList<>();
        for (int i = 1; i <= colCount; i++) {
            columns.add(meta.getColumnLabel(i).toUpperCase(Locale.ROOT));
        }

        List<List<Object>> rows = new ArrayList<>();
        int count = 0;
        while (rs.next()) {
            count++;
            List<Object> row = new ArrayList<>();
            for (int i = 1; i <= colCount; i++) {
                Object val = rs.getObject(i);
                if (val instanceof Number) {
                    row.add(val);
                } else if (val != null) {
                    row.add(val.toString());
                } else {
                    row.add(null);
                }
            }
            if (count <= 100) { // Keep up to 100 rows
                rows.add(row);
            }
        }
        return new QueryResult(columns, rows, count);
    }

    private boolean compareResults(QueryResult candidate, QueryResult expected, boolean orderRequired) {
        if (candidate.getTotalRows() != expected.getTotalRows()) {
            return false;
        }
        if (candidate.getColumns().size() != expected.getColumns().size()) {
            return false;
        }

        if (orderRequired) {
            for (int r = 0; r < candidate.getRows().size(); r++) {
                List<Object> candRow = candidate.getRows().get(r);
                List<Object> expRow = expected.getRows().get(r);
                if (!compareRowValues(candRow, expRow)) {
                    return false;
                }
            }
            return true;
        } else {
            // Unordered multiset comparison
            Map<String, Integer> expectedFreq = new HashMap<>();
            for (List<Object> row : expected.getRows()) {
                String key = stringifyRow(row);
                expectedFreq.put(key, expectedFreq.getOrDefault(key, 0) + 1);
            }

            for (List<Object> row : candidate.getRows()) {
                String key = stringifyRow(row);
                int count = expectedFreq.getOrDefault(key, 0);
                if (count <= 0) {
                    return false;
                }
                expectedFreq.put(key, count - 1);
            }
            return true;
        }
    }

    private boolean compareRowValues(List<Object> row1, List<Object> row2) {
        if (row1.size() != row2.size()) return false;
        for (int i = 0; i < row1.size(); i++) {
            Object v1 = row1.get(i);
            Object v2 = row2.get(i);
            if (!valuesMatch(v1, v2)) return false;
        }
        return true;
    }

    private boolean valuesMatch(Object v1, Object v2) {
        if (v1 == null && v2 == null) return true;
        if (v1 == null || v2 == null) return false;

        if (v1 instanceof Number n1 && v2 instanceof Number n2) {
            return Math.abs(n1.doubleValue() - n2.doubleValue()) < 0.001;
        }
        return v1.toString().trim().equalsIgnoreCase(v2.toString().trim());
    }

    private String stringifyRow(List<Object> row) {
        StringBuilder sb = new StringBuilder();
        for (Object v : row) {
            if (v instanceof Number n) {
                sb.append(Math.round(n.doubleValue() * 100.0) / 100.0).append("|");
            } else if (v != null) {
                sb.append(v.toString().trim().toLowerCase(Locale.ROOT)).append("|");
            } else {
                sb.append("NULL|");
            }
        }
        return sb.toString();
    }

    private String generateMismatchMessage(QueryResult candidate, QueryResult expected) {
        if (candidate.getColumns().size() != expected.getColumns().size()) {
            return "Column count mismatch: Expected " + expected.getColumns().size() + " columns, but returned " + candidate.getColumns().size() + ".";
        }
        if (candidate.getTotalRows() != expected.getTotalRows()) {
            return "Row count mismatch: Expected " + expected.getTotalRows() + " rows, but returned " + candidate.getTotalRows() + " rows.";
        }
        return "Values mismatch: The output values do not match the expected result set.";
    }

    private static class QueryResult {
        private final List<String> columns;
        private final List<List<Object>> rows;
        private final int totalRows;

        public QueryResult(List<String> columns, List<List<Object>> rows, int totalRows) {
            this.columns = columns;
            this.rows = rows;
            this.totalRows = totalRows;
        }

        public List<String> getColumns() { return columns; }
        public List<List<Object>> getRows() { return rows; }
        public int getTotalRows() { return totalRows; }
    }
}
