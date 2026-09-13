package com.assessment.config;

import com.assessment.model.AttemptQuestion;
import com.assessment.model.Question;
import com.assessment.repository.AttemptQuestionRepository;
import com.assessment.repository.QuestionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Order(3)
public class StarterCodeCleaner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(StarterCodeCleaner.class);

    private final QuestionRepository questionRepository;
    private final AttemptQuestionRepository attemptQuestionRepository;

    public StarterCodeCleaner(QuestionRepository questionRepository, AttemptQuestionRepository attemptQuestionRepository) {
        this.questionRepository = questionRepository;
        this.attemptQuestionRepository = attemptQuestionRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Checking and sanitizing starter codes in question bank...");

        String javaStarter = """
import java.util.*;

public class Solution {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        // TODO: Read input from System.in and print your solution to System.out
        
    }
}
""";

        String sqlStarter = "-- Write your SELECT query here\n";

        List<Question> questions = questionRepository.findAll();
        int updatedCount = 0;
        for (Question q : questions) {
            if ("JAVA".equals(q.getCategory())) {
                q.setStarterCode(javaStarter);
                questionRepository.save(q);
                updatedCount++;
            } else if ("SQL".equals(q.getCategory())) {
                q.setStarterCode(sqlStarter);
                questionRepository.save(q);
                updatedCount++;
            }
        }

        // Also clean up any unsubmitted AttemptQuestions in active attempts
        List<AttemptQuestion> attemptQuestions = attemptQuestionRepository.findAll();
        for (AttemptQuestion aq : attemptQuestions) {
            if ("NOT_ATTEMPTED".equals(aq.getStatus())) {
                aq.setCurrentCode(aq.getQuestion().getStarterCode());
                attemptQuestionRepository.save(aq);
            }
        }

        log.info("Sanitized {} questions in bank: Pre-filled solutions replaced with clean starter skeletons.", updatedCount);
    }
}
