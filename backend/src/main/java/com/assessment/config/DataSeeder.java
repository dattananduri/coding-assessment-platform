package com.assessment.config;

import com.assessment.model.*;
import com.assessment.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final AssessmentRepository assessmentRepository;
    private final QuestionRepository questionRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.username:admin}")
    private String adminUsername;

    @Value("${app.admin.password:admin123}")
    private String adminPassword;

    @Value("${app.admin.email:admin@assessment.com}")
    private String adminEmail;

    public DataSeeder(
            UserRepository userRepository,
            AssessmentRepository assessmentRepository,
            QuestionRepository questionRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.assessmentRepository = assessmentRepository;
        this.questionRepository = questionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        seedAdmin();
        seedAssessment();
        seedQuestions();
    }

    private void seedAdmin() {
        User admin = userRepository.findByUsername(adminUsername).orElse(null);
        if (admin == null) {
            admin = new User(adminUsername, passwordEncoder.encode(adminPassword), adminEmail, "ROLE_ADMIN");
        } else {
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setEmail(adminEmail);
            admin.setRole("ROLE_ADMIN");
        }
        userRepository.save(admin);
    }

    private void seedAssessment() {
        if (!assessmentRepository.existsByCode("DEMO90")) {
            Assessment assessment = new Assessment(
                    "Senior Engineering Technical Assessment (90 Mins)",
                    "DEMO90",
                    "Comprehensive technical evaluation consisting of 3 Java coding challenges, 3 SQL database problems, and 1 Spoken English technical walkthrough.",
                    90
            );
            assessmentRepository.save(assessment);
        }
    }

    private void seedQuestions() {
        if (questionRepository.count() > 0) {
            return;
        }

        // ==================== JAVA QUESTIONS ====================

        // Java 1: Arrays - Target Subarray Sum
        Question j1 = new Question();
        j1.setCategory("JAVA");
        j1.setTopic("ARRAYS");
        j1.setDifficulty("MEDIUM");
        j1.setTitle("Subarray Sum Matching Target");
        j1.setDescription("Given an array of integers `nums` and an integer `k`, return the total number of continuous subarrays whose sum equals `k`.\n\nA subarray is a contiguous non-empty sequence of elements within an array.");
        j1.setInputFormat("First line contains two integers: N (size of array) and K (target sum).\nSecond line contains N space-separated integers.");
        j1.setOutputFormat("Output a single integer representing the count of subarrays whose sum is equal to K.");
        j1.setConstraints("1 <= N <= 20000\n-1000 <= nums[i] <= 1000\n-10000000 <= k <= 10000000");
        j1.setStarterCode("""
import java.util.*;

public class Solution {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextInt()) return;
        int n = sc.nextInt();
        int k = sc.nextInt();
        int[] nums = new int[n];
        for (int i = 0; i < n; i++) {
            nums[i] = sc.nextInt();
        }
        
        System.out.println(subarraySum(nums, k));
    }

    public static int subarraySum(int[] nums, int k) {
        // TODO: Implement your solution here
        Map<Integer, Integer> map = new HashMap<>();
        map.put(0, 1);
        int sum = 0;
        int count = 0;
        for (int x : nums) {
            sum += x;
            if (map.containsKey(sum - k)) {
                count += map.get(sum - k);
            }
            map.put(sum, map.getOrDefault(sum, 0) + 1);
        }
        return count;
    }
}
""");
        j1.setTimeLimitMs(2000);
        j1.setMemoryLimitMb(128);
        j1.setMaxScore(10);
        j1.getTestCases().addAll(Arrays.asList(
                new TestCase(j1, "3 2\n1 1 1", "2", false, 1),
                new TestCase(j1, "3 3\n1 2 3", "2", false, 1),
                new TestCase(j1, "5 0\n0 0 0 0 0", "15", true, 1),
                new TestCase(j1, "6 5\n1 -1 5 -2 3 2", "3", true, 1)
        ));
        questionRepository.save(j1);

        // Java 2: Arrays - Merge Overlapping Intervals
        Question j2 = new Question();
        j2.setCategory("JAVA");
        j2.setTopic("ARRAYS");
        j2.setDifficulty("MEDIUM");
        j2.setTitle("Merge Overlapping Intervals");
        j2.setDescription("Given an array of intervals where intervals[i] = [start_i, end_i], merge all overlapping intervals, and return an array of the non-overlapping intervals that cover all the intervals in the input.");
        j2.setInputFormat("First line contains integer N (number of intervals).\nThe next N lines each contain two integers: start and end.");
        j2.setOutputFormat("Print the merged intervals sorted by their start time. Each line should contain two integers: start and end.");
        j2.setConstraints("1 <= N <= 10000\n0 <= start_i <= end_i <= 100000");
        j2.setStarterCode("""
import java.util.*;

public class Solution {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextInt()) return;
        int n = sc.nextInt();
        int[][] intervals = new int[n][2];
        for (int i = 0; i < n; i++) {
            intervals[i][0] = sc.nextInt();
            intervals[i][1] = sc.nextInt();
        }

        int[][] merged = merge(intervals);
        for (int[] interval : merged) {
            System.out.println(interval[0] + " " + interval[1]);
        }
    }

    public static int[][] merge(int[][] intervals) {
        if (intervals.length <= 1) return intervals;
        Arrays.sort(intervals, Comparator.comparingInt(a -> a[0]));
        List<int[]> result = new ArrayList<>();
        int[] current = intervals[0];
        result.add(current);

        for (int[] interval : intervals) {
            if (interval[0] <= current[1]) {
                current[1] = Math.max(current[1], interval[1]);
            } else {
                current = interval;
                result.add(current);
            }
        }
        return result.toArray(new int[result.size()][]);
    }
}
""");
        j2.setTimeLimitMs(2000);
        j2.setMemoryLimitMb(128);
        j2.setMaxScore(10);
        j2.getTestCases().addAll(Arrays.asList(
                new TestCase(j2, "4\n1 3\n2 6\n8 10\n15 18", "1 6\n8 10\n15 18", false, 1),
                new TestCase(j2, "2\n1 4\n4 5", "1 5", false, 1),
                new TestCase(j2, "3\n1 4\n0 4\n2 3", "0 4", true, 1),
                new TestCase(j2, "4\n2 3\n4 5\n6 7\n8 9", "2 3\n4 5\n6 7\n8 9", true, 1)
        ));
        questionRepository.save(j2);

        // Java 3: Strings - Longest Substring Without Repeating Characters
        Question j3 = new Question();
        j3.setCategory("JAVA");
        j3.setTopic("STRINGS");
        j3.setDifficulty("MEDIUM");
        j3.setTitle("Longest Substring Without Repeating Characters");
        j3.setDescription("Given a string `s`, find the length of the longest substring without duplicate characters.");
        j3.setInputFormat("Single line containing string s.");
        j3.setOutputFormat("Print a single integer representing the maximum length of a non-repeating substring.");
        j3.setConstraints("0 <= s.length <= 50000\ns consists of English letters, digits, symbols, and spaces.");
        j3.setStarterCode("""
import java.util.*;

public class Solution {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String s = sc.hasNextLine() ? sc.nextLine() : "";
        System.out.println(lengthOfLongestSubstring(s));
    }

    public static int lengthOfLongestSubstring(String s) {
        Map<Character, Integer> lastSeen = new HashMap<>();
        int maxLength = 0;
        int left = 0;

        for (int right = 0; right < s.length(); right++) {
            char c = s.charAt(right);
            if (lastSeen.containsKey(c) && lastSeen.get(c) >= left) {
                left = lastSeen.get(c) + 1;
            }
            lastSeen.put(c, right);
            maxLength = Math.max(maxLength, right - left + 1);
        }
        return maxLength;
    }
}
""");
        j3.setTimeLimitMs(2000);
        j3.setMemoryLimitMb(128);
        j3.setMaxScore(10);
        j3.getTestCases().addAll(Arrays.asList(
                new TestCase(j3, "abcabcbb", "3", false, 1),
                new TestCase(j3, "bbbbb", "1", false, 1),
                new TestCase(j3, "pwwkew", "3", true, 1),
                new TestCase(j3, "tmmzuxt", "5", true, 1)
        ));
        questionRepository.save(j3);

        // Java 4: Strings - Group Anagrams
        Question j4 = new Question();
        j4.setCategory("JAVA");
        j4.setTopic("STRINGS");
        j4.setDifficulty("MEDIUM");
        j4.setTitle("Group Anagram Strings");
        j4.setDescription("Given an array of strings `strs`, group all anagrams together. An Anagram is a word or phrase formed by rearranging the letters of a different word or phrase.");
        j4.setInputFormat("First line contains integer N.\nSecond line contains N space-separated strings.");
        j4.setOutputFormat("Print each anagram group on a new line, words sorted alphabetically, and lines sorted by the first word in each group.");
        j4.setConstraints("1 <= N <= 10000\n1 <= strs[i].length <= 100\nstrs[i] consists of lowercase English letters.");
        j4.setStarterCode("""
import java.util.*;

public class Solution {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextInt()) return;
        int n = sc.nextInt();
        String[] strs = new String[n];
        for (int i = 0; i < n; i++) {
            strs[i] = sc.next();
        }

        Map<String, List<String>> map = new HashMap<>();
        for (String s : strs) {
            char[] chars = s.toCharArray();
            Arrays.sort(chars);
            String key = new String(chars);
            map.computeIfAbsent(key, k -> new ArrayList<>()).add(s);
        }

        List<List<String>> groups = new ArrayList<>(map.values());
        for (List<String> group : groups) {
            Collections.sort(group);
        }
        groups.sort(Comparator.comparing(g -> g.get(0)));

        for (List<String> group : groups) {
            System.out.println(String.join(" ", group));
        }
    }
}
""");
        j4.setTimeLimitMs(2000);
        j4.setMemoryLimitMb(128);
        j4.setMaxScore(10);
        j4.getTestCases().addAll(Arrays.asList(
                new TestCase(j4, "6\neat tea tan ate nat bat", "bat\neat tea ate\nnat tan", false, 1),
                new TestCase(j4, "1\na", "a", false, 1),
                new TestCase(j4, "4\nab ba cd dc", "ab ba\ncd dc", true, 1)
        ));
        questionRepository.save(j4);

        // Java 5: Data Structures - Evaluate Reverse Polish Notation (Stack)
        Question j5 = new Question();
        j5.setCategory("JAVA");
        j5.setTopic("DATA_STRUCTURES");
        j5.setDifficulty("MEDIUM");
        j5.setTitle("Evaluate Reverse Polish Notation");
        j5.setDescription("You are given an array of strings `tokens` that represents an arithmetic expression in a Reverse Polish Notation (RPN / Postfix).\n\nEvaluate the expression. Return an integer that represents the value of the expression.\n\nValid operators are '+', '-', '*', and '/'. Division truncates toward zero.");
        j5.setInputFormat("First line contains integer N.\nSecond line contains N space-separated tokens.");
        j5.setOutputFormat("Print a single integer representing the evaluated result.");
        j5.setConstraints("1 <= N <= 10000\ntokens[i] is either an operator or an integer in range [-200, 200]");
        j5.setStarterCode("""
import java.util.*;

public class Solution {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextInt()) return;
        int n = sc.nextInt();
        String[] tokens = new String[n];
        for (int i = 0; i < n; i++) {
            tokens[i] = sc.next();
        }

        System.out.println(evalRPN(tokens));
    }

    public static int evalRPN(String[] tokens) {
        Deque<Integer> stack = new ArrayDeque<>();
        for (String token : tokens) {
            if (token.equals("+")) {
                stack.push(stack.pop() + stack.pop());
            } else if (token.equals("-")) {
                int b = stack.pop();
                int a = stack.pop();
                stack.push(a - b);
            } else if (token.equals("*")) {
                stack.push(stack.pop() * stack.pop());
            } else if (token.equals("/")) {
                int b = stack.pop();
                int a = stack.pop();
                stack.push(a / b);
            } else {
                stack.push(Integer.parseInt(token));
            }
        }
        return stack.pop();
    }
}
""");
        j5.setTimeLimitMs(2000);
        j5.setMemoryLimitMb(128);
        j5.setMaxScore(10);
        j5.getTestCases().addAll(Arrays.asList(
                new TestCase(j5, "5\n2 1 + 3 *", "9", false, 1),
                new TestCase(j5, "5\n4 13 5 / +", "6", false, 1),
                new TestCase(j5, "5\n10 6 9 3 + -11 * / * 17 + 5 +", "22", true, 1)
        ));
        questionRepository.save(j5);

        // Java 6: Data Structures - Valid Parentheses Depth / Minimum Removal
        Question j6 = new Question();
        j6.setCategory("JAVA");
        j6.setTopic("DATA_STRUCTURES");
        j6.setDifficulty("MEDIUM");
        j6.setTitle("Minimum Removals for Valid Parentheses");
        j6.setDescription("Given a string `s` containing '(' and ')' and lowercase letters, remove the minimum number of parentheses (either '(' or ')', in any positions) so that the resulting parentheses string is valid and return the number of characters in the resulting string.");
        j6.setInputFormat("A single line containing string s.");
        j6.setOutputFormat("Print the length of the longest valid parentheses string that can be formed.");
        j6.setConstraints("1 <= s.length <= 100000\ns consists of lowercase letters and parentheses '(' and ')'.");
        j6.setStarterCode("""
import java.util.*;

public class Solution {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String s = sc.hasNextLine() ? sc.nextLine() : "";
        System.out.println(minRemoveToMakeValid(s));
    }

    public static int minRemoveToMakeValid(String s) {
        Set<Integer> indexesToRemove = new HashSet<>();
        Deque<Integer> stack = new ArrayDeque<>();

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '(') {
                stack.push(i);
            } else if (c == ')') {
                if (stack.isEmpty()) {
                    indexesToRemove.add(i);
                } else {
                    stack.pop();
                }
            }
        }

        while (!stack.isEmpty()) {
            indexesToRemove.add(stack.pop());
        }

        return s.length() - indexesToRemove.size();
    }
}
""");
        j6.setTimeLimitMs(2000);
        j6.setMemoryLimitMb(128);
        j6.setMaxScore(10);
        j6.getTestCases().addAll(Arrays.asList(
                new TestCase(j6, "lee(t(c)o)de)", "12", false, 1),
                new TestCase(j6, "a)b(c)d", "6", false, 1),
                new TestCase(j6, "))((", "0", true, 1),
                new TestCase(j6, "abc", "3", true, 1)
        ));
        questionRepository.save(j6);

        // ==================== SQL QUESTIONS ====================

        // SQL 1: JOINs - Customer Orders & Unfulfilled Status
        Question s1 = new Question();
        s1.setCategory("SQL");
        s1.setTopic("JOINS");
        s1.setDifficulty("INTERMEDIATE");
        s1.setTitle("Customer Orders with Shipment Tracking");
        s1.setDescription("Write a query to retrieve customer details along with their placed orders and carrier shipping details.\n\nReturn the `customer_name`, `order_id`, `order_date`, and `carrier`. If an order has not been assigned a carrier yet, display `'PENDING'` for the carrier. Only include orders where `total_amount` is greater than or equal to 100.00.\n\nOrder results by `order_id` ascending.");
        s1.setStarterCode("-- Write your SELECT query here\nSELECT \n  c.name AS customer_name,\n  o.id AS order_id,\n  o.order_date,\n  COALESCE(s.carrier, 'PENDING') AS carrier\nFROM customers c\nJOIN orders o ON c.id = o.customer_id\nLEFT JOIN shipments s ON o.id = s.order_id\nWHERE o.total_amount >= 100.00\nORDER BY o.id ASC;");
        s1.setMaxScore(10);
        SqlDataset ds1 = new SqlDataset(
                s1,
                "### Database Schema\n- `customers` (`id` INT, `name` VARCHAR(100), `email` VARCHAR(100))\n- `orders` (`id` INT, `customer_id` INT, `order_date` DATE, `total_amount` DECIMAL(10,2))\n- `shipments` (`id` INT, `order_id` INT, `carrier` VARCHAR(50), `tracking_no` VARCHAR(50))",
                """
                CREATE TABLE customers (id INT PRIMARY KEY, name VARCHAR(100), email VARCHAR(100));
                CREATE TABLE orders (id INT PRIMARY KEY, customer_id INT, order_date DATE, total_amount DECIMAL(10,2));
                CREATE TABLE shipments (id INT PRIMARY KEY, order_id INT, carrier VARCHAR(50), tracking_no VARCHAR(50));
                """,
                """
                INSERT INTO customers VALUES (1, 'Alice Smith', 'alice@example.com'), (2, 'Bob Jones', 'bob@example.com'), (3, 'Charlie Brown', 'charlie@example.com');
                INSERT INTO orders VALUES (101, 1, '2024-01-15', 150.00), (102, 1, '2024-02-10', 45.00), (103, 2, '2024-02-12', 220.50), (104, 3, '2024-03-01', 110.00);
                INSERT INTO shipments VALUES (1, 101, 'FedEx', 'TRK1001'), (2, 103, 'UPS', 'TRK1002');
                """,
                "SELECT c.name AS customer_name, o.id AS order_id, o.order_date, COALESCE(s.carrier, 'PENDING') AS carrier FROM customers c JOIN orders o ON c.id = o.customer_id LEFT JOIN shipments s ON o.id = s.order_id WHERE o.total_amount >= 100.00 ORDER BY o.id ASC;",
                true,
                """
                [
                  {"customer_name": "Alice Smith", "order_id": 101, "order_date": "2024-01-15", "carrier": "FedEx"},
                  {"customer_name": "Bob Jones", "order_id": 103, "order_date": "2024-02-12", "carrier": "UPS"},
                  {"customer_name": "Charlie Brown", "order_id": 104, "order_date": "2024-03-01", "carrier": "PENDING"}
                ]
                """
        );
        s1.setSqlDataset(ds1);
        questionRepository.save(s1);

        // SQL 2: JOINs - Employee Manager Hierarchy
        Question s2 = new Question();
        s2.setCategory("SQL");
        s2.setTopic("JOINS");
        s2.setDifficulty("INTERMEDIATE");
        s2.setTitle("Employee Management and Department Hierarchy");
        s2.setDescription("Write a query to report the `employee_name`, `department_name`, `salary`, and `manager_name` for each employee in the company.\n\nIf an employee does not have a manager (e.g. CEO), display `'None'` as the manager name.\n\nOrder the output by `department_name` ascending, then `salary` descending.");
        s2.setStarterCode("-- Write your SELECT query here\nSELECT \n  e.name AS employee_name,\n  d.dept_name AS department_name,\n  e.salary,\n  COALESCE(m.name, 'None') AS manager_name\nFROM employees e\nJOIN departments d ON e.dept_id = d.id\nLEFT JOIN employees m ON e.manager_id = m.id\nORDER BY department_name ASC, e.salary DESC;");
        s2.setMaxScore(10);
        SqlDataset ds2 = new SqlDataset(
                s2,
                "### Database Schema\n- `departments` (`id` INT, `dept_name` VARCHAR(50))\n- `employees` (`id` INT, `name` VARCHAR(100), `dept_id` INT, `manager_id` INT, `salary` INT)",
                """
                CREATE TABLE departments (id INT PRIMARY KEY, dept_name VARCHAR(50));
                CREATE TABLE employees (id INT PRIMARY KEY, name VARCHAR(100), dept_id INT, manager_id INT, salary INT);
                """,
                """
                INSERT INTO departments VALUES (1, 'Engineering'), (2, 'Marketing'), (3, 'Finance');
                INSERT INTO employees VALUES (1, 'Sarah Connor', 1, NULL, 150000), (2, 'John Doe', 1, 1, 110000), (3, 'Jane Smith', 2, NULL, 130000), (4, 'Mike Ross', 2, 3, 90000), (5, 'Harvey Specter', 3, NULL, 200000);
                """,
                "SELECT e.name AS employee_name, d.dept_name AS department_name, e.salary, COALESCE(m.name, 'None') AS manager_name FROM employees e JOIN departments d ON e.dept_id = d.id LEFT JOIN employees m ON e.manager_id = m.id ORDER BY department_name ASC, e.salary DESC;",
                true,
                """
                [
                  {"employee_name": "Sarah Connor", "department_name": "Engineering", "salary": 150000, "manager_name": "None"},
                  {"employee_name": "John Doe", "department_name": "Engineering", "salary": 110000, "manager_name": "Sarah Connor"},
                  {"employee_name": "Harvey Specter", "department_name": "Finance", "salary": 200000, "manager_name": "None"},
                  {"employee_name": "Jane Smith", "department_name": "Marketing", "salary": 130000, "manager_name": "None"},
                  {"employee_name": "Mike Ross", "department_name": "Marketing", "salary": 90000, "manager_name": "Jane Smith"}
                ]
                """
        );
        s2.setSqlDataset(ds2);
        questionRepository.save(s2);

        // SQL 3: Aggregation - High Volume Product Categories & Monthly Revenue
        Question s3 = new Question();
        s3.setCategory("SQL");
        s3.setTopic("AGGREGATION");
        s3.setDifficulty("INTERMEDIATE");
        s3.setTitle("High Revenue Product Categories");
        s3.setDescription("Write a query to calculate the `category`, the total quantity sold (`total_units`), and the total revenue generated (`total_revenue`) for each product category.\n\nOnly include categories where `total_revenue` is at least 1,000.00 and `total_units` is at least 10.\n\nOrder the results by `total_revenue` descending.");
        s3.setStarterCode("-- Write your SELECT query here\nSELECT \n  p.category,\n  SUM(oi.quantity) AS total_units,\n  SUM(oi.quantity * oi.unit_price) AS total_revenue\nFROM products p\nJOIN order_items oi ON p.id = oi.product_id\nGROUP BY p.category\nHAVING SUM(oi.quantity * oi.unit_price) >= 1000.00 AND SUM(oi.quantity) >= 10\nORDER BY total_revenue DESC;");
        s3.setMaxScore(10);
        SqlDataset ds3 = new SqlDataset(
                s3,
                "### Database Schema\n- `products` (`id` INT, `name` VARCHAR(100), `category` VARCHAR(50))\n- `order_items` (`id` INT, `product_id` INT, `quantity` INT, `unit_price` DECIMAL(10,2))",
                """
                CREATE TABLE products (id INT PRIMARY KEY, name VARCHAR(100), category VARCHAR(50));
                CREATE TABLE order_items (id INT PRIMARY KEY, product_id INT, quantity INT, unit_price DECIMAL(10,2));
                """,
                """
                INSERT INTO products VALUES (1, 'Laptop Pro', 'Electronics'), (2, 'Wireless Mouse', 'Electronics'), (3, 'Office Desk', 'Furniture'), (4, 'Ergo Chair', 'Furniture'), (5, 'Pen Pack', 'Stationery');
                INSERT INTO order_items VALUES (1, 1, 5, 800.00), (2, 2, 20, 25.00), (3, 3, 2, 350.00), (4, 4, 10, 150.00), (5, 5, 15, 10.00);
                """,
                "SELECT p.category, SUM(oi.quantity) AS total_units, SUM(oi.quantity * oi.unit_price) AS total_revenue FROM products p JOIN order_items oi ON p.id = oi.product_id GROUP BY p.category HAVING SUM(oi.quantity * oi.unit_price) >= 1000.00 AND SUM(oi.quantity) >= 10 ORDER BY total_revenue DESC;",
                true,
                """
                [
                  {"category": "Electronics", "total_units": 25, "total_revenue": 4500.00},
                  {"category": "Furniture", "total_units": 12, "total_revenue": 2200.00}
                ]
                """
        );
        s3.setSqlDataset(ds3);
        questionRepository.save(s3);

        // SQL 4: Aggregation - Customer Order Summary with Status Filters
        Question s4 = new Question();
        s4.setCategory("SQL");
        s4.setTopic("AGGREGATION");
        s4.setDifficulty("INTERMEDIATE");
        s4.setTitle("Customer Purchasing Volume and Average Order Value");
        s4.setDescription("Write a query to report for each customer their `customer_id`, total orders placed (`order_count`), and average order amount (`avg_order_value`) rounded to 2 decimal places.\n\nOnly include customers who have placed at least 2 orders.\n\nOrder the results by `avg_order_value` descending.");
        s4.setStarterCode("-- Write your SELECT query here\nSELECT \n  customer_id,\n  COUNT(id) AS order_count,\n  ROUND(AVG(amount), 2) AS avg_order_value\nFROM customer_orders\nGROUP BY customer_id\nHAVING COUNT(id) >= 2\nORDER BY avg_order_value DESC;");
        s4.setMaxScore(10);
        SqlDataset ds4 = new SqlDataset(
                s4,
                "### Database Schema\n- `customer_orders` (`id` INT, `customer_id` INT, `order_date` DATE, `amount` DECIMAL(10,2))",
                """
                CREATE TABLE customer_orders (id INT PRIMARY KEY, customer_id INT, order_date DATE, amount DECIMAL(10,2));
                """,
                """
                INSERT INTO customer_orders VALUES (1, 101, '2024-01-01', 120.00), (2, 101, '2024-01-15', 80.00), (3, 102, '2024-01-10', 300.00), (4, 102, '2024-02-05', 450.00), (5, 103, '2024-02-01', 50.00);
                """,
                "SELECT customer_id, COUNT(id) AS order_count, ROUND(AVG(amount), 2) AS avg_order_value FROM customer_orders GROUP BY customer_id HAVING COUNT(id) >= 2 ORDER BY avg_order_value DESC;",
                true,
                """
                [
                  {"customer_id": 102, "order_count": 2, "avg_order_value": 375.00},
                  {"customer_id": 101, "order_count": 2, "avg_order_value": 100.00}
                ]
                """
        );
        s4.setSqlDataset(ds4);
        questionRepository.save(s4);

        // SQL 5: Window Functions - Department Top Earners
        Question s5 = new Question();
        s5.setCategory("SQL");
        s5.setTopic("WINDOW_FUNCTIONS");
        s5.setDifficulty("INTERMEDIATE");
        s5.setTitle("Department Top 2 Salaries using Window Functions");
        s5.setDescription("Write a query to find employees who earn the top 2 highest distinct salaries in each department using `DENSE_RANK()`.\n\nOutput the `department_name`, `employee_name`, and `salary`.\n\nOrder the results by `department_name` ascending, then `salary` descending, then `employee_name` ascending.");
        s5.setStarterCode("-- Write your SELECT query with window function here\nWITH RankedSalaries AS (\n  SELECT \n    d.dept_name AS department_name,\n    e.name AS employee_name,\n    e.salary,\n    DENSE_RANK() OVER (PARTITION BY e.dept_id ORDER BY e.salary DESC) as rnk\n  FROM employees e\n  JOIN departments d ON e.dept_id = d.id\n)\nSELECT department_name, employee_name, salary\nFROM RankedSalaries\nWHERE rnk <= 2\nORDER BY department_name ASC, salary DESC, employee_name ASC;");
        s5.setMaxScore(10);
        SqlDataset ds5 = new SqlDataset(
                s5,
                "### Database Schema\n- `departments` (`id` INT, `dept_name` VARCHAR(50))\n- `employees` (`id` INT, `name` VARCHAR(100), `salary` INT, `dept_id` INT)",
                """
                CREATE TABLE departments (id INT PRIMARY KEY, dept_name VARCHAR(50));
                CREATE TABLE employees (id INT PRIMARY KEY, name VARCHAR(100), salary INT, dept_id INT);
                """,
                """
                INSERT INTO departments VALUES (1, 'Engineering'), (2, 'Sales');
                INSERT INTO employees VALUES (1, 'Joe', 85000, 1), (2, 'Henry', 80000, 2), (3, 'Sam', 60000, 2), (4, 'Max', 90000, 1), (5, 'Janet', 69000, 1), (6, 'Randy', 85000, 1);
                """,
                "WITH RankedSalaries AS (SELECT d.dept_name AS department_name, e.name AS employee_name, e.salary, DENSE_RANK() OVER (PARTITION BY e.dept_id ORDER BY e.salary DESC) as rnk FROM employees e JOIN departments d ON e.dept_id = d.id) SELECT department_name, employee_name, salary FROM RankedSalaries WHERE rnk <= 2 ORDER BY department_name ASC, salary DESC, employee_name ASC;",
                true,
                """
                [
                  {"department_name": "Engineering", "employee_name": "Max", "salary": 90000},
                  {"department_name": "Engineering", "employee_name": "Joe", "salary": 85000},
                  {"department_name": "Engineering", "employee_name": "Randy", "salary": 85000},
                  {"department_name": "Sales", "employee_name": "Henry", "salary": 80000},
                  {"department_name": "Sales", "employee_name": "Sam", "salary": 60000}
                ]
                """
        );
        s5.setSqlDataset(ds5);
        questionRepository.save(s5);

        // SQL 6: Window Functions - Running Total Revenue
        Question s6 = new Question();
        s6.setCategory("SQL");
        s6.setTopic("WINDOW_FUNCTIONS");
        s6.setDifficulty("INTERMEDIATE");
        s6.setTitle("Running Total Revenue by Region");
        s6.setDescription("Write a query to compute the cumulative running total of sales revenue for each sales `region` ordered by `sale_date`.\n\nReturn `region`, `sale_date`, `amount`, and the running total as `cumulative_revenue`.\n\nOrder results by `region` ascending, then `sale_date` ascending.");
        s6.setStarterCode("-- Write your SELECT query with window function here\nSELECT \n  region,\n  sale_date,\n  amount,\n  SUM(amount) OVER (PARTITION BY region ORDER BY sale_date ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW) AS cumulative_revenue\nFROM sales\nORDER BY region ASC, sale_date ASC;");
        s6.setMaxScore(10);
        SqlDataset ds6 = new SqlDataset(
                s6,
                "### Database Schema\n- `sales` (`id` INT, `region` VARCHAR(50), `sale_date` DATE, `amount` DECIMAL(10,2))",
                """
                CREATE TABLE sales (id INT PRIMARY KEY, region VARCHAR(50), sale_date DATE, amount DECIMAL(10,2));
                """,
                """
                INSERT INTO sales VALUES (1, 'North', '2024-01-01', 500.00), (2, 'North', '2024-01-05', 300.00), (3, 'North', '2024-01-10', 400.00), (4, 'South', '2024-01-02', 700.00), (5, 'South', '2024-01-08', 250.00);
                """,
                "SELECT region, sale_date, amount, SUM(amount) OVER (PARTITION BY region ORDER BY sale_date ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW) AS cumulative_revenue FROM sales ORDER BY region ASC, sale_date ASC;",
                true,
                """
                [
                  {"region": "North", "sale_date": "2024-01-01", "amount": 500.00, "cumulative_revenue": 500.00},
                  {"region": "North", "sale_date": "2024-01-05", "amount": 300.00, "cumulative_revenue": 800.00},
                  {"region": "North", "sale_date": "2024-01-10", "amount": 400.00, "cumulative_revenue": 1200.00},
                  {"region": "South", "sale_date": "2024-01-02", "amount": 700.00, "cumulative_revenue": 700.00},
                  {"region": "South", "sale_date": "2024-01-08", "amount": 250.00, "cumulative_revenue": 950.00}
                ]
                """
        );
        s6.setSqlDataset(ds6);
        questionRepository.save(s6);

        // ==================== ENGLISH QUESTIONS ====================

        // English 1: Final Year Project
        Question e1 = new Question();
        e1.setCategory("ENGLISH");
        e1.setTopic("COMMUNICATION");
        e1.setDifficulty("INTERMEDIATE");
        e1.setTitle("Technical Project & Problem Solving Walkthrough");
        e1.setDescription("Explain your final-year or recent engineering project, the core technologies and architecture you utilized, the major technical bottlenecks or challenges you encountered, and the concrete methodologies you employed to resolve them.");
        e1.setMaxScore(40);
        questionRepository.save(e1);

        // English 2: Production Incident & Debugging
        Question e2 = new Question();
        e2.setCategory("ENGLISH");
        e2.setTopic("COMMUNICATION");
        e2.setDifficulty("INTERMEDIATE");
        e2.setTitle("Engineering Incident Resolution & Root Cause Analysis");
        e2.setDescription("Describe a situation where you had to debug and solve an intricate software bug or performance degradation under pressure. Explain your diagnostic methodology, the tools used, the resolution implemented, and key takeaways.");
        e2.setMaxScore(40);
        questionRepository.save(e2);
    }
}
