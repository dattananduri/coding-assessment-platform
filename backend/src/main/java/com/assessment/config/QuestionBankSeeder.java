package com.assessment.config;

import com.assessment.model.Question;
import com.assessment.model.SqlDataset;
import com.assessment.model.TestCase;
import com.assessment.repository.QuestionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@Order(2)
public class QuestionBankSeeder implements CommandLineRunner {

    private final QuestionRepository questionRepository;

    public QuestionBankSeeder(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        long currentCount = questionRepository.count();
        if (currentCount >= 50) {
            return;
        }

        List<Question> questions = new ArrayList<>();

        // Helper to add Java question
        // ----------------------------------------------------
        // SECTION A: ARRAYS (JAVA)
        // ----------------------------------------------------

        questions.add(createJavaQ(
                "ARRAYS",
                "Container With Most Water",
                "Given an integer array `height` of length `n`, where each element represents the height of a vertical line at coordinate (i, height[i]). Find two lines that together with the x-axis form a container, such that the container contains the most water. Return the maximum amount of water a container can store.",
                "First line contains integer N.\nSecond line contains N space-separated integers representing heights.",
                "Print a single integer representing maximum water container area.",
                "2 <= N <= 100000\n0 <= height[i] <= 10000",
                """
import java.util.*;

public class Solution {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextInt()) return;
        int n = sc.nextInt();
        int[] height = new int[n];
        for (int i = 0; i < n; i++) height[i] = sc.nextInt();
        
        int l = 0, r = n - 1;
        int maxArea = 0;
        while (l < r) {
            int h = Math.min(height[l], height[r]);
            maxArea = Math.max(maxArea, h * (r - l));
            if (height[l] < height[r]) l++;
            else r--;
        }
        System.out.println(maxArea);
    }
}
""",
                Arrays.asList(
                        new TestCase(null, "9\n1 8 6 2 5 4 8 3 7", "49", false, 1),
                        new TestCase(null, "2\n1 1", "1", false, 1),
                        new TestCase(null, "5\n4 3 2 1 4", "16", true, 1),
                        new TestCase(null, "4\n1 2 4 3", "4", true, 1)
                )
        ));

        questions.add(createJavaQ(
                "ARRAYS",
                "Three Sum Zero Balance",
                "Given an integer array nums, return the count of all triplets [nums[i], nums[j], nums[k]] such that i != j, i != k, and j != k, and nums[i] + nums[j] + nums[k] == 0 without duplicate triplets.",
                "First line contains integer N.\nSecond line contains N space-separated integers.",
                "Print the count of unique triplets that sum to zero.",
                "3 <= N <= 3000\n-100000 <= nums[i] <= 100000",
                """
import java.util.*;

public class Solution {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextInt()) return;
        int n = sc.nextInt();
        int[] nums = new int[n];
        for (int i = 0; i < n; i++) nums[i] = sc.nextInt();
        Arrays.sort(nums);
        int count = 0;
        for (int i = 0; i < n - 2; i++) {
            if (i > 0 && nums[i] == nums[i - 1]) continue;
            int l = i + 1, r = n - 1;
            while (l < r) {
                int sum = nums[i] + nums[l] + nums[r];
                if (sum == 0) {
                    count++;
                    while (l < r && nums[l] == nums[l + 1]) l++;
                    while (l < r && nums[r] == nums[r - 1]) r--;
                    l++; r--;
                } else if (sum < 0) l++;
                else r--;
            }
        }
        System.out.println(count);
    }
}
""",
                Arrays.asList(
                        new TestCase(null, "6\n-1 0 1 2 -1 -4", "2", false, 1),
                        new TestCase(null, "3\n0 1 1", "0", false, 1),
                        new TestCase(null, "3\n0 0 0", "1", true, 1),
                        new TestCase(null, "7\n-2 0 1 1 2 -1 -4", "4", true, 1)
                )
        ));

        questions.add(createJavaQ(
                "ARRAYS",
                "Product of Array Except Self",
                "Given an integer array nums, return an array answer such that answer[i] is equal to the product of all the elements of nums except nums[i], calculated in O(n) without division.",
                "First line contains integer N.\nSecond line contains N space-separated integers.",
                "Print N space-separated integers representing the resulting products.",
                "2 <= N <= 100000\n-30 <= nums[i] <= 30",
                """
import java.util.*;

public class Solution {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextInt()) return;
        int n = sc.nextInt();
        int[] nums = new int[n];
        for (int i = 0; i < n; i++) nums[i] = sc.nextInt();
        int[] res = new int[n];
        res[0] = 1;
        for (int i = 1; i < n; i++) res[i] = res[i - 1] * nums[i - 1];
        int r = 1;
        for (int i = n - 1; i >= 0; i--) {
            res[i] = res[i] * r;
            r *= nums[i];
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            sb.append(res[i]).append(i == n - 1 ? "" : " ");
        }
        System.out.println(sb.toString());
    }
}
""",
                Arrays.asList(
                        new TestCase(null, "4\n1 2 3 4", "24 12 8 6", false, 1),
                        new TestCase(null, "5\n-1 1 0 -3 3", "0 0 9 0 0", false, 1),
                        new TestCase(null, "2\n2 5", "5 2", true, 1)
                )
        ));

        questions.add(createJavaQ(
                "ARRAYS",
                "Maximum Product Subarray",
                "Given an integer array `nums`, find a contiguous non-empty subarray within the array that has the largest product, and return that product.",
                "First line contains integer N.\nSecond line contains N space-separated integers.",
                "Print a single integer representing the maximum product.",
                "1 <= N <= 20000\n-10 <= nums[i] <= 10",
                """
import java.util.*;

public class Solution {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextInt()) return;
        int n = sc.nextInt();
        int[] nums = new int[n];
        for (int i = 0; i < n; i++) nums[i] = sc.nextInt();
        int maxP = nums[0], minP = nums[0], res = nums[0];
        for (int i = 1; i < n; i++) {
            int curr = nums[i];
            int tempMax = Math.max(curr, Math.max(maxP * curr, minP * curr));
            minP = Math.min(curr, Math.min(maxP * curr, minP * curr));
            maxP = tempMax;
            res = Math.max(res, maxP);
        }
        System.out.println(res);
    }
}
""",
                Arrays.asList(
                        new TestCase(null, "4\n2 3 -2 4", "6", false, 1),
                        new TestCase(null, "3\n-2 0 -1", "0", false, 1),
                        new TestCase(null, "2\n-2 3", "3", true, 1),
                        new TestCase(null, "3\n-2 -3 -4", "6", true, 1)
                )
        ));

        questions.add(createJavaQ(
                "ARRAYS",
                "Find Minimum in Rotated Sorted Array",
                "Suppose an array of length `n` sorted in ascending order is rotated between 1 and n times. Given the rotated sorted array `nums` of unique elements, return the minimum element of this array in O(log n) time.",
                "First line contains integer N.\nSecond line contains N space-separated integers.",
                "Print the minimum element.",
                "1 <= N <= 50000\n-50000 <= nums[i] <= 50000",
                """
import java.util.*;

public class Solution {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextInt()) return;
        int n = sc.nextInt();
        int[] nums = new int[n];
        for (int i = 0; i < n; i++) nums[i] = sc.nextInt();
        int l = 0, r = n - 1;
        while (l < r) {
            int mid = l + (r - l) / 2;
            if (nums[mid] > nums[r]) l = mid + 1;
            else r = mid;
        }
        System.out.println(nums[l]);
    }
}
""",
                Arrays.asList(
                        new TestCase(null, "5\n3 4 5 1 2", "1", false, 1),
                        new TestCase(null, "7\n4 5 6 7 0 1 2", "0", false, 1),
                        new TestCase(null, "4\n11 13 15 17", "11", true, 1),
                        new TestCase(null, "1\n42", "42", true, 1)
                )
        ));

        questions.add(createJavaQ(
                "ARRAYS",
                "Search in Rotated Sorted Array",
                "Given rotated sorted array `nums` of unique integers and integer `target`, return the 0-indexed position of `target` if found, or `-1` if not found in O(log n) time.",
                "First line contains integers N and target.\nSecond line contains N space-separated integers.",
                "Print the index of target or -1.",
                "1 <= N <= 100000\n-10000 <= nums[i], target <= 10000",
                """
import java.util.*;

public class Solution {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextInt()) return;
        int n = sc.nextInt();
        int target = sc.nextInt();
        int[] nums = new int[n];
        for (int i = 0; i < n; i++) nums[i] = sc.nextInt();
        int l = 0, r = n - 1;
        int ans = -1;
        while (l <= r) {
            int mid = l + (r - l) / 2;
            if (nums[mid] == target) { ans = mid; break; }
            if (nums[l] <= nums[mid]) {
                if (nums[l] <= target && target < nums[mid]) r = mid - 1;
                else l = mid + 1;
            } else {
                if (nums[mid] < target && target <= nums[r]) l = mid + 1;
                else r = mid - 1;
            }
        }
        System.out.println(ans);
    }
}
""",
                Arrays.asList(
                        new TestCase(null, "7 0\n4 5 6 7 0 1 2", "4", false, 1),
                        new TestCase(null, "7 3\n4 5 6 7 0 1 2", "-1", false, 1),
                        new TestCase(null, "1 0\n1", "-1", true, 1)
                )
        ));

        questions.add(createJavaQ(
                "ARRAYS",
                "Sort Colors Dutch National Flag",
                "Given an array `nums` with n objects colored red, white, or blue (represented as 0, 1, and 2), sort them in-place so that objects of the same color are adjacent in the order 0, 1, 2.",
                "First line contains integer N.\nSecond line contains N space-separated numbers (0, 1, or 2).",
                "Print the sorted numbers separated by space.",
                "1 <= N <= 30000\nnums[i] is 0, 1, or 2.",
                """
import java.util.*;

public class Solution {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextInt()) return;
        int n = sc.nextInt();
        int[] nums = new int[n];
        for (int i = 0; i < n; i++) nums[i] = sc.nextInt();
        int low = 0, mid = 0, high = n - 1;
        while (mid <= high) {
            if (nums[mid] == 0) {
                int t = nums[low]; nums[low] = nums[mid]; nums[mid] = t;
                low++; mid++;
            } else if (nums[mid] == 1) {
                mid++;
            } else {
                int t = nums[mid]; nums[mid] = nums[high]; nums[high] = t;
                high--;
            }
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) sb.append(nums[i]).append(i == n - 1 ? "" : " ");
        System.out.println(sb.toString());
    }
}
""",
                Arrays.asList(
                        new TestCase(null, "6\n2 0 2 1 1 0", "0 0 1 1 2 2", false, 1),
                        new TestCase(null, "3\n2 0 1", "0 1 2", false, 1),
                        new TestCase(null, "2\n1 0", "0 1", true, 1)
                )
        ));

        questions.add(createJavaQ(
                "ARRAYS",
                "Rotate Array by K Steps",
                "Given an integer array `nums`, rotate the array to the right by `k` steps, where k is non-negative.",
                "First line contains integers N and K.\nSecond line contains N space-separated integers.",
                "Print the rotated array elements separated by space.",
                "1 <= N <= 100000\n0 <= K <= 100000",
                """
import java.util.*;

public class Solution {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextInt()) return;
        int n = sc.nextInt();
        int k = sc.nextInt() % n;
        int[] nums = new int[n];
        for (int i = 0; i < n; i++) nums[i] = sc.nextInt();
        reverse(nums, 0, n - 1);
        reverse(nums, 0, k - 1);
        reverse(nums, k, n - 1);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) sb.append(nums[i]).append(i == n - 1 ? "" : " ");
        System.out.println(sb.toString());
    }
    static void reverse(int[] a, int i, int j) {
        while (i < j) { int t = a[i]; a[i] = a[j]; a[j] = t; i++; j--; }
    }
}
""",
                Arrays.asList(
                        new TestCase(null, "7 3\n1 2 3 4 5 6 7", "5 6 7 1 2 3 4", false, 1),
                        new TestCase(null, "4 2\n-1 -100 3 99", "3 99 -1 -100", false, 1)
                )
        ));

        questions.add(createJavaQ(
                "ARRAYS",
                "Subarrays with Sum Divisible by K",
                "Given an integer array `nums` and an integer `k`, return the number of non-empty subarrays that have a sum divisible by `k`.",
                "First line contains integers N and K.\nSecond line contains N space-separated integers.",
                "Print a single integer representing total qualifying subarrays.",
                "1 <= N <= 30000\n2 <= K <= 10000",
                """
import java.util.*;

public class Solution {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextInt()) return;
        int n = sc.nextInt();
        int k = sc.nextInt();
        int[] count = new int[k];
        count[0] = 1;
        int prefix = 0, res = 0;
        for (int i = 0; i < n; i++) {
            prefix = (prefix + sc.nextInt()) % k;
            if (prefix < 0) prefix += k;
            res += count[prefix];
            count[prefix]++;
        }
        System.out.println(res);
    }
}
""",
                Arrays.asList(
                        new TestCase(null, "6 5\n4 5 0 -2 -3 1", "7", false, 1),
                        new TestCase(null, "1 5\n5", "1", false, 1)
                )
        ));

        questions.add(createJavaQ(
                "ARRAYS",
                "Find Peak Element",
                "A peak element is an element that is strictly greater than its neighbors. Given a 0-indexed integer array `nums`, find any peak element and return its index in O(log n) time.",
                "First line contains integer N.\nSecond line contains N space-separated integers.",
                "Print the index of the peak element.",
                "1 <= N <= 100000\nnums[i] != nums[i + 1]",
                """
import java.util.*;

public class Solution {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextInt()) return;
        int n = sc.nextInt();
        int[] nums = new int[n];
        for (int i = 0; i < n; i++) nums[i] = sc.nextInt();
        int l = 0, r = n - 1;
        while (l < r) {
            int mid = l + (r - l) / 2;
            if (nums[mid] > nums[mid + 1]) r = mid;
            else l = mid + 1;
        }
        System.out.println(l);
    }
}
""",
                Arrays.asList(
                        new TestCase(null, "4\n1 2 3 1", "2", false, 1),
                        new TestCase(null, "1\n5", "0", false, 1)
                )
        ));

        // ----------------------------------------------------
        // SECTION B: STRING MANIPULATION (JAVA)
        // ----------------------------------------------------

        questions.add(createJavaQ(
                "STRINGS",
                "Longest Palindromic Substring Length",
                "Given a string `s`, return the length of the longest palindromic substring in `s`.",
                "A single line containing string s.",
                "Print an integer representing the maximum length of a palindromic substring.",
                "1 <= s.length <= 1000\ns consists of only digits and English letters.",
                """
import java.util.*;

public class Solution {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String s = sc.hasNextLine() ? sc.nextLine().trim() : "";
        if (s.isEmpty()) { System.out.println(0); return; }
        int maxLen = 1;
        for (int i = 0; i < s.length(); i++) {
            maxLen = Math.max(maxLen, expand(s, i, i));
            maxLen = Math.max(maxLen, expand(s, i, i + 1));
        }
        System.out.println(maxLen);
    }
    static int expand(String s, int l, int r) {
        while (l >= 0 && r < s.length() && s.charAt(l) == s.charAt(r)) { l--; r++; }
        return r - l - 1;
    }
}
""",
                Arrays.asList(
                        new TestCase(null, "babad", "3", false, 1),
                        new TestCase(null, "cbbd", "2", false, 1),
                        new TestCase(null, "a", "1", true, 1),
                        new TestCase(null, "racecar", "7", true, 1)
                )
        ));

        questions.add(createJavaQ(
                "STRINGS",
                "Palindromic Substrings Count",
                "Given a string `s`, return the number of palindromic substrings in it. A substring is a contiguous sequence of characters within the string.",
                "Single line containing string s.",
                "Print the total count of palindromic substrings.",
                "1 <= s.length <= 1000\ns consists of lowercase English letters.",
                """
import java.util.*;

public class Solution {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String s = sc.hasNextLine() ? sc.nextLine().trim() : "";
        int count = 0;
        for (int i = 0; i < s.length(); i++) {
            count += countPal(s, i, i);
            count += countPal(s, i, i + 1);
        }
        System.out.println(count);
    }
    static int countPal(String s, int l, int r) {
        int c = 0;
        while (l >= 0 && r < s.length() && s.charAt(l) == s.charAt(r)) { c++; l--; r++; }
        return c;
    }
}
""",
                Arrays.asList(
                        new TestCase(null, "abc", "3", false, 1),
                        new TestCase(null, "aaa", "6", false, 1),
                        new TestCase(null, "xkjkx", "7", true, 1)
                )
        ));

        questions.add(createJavaQ(
                "STRINGS",
                "Reverse Words in a String",
                "Given an input string `s`, reverse the order of the words. A word is defined as a sequence of non-space characters. The words in `s` will be separated by at least one space. Return a string of the words in reverse order concatenated by a single space.",
                "Single line containing string s.",
                "Print reversed words separated by single space.",
                "1 <= s.length <= 10000",
                """
import java.util.*;

public class Solution {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String s = sc.hasNextLine() ? sc.nextLine() : "";
        String[] words = s.trim().split("\\\\s+");
        StringBuilder sb = new StringBuilder();
        for (int i = words.length - 1; i >= 0; i--) {
            sb.append(words[i]).append(i == 0 ? "" : " ");
        }
        System.out.println(sb.toString());
    }
}
""",
                Arrays.asList(
                        new TestCase(null, "the sky is blue", "blue is sky the", false, 1),
                        new TestCase(null, "  hello world  ", "world hello", false, 1),
                        new TestCase(null, "a good   example", "example good a", true, 1)
                )
        ));

        questions.add(createJavaQ(
                "STRINGS",
                "Find All Anagrams in a String",
                "Given two strings `s` and `p`, return the total count of start indices of `p`'s anagrams in `s`.",
                "First line contains string s.\nSecond line contains string p.",
                "Print count of anagram start positions found in s.",
                "1 <= s.length, p.length <= 30000\ns and p consist of lowercase English letters.",
                """
import java.util.*;

public class Solution {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String s = sc.hasNextLine() ? sc.nextLine().trim() : "";
        String p = sc.hasNextLine() ? sc.nextLine().trim() : "";
        if (s.length() < p.length()) { System.out.println(0); return; }
        int[] pCount = new int[26];
        int[] sCount = new int[26];
        for (char c : p.toCharArray()) pCount[c - 'a']++;
        int k = p.length(), matches = 0;
        for (int i = 0; i < s.length(); i++) {
            sCount[s.charAt(i) - 'a']++;
            if (i >= k) sCount[s.charAt(i - k) - 'a']--;
            if (i >= k - 1 && Arrays.equals(sCount, pCount)) matches++;
        }
        System.out.println(matches);
    }
}
""",
                Arrays.asList(
                        new TestCase(null, "cbaebabacd\nabc", "2", false, 1),
                        new TestCase(null, "abab\nab", "3", false, 1),
                        new TestCase(null, "af\nbe", "0", true, 1)
                )
        ));

        questions.add(createJavaQ(
                "STRINGS",
                "String Compression Run Length",
                "Given an array of characters, compress it using run-length encoding. Return the length of the compressed string representation.",
                "Single line containing characters separated by space.",
                "Print integer representing length of compressed representation.",
                "1 <= characters <= 10000",
                """
import java.util.*;

public class Solution {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        List<Character> list = new ArrayList<>();
        while (sc.hasNext()) {
            String token = sc.next();
            if (!token.isEmpty()) list.add(token.charAt(0));
        }
        if (list.isEmpty()) { System.out.println(0); return; }
        int len = 0;
        int i = 0;
        while (i < list.size()) {
            int j = i;
            while (j < list.size() && list.get(j).equals(list.get(i))) j++;
            int count = j - i;
            len++;
            if (count > 1) len += String.valueOf(count).length();
            i = j;
        }
        System.out.println(len);
    }
}
""",
                Arrays.asList(
                        new TestCase(null, "a a b b c c c", "6", false, 1),
                        new TestCase(null, "a", "1", false, 1),
                        new TestCase(null, "a b b b b b b b b b b b", "4", true, 1)
                )
        ));

        questions.add(createJavaQ(
                "STRINGS",
                "Decode String with Multipliers",
                "Given an encoded string, return its decoded string. The encoding rule is: `k[encoded_string]`, where the encoded_string inside the square brackets is being repeated exactly k times.",
                "Single line containing encoded string.",
                "Print decoded string.",
                "1 <= s.length <= 1000\nk is a positive integer.",
                """
import java.util.*;

public class Solution {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String s = sc.hasNextLine() ? sc.nextLine().trim() : "";
        Deque<Integer> countStack = new ArrayDeque<>();
        Deque<StringBuilder> stringStack = new ArrayDeque<>();
        StringBuilder curr = new StringBuilder();
        int k = 0;
        for (char ch : s.toCharArray()) {
            if (Character.isDigit(ch)) {
                k = k * 10 + (ch - '0');
            } else if (ch == '[') {
                countStack.push(k);
                stringStack.push(curr);
                curr = new StringBuilder();
                k = 0;
            } else if (ch == ']') {
                StringBuilder decoded = stringStack.pop();
                int repeat = countStack.pop();
                for (int i = 0; i < repeat; i++) decoded.append(curr);
                curr = decoded;
            } else {
                curr.append(ch);
            }
        }
        System.out.println(curr.toString());
    }
}
""",
                Arrays.asList(
                        new TestCase(null, "3[a]2[bc]", "aaabcbc", false, 1),
                        new TestCase(null, "3[a2[c]]", "accaccacc", false, 1),
                        new TestCase(null, "2[abc]3[cd]ef", "abcabccdcdcdef", true, 1)
                )
        ));

        questions.add(createJavaQ(
                "STRINGS",
                "Longest Repeating Character Replacement",
                "You are given a string `s` and an integer `k`. You can choose any character of the string and change it to any other uppercase English character at most `k` times. Return the length of the longest substring containing the same letter you can get.",
                "First line contains integer K.\nSecond line contains string s.",
                "Print maximum length of uniform substring.",
                "1 <= s.length <= 100000\n0 <= K <= s.length",
                """
import java.util.*;

public class Solution {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextInt()) return;
        int k = sc.nextInt();
        String s = sc.next();
        int[] count = new int[26];
        int maxCount = 0, maxLen = 0, l = 0;
        for (int r = 0; r < s.length(); r++) {
            maxCount = Math.max(maxCount, ++count[s.charAt(r) - 'A']);
            while ((r - l + 1) - maxCount > k) {
                count[s.charAt(l) - 'A']--;
                l++;
            }
            maxLen = Math.max(maxLen, r - l + 1);
        }
        System.out.println(maxLen);
    }
}
""",
                Arrays.asList(
                        new TestCase(null, "2\nABAB", "4", false, 1),
                        new TestCase(null, "1\nAABABBA", "4", false, 1)
                )
        ));

        // ----------------------------------------------------
        // SECTION C: DATA STRUCTURES (JAVA)
        // ----------------------------------------------------

        questions.add(createJavaQ(
                "DATA_STRUCTURES",
                "Daily Temperatures Warmer Days",
                "Given an array of integers temperatures represents the daily temperatures, return an array answer such that answer[i] is the number of days you have to wait after the ith day to get a warmer temperature. If there is no future day for which this is possible, keep answer[i] == 0 instead.",
                "First line contains integer N.\nSecond line contains N space-separated integers.",
                "Print N space-separated integers representing waiting days.",
                "1 <= N <= 100000\n30 <= temperatures[i] <= 100",
                """
import java.util.*;

public class Solution {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextInt()) return;
        int n = sc.nextInt();
        int[] t = new int[n];
        for (int i = 0; i < n; i++) t[i] = sc.nextInt();
        int[] res = new int[n];
        Deque<Integer> stack = new ArrayDeque<>();
        for (int i = 0; i < n; i++) {
            while (!stack.isEmpty() && t[i] > t[stack.peek()]) {
                int prev = stack.pop();
                res[prev] = i - prev;
            }
            stack.push(i);
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) sb.append(res[i]).append(i == n - 1 ? "" : " ");
        System.out.println(sb.toString());
    }
}
""",
                Arrays.asList(
                        new TestCase(null, "8\n73 74 75 71 69 72 76 73", "1 1 4 2 1 1 0 0", false, 1),
                        new TestCase(null, "4\n30 40 50 60", "1 1 1 0", false, 1),
                        new TestCase(null, "3\n30 60 90", "1 1 0", true, 1)
                )
        ));

        questions.add(createJavaQ(
                "DATA_STRUCTURES",
                "Asteroid Collision Simulation",
                "We are given an array `asteroids` of integers representing asteroids in a row. For each asteroid, the absolute value represents its size, and the sign represents its direction (positive meaning right, negative meaning left). Find out the state of the asteroids after all collisions.",
                "First line contains integer N.\nSecond line contains N space-separated asteroid sizes.",
                "Print surviving asteroids separated by space, or empty if none survive.",
                "2 <= N <= 10000\n-1000 <= asteroids[i] <= 1000",
                """
import java.util.*;

public class Solution {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextInt()) return;
        int n = sc.nextInt();
        int[] a = new int[n];
        for (int i = 0; i < n; i++) a[i] = sc.nextInt();
        Deque<Integer> s = new ArrayDeque<>();
        for (int ast : a) {
            boolean exploded = false;
            while (!s.isEmpty() && ast < 0 && s.peek() > 0) {
                if (s.peek() < -ast) {
                    s.pop();
                    continue;
                } else if (s.peek() == -ast) {
                    s.pop();
                }
                exploded = true;
                break;
            }
            if (!exploded) s.push(ast);
        }
        List<Integer> res = new ArrayList<>(s);
        Collections.reverse(res);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < res.size(); i++) sb.append(res.get(i)).append(i == res.size() - 1 ? "" : " ");
        System.out.println(sb.toString());
    }
}
""",
                Arrays.asList(
                        new TestCase(null, "3\n5 10 -5", "5 10", false, 1),
                        new TestCase(null, "2\n8 -8", "", false, 1),
                        new TestCase(null, "3\n10 2 -5", "10", true, 1)
                )
        ));

        questions.add(createJavaQ(
                "DATA_STRUCTURES",
                "Simplify Unix File Path",
                "Given a string `path`, which is an absolute path to a file or directory in a Unix-style file system, convert it to the simplified canonical path.",
                "Single line containing path string.",
                "Print canonical Unix path.",
                "1 <= path.length <= 3000",
                """
import java.util.*;

public class Solution {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String path = sc.hasNextLine() ? sc.nextLine().trim() : "/";
        Deque<String> stack = new ArrayDeque<>();
        for (String p : path.split("/")) {
            if (p.equals("..")) {
                if (!stack.isEmpty()) stack.pop();
            } else if (!p.isEmpty() && !p.equals(".")) {
                stack.push(p);
            }
        }
        List<String> list = new ArrayList<>(stack);
        Collections.reverse(list);
        System.out.println("/" + String.join("/", list));
    }
}
""",
                Arrays.asList(
                        new TestCase(null, "/home/", "/home", false, 1),
                        new TestCase(null, "/../", "/", false, 1),
                        new TestCase(null, "/home//foo/", "/home/foo", true, 1)
                )
        ));

        questions.add(createJavaQ(
                "DATA_STRUCTURES",
                "Remove K Digits for Smallest Number",
                "Given string num representing a non-negative integer and an integer k, return the smallest possible integer after removing k digits from num.",
                "First line contains integer K.\nSecond line contains numeric string num.",
                "Print the smallest integer resulting after removal.",
                "1 <= k <= num.length <= 100000",
                """
import java.util.*;

public class Solution {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextInt()) return;
        int k = sc.nextInt();
        String num = sc.next();
        Deque<Character> stack = new ArrayDeque<>();
        for (char c : num.toCharArray()) {
            while (!stack.isEmpty() && k > 0 && stack.peek() > c) {
                stack.pop();
                k--;
            }
            stack.push(c);
        }
        while (k > 0 && !stack.isEmpty()) { stack.pop(); k--; }
        StringBuilder sb = new StringBuilder();
        while (!stack.isEmpty()) sb.append(stack.pop());
        sb.reverse();
        while (sb.length() > 1 && sb.charAt(0) == '0') sb.deleteCharAt(0);
        System.out.println(sb.length() == 0 ? "0" : sb.toString());
    }
}
""",
                Arrays.asList(
                        new TestCase(null, "3\n1432219", "1219", false, 1),
                        new TestCase(null, "1\n10200", "200", false, 1),
                        new TestCase(null, "2\n10", "0", true, 1)
                )
        ));

        questions.add(createJavaQ(
                "DATA_STRUCTURES",
                "Online Stock Span",
                "Design an algorithm that collects daily price quotes for some stock and returns the span of that stock's price for the current day. The span of the stock's price today is the maximum number of consecutive days for which the stock price was less than or equal to today's price.",
                "First line contains integer N.\nSecond line contains N space-separated stock prices.",
                "Print N space-separated integers representing spans.",
                "1 <= N <= 100000\n1 <= price <= 100000",
                """
import java.util.*;

public class Solution {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextInt()) return;
        int n = sc.nextInt();
        int[] prices = new int[n];
        for (int i = 0; i < n; i++) prices[i] = sc.nextInt();
        Deque<int[]> stack = new ArrayDeque<>();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            int price = prices[i];
            int span = 1;
            while (!stack.isEmpty() && stack.peek()[0] <= price) {
                span += stack.pop()[1];
            }
            stack.push(new int[]{price, span});
            sb.append(span).append(i == n - 1 ? "" : " ");
        }
        System.out.println(sb.toString());
    }
}
""",
                Arrays.asList(
                        new TestCase(null, "7\n100 80 60 70 60 75 85", "1 1 1 2 1 4 6", false, 1),
                        new TestCase(null, "3\n10 10 10", "1 2 3", false, 1)
                )
        ));

        // ----------------------------------------------------
        // SECTION D: SQL QUESTIONS (JOINS, AGGREGATION, WINDOW)
        // ----------------------------------------------------

        questions.add(createSqlQ(
                "JOINS",
                "Students and Course Enrollment Status",
                "Retrieve all students with their enrolled courses and grade status. Include students who have not enrolled in any course (display 'NOT ENROLLED'). Order by student_name ASC, course_name ASC.",
                "SELECT s.name AS student_name, COALESCE(c.title, 'NOT ENROLLED') AS course_name FROM students s LEFT JOIN enrollments e ON s.id = e.student_id LEFT JOIN courses c ON e.course_id = c.id ORDER BY student_name ASC, course_name ASC;",
                "### Schema\n- `students(id, name)`\n- `courses(id, title)`\n- `enrollments(student_id, course_id, grade)`",
                "CREATE TABLE students (id INT PRIMARY KEY, name VARCHAR(50)); CREATE TABLE courses (id INT PRIMARY KEY, title VARCHAR(50)); CREATE TABLE enrollments (student_id INT, course_id INT, grade VARCHAR(2));",
                "INSERT INTO students VALUES (1, 'Amy'), (2, 'Brian'), (3, 'Chloe'); INSERT INTO courses VALUES (10, 'CompSci 101'), (20, 'Data Structures'); INSERT INTO enrollments VALUES (1, 10, 'A'), (1, 20, 'B'), (2, 10, 'A');"
        ));

        questions.add(createSqlQ(
                "JOINS",
                "Supplier Product Fulfillment and Stock",
                "List all product names with their supplier company name and current inventory quantity. Only show products where inventory quantity is greater than 20. Order by product_name ASC.",
                "SELECT p.name AS product_name, s.company_name, p.quantity FROM products p JOIN suppliers s ON p.supplier_id = s.id WHERE p.quantity > 20 ORDER BY product_name ASC;",
                "### Schema\n- `suppliers(id, company_name)`\n- `products(id, name, supplier_id, quantity)`",
                "CREATE TABLE suppliers (id INT PRIMARY KEY, company_name VARCHAR(50)); CREATE TABLE products (id INT PRIMARY KEY, name VARCHAR(50), supplier_id INT, quantity INT);",
                "INSERT INTO suppliers VALUES (1, 'Acme Corp'), (2, 'Global Supplies'); INSERT INTO products VALUES (101, 'Widget A', 1, 50), (102, 'Widget B', 1, 10), (103, 'Gadget X', 2, 80);"
        ));

        questions.add(createSqlQ(
                "AGGREGATION",
                "Monthly Active Subscriptions and Revenue",
                "Calculate total revenue and subscriber count per plan type for subscribers who joined in 2024. Only include plans with at least 2 subscribers. Order by total_revenue DESC.",
                "SELECT plan_type, COUNT(id) AS subscribers_count, SUM(price) AS total_revenue FROM subscriptions GROUP BY plan_type HAVING COUNT(id) >= 2 ORDER BY total_revenue DESC;",
                "### Schema\n- `subscriptions(id, plan_type, price, start_date)`",
                "CREATE TABLE subscriptions (id INT PRIMARY KEY, plan_type VARCHAR(50), price DECIMAL(10,2), start_date DATE);",
                "INSERT INTO subscriptions VALUES (1, 'Premium', 19.99, '2024-01-01'), (2, 'Premium', 19.99, '2024-02-15'), (3, 'Basic', 9.99, '2024-01-20'), (4, 'Basic', 9.99, '2024-03-01'), (5, 'Enterprise', 99.99, '2024-02-01');"
        ));

        questions.add(createSqlQ(
                "AGGREGATION",
                "Vendor Delivery Delay Metrics",
                "Find the vendor name, average delivery delay in days (rounded to 1 decimal place), and total orders fulfilled for vendors having more than 1 shipment. Order by avg_delay DESC.",
                "SELECT vendor_name, ROUND(AVG(delay_days), 1) AS avg_delay, COUNT(id) AS total_shipments FROM deliveries GROUP BY vendor_name HAVING COUNT(id) > 1 ORDER BY avg_delay DESC;",
                "### Schema\n- `deliveries(id, vendor_name, delay_days)`",
                "CREATE TABLE deliveries (id INT PRIMARY KEY, vendor_name VARCHAR(50), delay_days INT);",
                "INSERT INTO deliveries VALUES (1, 'FastFreight', 2), (2, 'FastFreight', 4), (3, 'QuickShip', 1), (4, 'QuickShip', 1), (5, 'SlowMove', 7);"
        ));

        questions.add(createSqlQ(
                "WINDOW_FUNCTIONS",
                "Rank Customer Orders by Value",
                "Using the RANK() window function, rank each customer's orders from highest amount to lowest amount. Output customer_id, order_id, amount, and order_rank. Order by customer_id ASC, order_rank ASC.",
                "SELECT customer_id, id AS order_id, amount, RANK() OVER (PARTITION BY customer_id ORDER BY amount DESC) AS order_rank FROM orders ORDER BY customer_id ASC, order_rank ASC;",
                "### Schema\n- `orders(id, customer_id, amount)`",
                "CREATE TABLE orders (id INT PRIMARY KEY, customer_id INT, amount DECIMAL(10,2));",
                "INSERT INTO orders VALUES (1, 101, 150.00), (2, 101, 300.00), (3, 101, 75.00), (4, 102, 500.00), (5, 102, 250.00);"
        ));

        questions.add(createSqlQ(
                "WINDOW_FUNCTIONS",
                "Salary Gap with Immediate Senior",
                "For each employee, compute their salary and the salary of the next higher earner in the company using LEAD(). Output employee name, salary, and next_higher_salary. Order by salary ASC.",
                "SELECT name, salary, LEAD(salary) OVER (ORDER BY salary ASC) AS next_higher_salary FROM staff ORDER BY salary ASC;",
                "### Schema\n- `staff(id, name, salary)`",
                "CREATE TABLE staff (id INT PRIMARY KEY, name VARCHAR(50), salary INT);",
                "INSERT INTO staff VALUES (1, 'Alice', 60000), (2, 'Bob', 75000), (3, 'Charlie', 90000), (4, 'David', 120000);"
        ));

        // ----------------------------------------------------
        // SECTION E: ENGLISH SPEAKING TOPICS
        // ----------------------------------------------------

        questions.add(createEnglishQ(
                "Distributed System Architecture & Trade-off Decisions",
                "Describe a distributed system, cloud service, or microservice architecture you designed or maintained. Explain the trade-offs you made between consistency, availability, latency, and cost."
        ));

        questions.add(createEnglishQ(
                "Refactoring Legacy Codebase & Quality Engineering",
                "Talk about a significant refactoring initiative you undertook on a legacy codebase. How did you manage technical debt, maintain backward compatibility, and ensure test coverage during the migration?"
        ));

        questions.add(createEnglishQ(
                "High Concurrency & Database Bottlenecks",
                "Explain how you resolved a high concurrency database bottleneck or race condition in a production application. Discuss your locking strategy, indexing improvements, or caching layers."
        ));

        // Additional Java Questions to exceed 50 total questions
        questions.add(createJavaQ(
                "ARRAYS",
                "Longest Consecutive Sequence in Array",
                "Given an unsorted array of integers nums, return the length of the longest consecutive elements sequence in O(n) time.",
                "First line contains integer N.\nSecond line contains N space-separated integers.",
                "Print an integer representing the length of the longest consecutive sequence.",
                "0 <= N <= 100000\n-1000000 <= nums[i] <= 1000000",
                """
import java.util.*;

public class Solution {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextInt()) { System.out.println(0); return; }
        int n = sc.nextInt();
        Set<Integer> set = new HashSet<>();
        for (int i = 0; i < n; i++) set.add(sc.nextInt());
        int longest = 0;
        for (int num : set) {
            if (!set.contains(num - 1)) {
                int curr = num;
                int streak = 1;
                while (set.contains(curr + 1)) { curr++; streak++; }
                longest = Math.max(longest, streak);
            }
        }
        System.out.println(longest);
    }
}
""",
                Arrays.asList(
                        new TestCase(null, "6\n100 4 200 1 3 2", "4", false, 1),
                        new TestCase(null, "10\n0 3 7 2 5 8 4 6 0 1", "9", false, 1)
                )
        ));

        questions.add(createJavaQ(
                "ARRAYS",
                "Jump Game Reachability",
                "You are given an integer array nums. You are initially positioned at the array's first index, and each element in the array represents your maximum jump length at that position. Return 1 if you can reach the last index, or 0 otherwise.",
                "First line contains integer N.\nSecond line contains N space-separated integers.",
                "Print 1 if reachable, 0 otherwise.",
                "1 <= N <= 30000\n0 <= nums[i] <= 100000",
                """
import java.util.*;

public class Solution {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextInt()) return;
        int n = sc.nextInt();
        int[] nums = new int[n];
        for (int i = 0; i < n; i++) nums[i] = sc.nextInt();
        int maxReach = 0;
        boolean canReach = true;
        for (int i = 0; i < n; i++) {
            if (i > maxReach) { canReach = false; break; }
            maxReach = Math.max(maxReach, i + nums[i]);
        }
        System.out.println(canReach ? 1 : 0);
    }
}
""",
                Arrays.asList(
                        new TestCase(null, "5\n2 3 1 1 4", "1", false, 1),
                        new TestCase(null, "5\n3 2 1 0 4", "0", false, 1)
                )
        ));

        questions.add(createJavaQ(
                "ARRAYS",
                "Find All Duplicates in an Array",
                "Given an integer array nums of length n where all the integers of nums are in the range [1, n] and each integer appears at most twice, return all the integers that appear twice in sorted order.",
                "First line contains integer N.\nSecond line contains N space-separated integers.",
                "Print duplicate numbers separated by space in ascending order, or empty line if none.",
                "1 <= N <= 100000\n1 <= nums[i] <= N",
                """
import java.util.*;

public class Solution {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextInt()) return;
        int n = sc.nextInt();
        int[] nums = new int[n];
        for (int i = 0; i < n; i++) nums[i] = sc.nextInt();
        List<Integer> res = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            int idx = Math.abs(nums[i]) - 1;
            if (nums[idx] < 0) res.add(Math.abs(nums[i]));
            else nums[idx] = -nums[idx];
        }
        Collections.sort(res);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < res.size(); i++) sb.append(res.get(i)).append(i == res.size() - 1 ? "" : " ");
        System.out.println(sb.toString());
    }
}
""",
                Arrays.asList(
                        new TestCase(null, "8\n4 3 2 7 8 2 3 1", "2 3", false, 1),
                        new TestCase(null, "3\n1 1 2", "1", false, 1),
                        new TestCase(null, "1\n1", "", true, 1)
                )
        ));

        questions.add(createJavaQ(
                "STRINGS",
                "Letter Combinations of a Phone Number",
                "Given a string containing digits from 2-9 inclusive, return all possible letter combinations that the number could represent. Print the combinations sorted lexicographically separated by space.",
                "Single line containing digits string.",
                "Print space-separated combinations.",
                "0 <= digits.length <= 4\ndigits[i] is a digit in the range ['2', '9'].",
                """
import java.util.*;

public class Solution {
    static final String[] MAP = {"", "", "abc", "def", "ghi", "jkl", "mno", "pqrs", "tuv", "wxyz"};
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String digits = sc.hasNextLine() ? sc.nextLine().trim() : "";
        if (digits.isEmpty()) { System.out.println(""); return; }
        List<String> res = new ArrayList<>();
        backtrack(digits, 0, new StringBuilder(), res);
        Collections.sort(res);
        System.out.println(String.join(" ", res));
    }
    static void backtrack(String d, int idx, StringBuilder cur, List<String> res) {
        if (idx == d.length()) { res.add(cur.toString()); return; }
        String letters = MAP[d.charAt(idx) - '0'];
        for (char ch : letters.toCharArray()) {
            cur.append(ch);
            backtrack(d, idx + 1, cur, res);
            cur.deleteCharAt(cur.length() - 1);
        }
    }
}
""",
                Arrays.asList(
                        new TestCase(null, "23", "ad ae af bd be bf cd ce cf", false, 1),
                        new TestCase(null, "2", "a b c", false, 1)
                )
        ));

        questions.add(createJavaQ(
                "DATA_STRUCTURES",
                "Kth Largest Element in an Array",
                "Given an integer array nums and an integer k, return the kth largest element in the array using a Min-Heap / PriorityQueue in O(n log k) time.",
                "First line contains integers N and K.\nSecond line contains N space-separated integers.",
                "Print the kth largest integer.",
                "1 <= k <= N <= 100000\n-10000 <= nums[i] <= 10000",
                """
import java.util.*;

public class Solution {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextInt()) return;
        int n = sc.nextInt();
        int k = sc.nextInt();
        PriorityQueue<Integer> pq = new PriorityQueue<>(k);
        for (int i = 0; i < n; i++) {
            pq.offer(sc.nextInt());
            if (pq.size() > k) pq.poll();
        }
        System.out.println(pq.peek());
    }
}
""",
                Arrays.asList(
                        new TestCase(null, "6 2\n3 2 1 5 6 4", "5", false, 1),
                        new TestCase(null, "9 4\n3 2 3 1 2 4 5 5 6", "4", false, 1)
                )
        ));

        questions.add(createSqlQ(
                "JOINS",
                "Project Billing and Hours Allocation",
                "Retrieve each project title, the total billable hours worked across all consultants, and total billable cost. If a project has no logged hours, display 0 for hours and 0.00 for cost. Order by total_cost DESC, title ASC.",
                "SELECT p.title, COALESCE(SUM(l.hours), 0) AS total_hours, COALESCE(SUM(l.hours * c.hourly_rate), 0.00) AS total_cost FROM projects p LEFT JOIN logs l ON p.id = l.project_id LEFT JOIN consultants c ON l.consultant_id = c.id GROUP BY p.title ORDER BY total_cost DESC, p.title ASC;",
                "### Schema\n- `projects(id, title)`\n- `consultants(id, name, hourly_rate)`\n- `logs(project_id, consultant_id, hours)`",
                "CREATE TABLE projects (id INT PRIMARY KEY, title VARCHAR(50)); CREATE TABLE consultants (id INT PRIMARY KEY, name VARCHAR(50), hourly_rate DECIMAL(10,2)); CREATE TABLE logs (project_id INT, consultant_id INT, hours INT);",
                "INSERT INTO projects VALUES (1, 'Cloud Migration'), (2, 'Security Audit'), (3, 'AI Chatbot'); INSERT INTO consultants VALUES (10, 'Dave', 120.00), (20, 'Eve', 150.00); INSERT INTO logs VALUES (1, 10, 40), (1, 20, 20), (2, 10, 10);"
        ));

        questions.add(createSqlQ(
                "AGGREGATION",
                "Hospital Patient Admissions and Doctor Workload",
                "Find doctor specialization, total patient count admitted under doctors in that specialization, and the average stay in days (rounded to 1 decimal place). Only include specializations with more than 1 patient. Order by total_patients DESC.",
                "SELECT d.specialization, COUNT(a.patient_id) AS total_patients, ROUND(AVG(a.stay_days), 1) AS avg_stay_days FROM doctors d JOIN admissions a ON d.id = a.doctor_id GROUP BY d.specialization HAVING COUNT(a.patient_id) > 1 ORDER BY total_patients DESC;",
                "### Schema\n- `doctors(id, name, specialization)`\n- `admissions(patient_id, doctor_id, stay_days)`",
                "CREATE TABLE doctors (id INT PRIMARY KEY, name VARCHAR(50), specialization VARCHAR(50)); CREATE TABLE admissions (patient_id INT, doctor_id INT, stay_days INT);",
                "INSERT INTO doctors VALUES (1, 'Dr. Smith', 'Cardiology'), (2, 'Dr. Jones', 'Cardiology'), (3, 'Dr. Patel', 'Neurology'); INSERT INTO admissions VALUES (101, 1, 4), (102, 2, 6), (103, 3, 2), (104, 1, 5);"
        ));

        questions.add(createSqlQ(
                "WINDOW_FUNCTIONS",
                "Cumulative Donations by Campaign",
                "Calculate running total donations for each charity campaign ordered by donation_date. Return campaign_name, donation_date, amount, and cumulative_donations. Order by campaign_name ASC, donation_date ASC.",
                "SELECT campaign_name, donation_date, amount, SUM(amount) OVER (PARTITION BY campaign_name ORDER BY donation_date ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW) AS cumulative_donations FROM donations ORDER BY campaign_name ASC, donation_date ASC;",
                "### Schema\n- `donations(id, campaign_name, donation_date, amount)`",
                "CREATE TABLE donations (id INT PRIMARY KEY, campaign_name VARCHAR(50), donation_date DATE, amount DECIMAL(10,2));",
                "INSERT INTO donations VALUES (1, 'CleanWater', '2024-01-01', 100.00), (2, 'CleanWater', '2024-01-05', 250.00), (3, 'ForestTree', '2024-01-02', 300.00), (4, 'ForestTree', '2024-01-07', 150.00);"
        ));

        questions.add(createJavaQ(
                "ARRAYS",
                "House Robber Dynamic Selection",
                "You are a professional robber planning to rob houses along a street. Each house has a certain amount of money stashed, the only constraint stopping you from robbing each of them is that adjacent houses have security systems connected and it will automatically contact the police if two adjacent houses were broken into on the same night. Return the maximum amount of money you can rob tonight without alerting the police.",
                "First line contains integer N.\nSecond line contains N space-separated integers representing money in each house.",
                "Print the maximum loot possible.",
                "1 <= N <= 10000\n0 <= nums[i] <= 1000",
                """
import java.util.*;

public class Solution {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextInt()) return;
        int n = sc.nextInt();
        int[] nums = new int[n];
        for (int i = 0; i < n; i++) nums[i] = sc.nextInt();
        if (n == 1) { System.out.println(nums[0]); return; }
        int prev2 = 0, prev1 = 0;
        for (int x : nums) {
            int cur = Math.max(prev1, prev2 + x);
            prev2 = prev1;
            prev1 = cur;
        }
        System.out.println(prev1);
    }
}
""",
                Arrays.asList(
                        new TestCase(null, "4\n1 2 3 1", "4", false, 1),
                        new TestCase(null, "5\n2 7 9 3 1", "12", false, 1),
                        new TestCase(null, "1\n10", "10", true, 1)
                )
        ));

        questions.add(createJavaQ(
                "STRINGS",
                "Valid Palindrome After Cleanup",
                "A phrase is a palindrome if, after converting all uppercase letters into lowercase letters and removing all non-alphanumeric characters, it reads the same forward and backward. Alphanumeric characters include letters and numbers. Given a string s, print 1 if it is a palindrome, or 0 otherwise.",
                "Single line containing string s.",
                "Print 1 if palindrome, 0 otherwise.",
                "1 <= s.length <= 200000",
                """
import java.util.*;

public class Solution {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String s = sc.hasNextLine() ? sc.nextLine() : "";
        int l = 0, r = s.length() - 1;
        boolean isPal = true;
        while (l < r) {
            while (l < r && !Character.isLetterOrDigit(s.charAt(l))) l++;
            while (l < r && !Character.isLetterOrDigit(s.charAt(r))) r--;
            if (Character.toLowerCase(s.charAt(l)) != Character.toLowerCase(s.charAt(r))) {
                isPal = false;
                break;
            }
            l++; r--;
        }
        System.out.println(isPal ? 1 : 0);
    }
}
""",
                Arrays.asList(
                        new TestCase(null, "A man, a plan, a canal: Panama", "1", false, 1),
                        new TestCase(null, "race a car", "0", false, 1),
                        new TestCase(null, " ", "1", true, 1)
                )
        ));

        questions.add(createJavaQ(
                "DATA_STRUCTURES",
                "Implement Stack using Queues",
                "Implement a last-in-first-out (LIFO) stack using only two standard queues. Given a sequence of PUSH x and POP operations, print the popped elements.",
                "First line contains integer N (number of operations).\nNext N lines contain operations 'PUSH x' or 'POP'.",
                "Print each popped element on a new line.",
                "1 <= N <= 10000",
                """
import java.util.*;

public class Solution {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextInt()) return;
        int n = sc.nextInt();
        Deque<Integer> q1 = new ArrayDeque<>();
        for (int i = 0; i < n; i++) {
            String op = sc.next();
            if (op.equals("PUSH")) {
                int x = sc.nextInt();
                q1.add(x);
                int sz = q1.size();
                while (sz > 1) { q1.add(q1.remove()); sz--; }
            } else if (op.equals("POP")) {
                if (!q1.isEmpty()) {
                    System.out.println(q1.remove());
                }
            }
        }
    }
}
""",
                Arrays.asList(
                        new TestCase(null, "4\nPUSH 1\nPUSH 2\nPOP\nPOP", "2\n1", false, 1),
                        new TestCase(null, "3\nPUSH 5\nPOP\nPUSH 10", "5", false, 1)
                )
        ));

        questions.add(createJavaQ(
                "DATA_STRUCTURES",
                "Coin Change Minimum Coins",
                "You are given an integer array coins representing coins of different denominations and an integer amount representing a total amount of money. Return the fewest number of coins that you need to make up that amount. If that amount of money cannot be made up by any combination of the coins, return -1.",
                "First line contains integers N (number of coin types) and amount.\nSecond line contains N space-separated coin values.",
                "Print the minimum coins required, or -1.",
                "1 <= N <= 12\n0 <= amount <= 10000",
                """
import java.util.*;

public class Solution {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextInt()) return;
        int n = sc.nextInt();
        int amount = sc.nextInt();
        int[] coins = new int[n];
        for (int i = 0; i < n; i++) coins[i] = sc.nextInt();
        int[] dp = new int[amount + 1];
        Arrays.fill(dp, amount + 1);
        dp[0] = 0;
        for (int i = 1; i <= amount; i++) {
            for (int c : coins) {
                if (i >= c) dp[i] = Math.min(dp[i], dp[i - c] + 1);
            }
        }
        System.out.println(dp[amount] > amount ? -1 : dp[amount]);
    }
}
""",
                Arrays.asList(
                        new TestCase(null, "3 11\n1 2 5", "3", false, 1),
                        new TestCase(null, "1 3\n2", "-1", false, 1),
                        new TestCase(null, "1 0\n1", "0", true, 1)
                )
        ));

        questions.add(createSqlQ(
                "JOINS",
                "Customer Support Ticket Resolution Times",
                "Find each support agent name, total tickets resolved, and average resolution time in minutes. Only show agents who have resolved at least 2 tickets. Order by total_tickets DESC, agent_name ASC.",
                "SELECT a.name AS agent_name, COUNT(t.id) AS total_tickets, ROUND(AVG(t.duration_mins), 1) AS avg_duration FROM agents a JOIN tickets t ON a.id = t.agent_id WHERE t.status = 'RESOLVED' GROUP BY a.name HAVING COUNT(t.id) >= 2 ORDER BY total_tickets DESC, agent_name ASC;",
                "### Schema\n- `agents(id, name)`\n- `tickets(id, agent_id, status, duration_mins)`",
                "CREATE TABLE agents (id INT PRIMARY KEY, name VARCHAR(50)); CREATE TABLE tickets (id INT PRIMARY KEY, agent_id INT, status VARCHAR(20), duration_mins INT);",
                "INSERT INTO agents VALUES (1, 'Sarah'), (2, 'Tom'); INSERT INTO tickets VALUES (101, 1, 'RESOLVED', 25), (102, 1, 'RESOLVED', 35), (103, 1, 'OPEN', 10), (104, 2, 'RESOLVED', 40), (105, 2, 'RESOLVED', 60);"
        ));

        questions.add(createSqlQ(
                "AGGREGATION",
                "Flight Delays by Airport and Airline",
                "Find departure airport code, total delayed departures (delay_mins > 0), and maximum delay recorded for airports with more than 1 delayed flight. Order by total_delays DESC, max_delay DESC.",
                "SELECT origin, COUNT(id) AS total_delays, MAX(delay_mins) AS max_delay FROM flights WHERE delay_mins > 0 GROUP BY origin HAVING COUNT(id) > 1 ORDER BY total_delays DESC, max_delay DESC;",
                "### Schema\n- `flights(id, origin, delay_mins)`",
                "CREATE TABLE flights (id INT PRIMARY KEY, origin VARCHAR(10), delay_mins INT);",
                "INSERT INTO flights VALUES (1, 'JFK', 15), (2, 'JFK', 45), (3, 'LAX', 0), (4, 'LAX', 30), (5, 'ORD', 60), (6, 'ORD', 20);"
        ));

        questions.add(createSqlQ(
                "WINDOW_FUNCTIONS",
                "Running Average Stock Price by Ticker",
                "Calculate the 2-row moving average (current row and immediate preceding row) of closing stock price for each ticker symbol ordered by trade_date. Output ticker, trade_date, close_price, and moving_avg rounded to 2 decimal places. Order by ticker ASC, trade_date ASC.",
                "SELECT ticker, trade_date, close_price, ROUND(AVG(close_price) OVER (PARTITION BY ticker ORDER BY trade_date ROWS BETWEEN 1 PRECEDING AND CURRENT ROW), 2) AS moving_avg FROM stock_quotes ORDER BY ticker ASC, trade_date ASC;",
                "### Schema\n- `stock_quotes(id, ticker, trade_date, close_price)`",
                "CREATE TABLE stock_quotes (id INT PRIMARY KEY, ticker VARCHAR(10), trade_date DATE, close_price DECIMAL(10,2));",
                "INSERT INTO stock_quotes VALUES (1, 'AAPL', '2024-01-01', 180.00), (2, 'AAPL', '2024-01-02', 185.00), (3, 'AAPL', '2024-01-03', 190.00), (4, 'GOOG', '2024-01-01', 140.00), (5, 'GOOG', '2024-01-02', 150.00);"
        ));

        questions.add(createEnglishQ(
                "Automated CI/CD Pipeline Design & Resilience Strategy",
                "Describe how you built or contributed to an automated Continuous Integration and Continuous Deployment (CI/CD) pipeline. Discuss testing gates, artifact generation, canary deployments, and automated rollback strategies."
        ));

        questions.add(createEnglishQ(
                "Technical Trade-offs Between Microservices and Monolithic Architecture",
                "Explain the architectural trade-offs between microservices and modular monoliths based on real-world systems you have built or observed. Under what conditions is a migration between the two architectures justified?"
        ));

        questionRepository.saveAll(questions);
    }

    private Question createJavaQ(String topic, String title, String desc, String inFormat, String outFormat, String constraints, String starter, List<TestCase> testCases) {
        Question q = new Question();
        q.setCategory("JAVA");
        q.setTopic(topic);
        q.setDifficulty("MEDIUM");
        q.setTitle(title);
        q.setDescription(desc);
        q.setInputFormat(inFormat);
        q.setOutputFormat(outFormat);
        q.setConstraints(constraints);
        q.setStarterCode(starter);
        q.setTimeLimitMs(2000);
        q.setMemoryLimitMb(128);
        q.setMaxScore(10);
        for (TestCase tc : testCases) {
            tc.setQuestion(q);
            q.getTestCases().add(tc);
        }
        return q;
    }

    private Question createSqlQ(String topic, String title, String desc, String refQuery, String schemaDesc, String ddl, String seed) {
        Question q = new Question();
        q.setCategory("SQL");
        q.setTopic(topic);
        q.setDifficulty("INTERMEDIATE");
        q.setTitle(title);
        q.setDescription(desc);
        q.setStarterCode("-- Write your SELECT query here\n" + refQuery);
        q.setMaxScore(10);
        SqlDataset ds = new SqlDataset(q, schemaDesc, ddl, seed, refQuery, true, "[]");
        q.setSqlDataset(ds);
        return q;
    }

    private Question createEnglishQ(String title, String desc) {
        Question q = new Question();
        q.setCategory("ENGLISH");
        q.setTopic("COMMUNICATION");
        q.setDifficulty("INTERMEDIATE");
        q.setTitle(title);
        q.setDescription(desc);
        q.setMaxScore(40);
        return q;
    }
}
