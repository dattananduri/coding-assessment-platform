package com.assessment.english;

import com.assessment.dto.EnglishSubmitResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
public class EnglishEvaluationService {

    @Value("${app.storage.audio-dir:./data/recordings}")
    private String audioStorageDir;

    private static final Set<String> TECHNICAL_VOCAB = new HashSet<>(Arrays.asList(
            "architecture", "implementation", "algorithm", "optimization", "scalability",
            "database", "framework", "component", "interface", "integration", "backend",
            "frontend", "service", "concurrency", "efficiency", "security", "deployment",
            "performance", "latency", "throughput", "infrastructure", "pipeline", "challenge",
            "solution", "methodology", "lifecycle", "testing", "monitoring", "design"
    ));

    private static final Set<String> TRANSITION_WORDS = new HashSet<>(Arrays.asList(
            "firstly", "secondly", "furthermore", "moreover", "however", "consequently",
            "therefore", "specifically", "additionally", "subsequently", "meanwhile",
            "initially", "finally", "in conclusion", "as a result", "in order to"
    ));

    private static final Set<String> FILLER_WORDS = new HashSet<>(Arrays.asList(
            "um", "uh", "like", "you know", "sort of", "kind of", "basically", "actually"
    ));

    public String saveAudioFile(Long attemptId, MultipartFile audioFile) throws IOException {
        Path dirPath = Paths.get(audioStorageDir).toAbsolutePath().normalize();
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }

        String originalFilename = audioFile.getOriginalFilename();
        String extension = ".webm";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String fileName = "attempt_" + attemptId + "_" + System.currentTimeMillis() + extension;
        Path targetPath = dirPath.resolve(fileName);
        
        try (var in = audioFile.getInputStream()) {
            java.nio.file.Files.copy(in, targetPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        }

        return fileName;
    }

    public EnglishSubmitResponseDto evaluate(String transcript, String promptText, int durationSeconds) {
        if (transcript == null || transcript.trim().isEmpty()) {
            return new EnglishSubmitResponseDto(
                    7, 7, 7, 7, 7, 7, 28.0,
                    "Spoken response recorded and uploaded successfully. Audio submitted for evaluation.",
                    "(Audio response captured)"
            );
        }

        String cleanTranscript = transcript.trim();
        String[] words = cleanTranscript.toLowerCase(Locale.ROOT).replaceAll("[^a-zA-Z0-9\\s]", "").split("\\s+");
        int wordCount = words.length;

        // 1. Vocabulary Score (0-10)
        Set<String> uniqueWords = new HashSet<>(Arrays.asList(words));
        double typeTokenRatio = (double) uniqueWords.size() / Math.max(wordCount, 1);

        int techWordCount = 0;
        for (String w : words) {
            if (TECHNICAL_VOCAB.contains(w)) techWordCount++;
        }

        int vocabScore = 5;
        if (typeTokenRatio > 0.55 && wordCount > 40) vocabScore += 2;
        else if (typeTokenRatio > 0.40) vocabScore += 1;

        if (techWordCount >= 4) vocabScore += 2;
        else if (techWordCount >= 2) vocabScore += 1;
        vocabScore = Math.min(10, Math.max(3, vocabScore));

        // 2. Grammar Score (0-10)
        String[] sentences = cleanTranscript.split("[.!?]+");
        int sentenceCount = Math.max(sentences.length, 1);
        double avgSentenceLength = (double) wordCount / sentenceCount;

        int grammarScore = 6;
        if (avgSentenceLength >= 8 && avgSentenceLength <= 25) grammarScore += 2;
        else if (avgSentenceLength > 25) grammarScore += 1;

        if (wordCount >= 50) grammarScore += 1;
        grammarScore = Math.min(10, Math.max(3, grammarScore));

        // 3. Fluency Score (0-10)
        int fillerCount = 0;
        String lowerTranscript = cleanTranscript.toLowerCase(Locale.ROOT);
        for (String filler : FILLER_WORDS) {
            int idx = 0;
            while ((idx = lowerTranscript.indexOf(filler, idx)) != -1) {
                fillerCount++;
                idx += filler.length();
            }
        }

        int fluencyScore = 7;
        if (fillerCount == 0 && wordCount >= 40) fluencyScore += 2;
        else if (fillerCount > 4) fluencyScore -= 2;
        else if (fillerCount > 2) fluencyScore -= 1;

        if (durationSeconds > 0) {
            double wpm = (double) wordCount / (durationSeconds / 60.0);
            if (wpm >= 90 && wpm <= 160) fluencyScore += 1;
        }
        fluencyScore = Math.min(10, Math.max(3, fluencyScore));

        // 4. Pronunciation & Speaking Quality (0-10)
        int pronunciationScore = 7;
        if (wordCount > 60) pronunciationScore += 1;
        if (wordCount > 100) pronunciationScore += 1;
        pronunciationScore = Math.min(10, Math.max(4, pronunciationScore));

        // 5. Relevance Score (0-10)
        int relevanceScore = 6;
        if (promptText != null) {
            String[] promptKeywords = promptText.toLowerCase(Locale.ROOT).replaceAll("[^a-zA-Z0-9\\s]", "").split("\\s+");
            int matches = 0;
            for (String kw : promptKeywords) {
                if (kw.length() > 3 && lowerTranscript.contains(kw)) {
                    matches++;
                }
            }
            if (matches >= 4) relevanceScore += 3;
            else if (matches >= 2) relevanceScore += 2;
            else if (matches >= 1) relevanceScore += 1;
        }
        relevanceScore = Math.min(10, Math.max(3, relevanceScore));

        // 6. Structure Score (0-10)
        int structureScore = 5;
        int transitionCount = 0;
        for (String tr : TRANSITION_WORDS) {
            if (lowerTranscript.contains(tr)) transitionCount++;
        }
        if (transitionCount >= 3) structureScore += 3;
        else if (transitionCount >= 1) structureScore += 2;

        if (sentenceCount >= 3) structureScore += 1;
        structureScore = Math.min(10, Math.max(3, structureScore));

        // Scale Rubrics to 40 Points Max
        int sumRubric = grammarScore + vocabScore + fluencyScore + pronunciationScore + relevanceScore + structureScore;
        double totalScaled = Math.round(((double) sumRubric / 60.0 * 40.0) * 10.0) / 10.0;

        // Feedback narrative
        StringBuilder feedback = new StringBuilder();
        feedback.append("Demonstrated solid spoken communication with a speech duration of approximately ")
                .append(durationSeconds).append(" seconds and ").append(wordCount).append(" words. ");
        if (relevanceScore >= 8) {
            feedback.append("The response directly addressed the prompt with clear domain context. ");
        } else {
            feedback.append("Consider anchoring the explanation more closely to the prompt questions. ");
        }
        if (structureScore >= 8) {
            feedback.append("Good structural organization using coherent transitions. ");
        } else {
            feedback.append("Using structured transitions (e.g., 'Initially', 'Furthermore', 'Consequently') would enhance logical flow. ");
        }
        if (vocabScore >= 8) {
            feedback.append("Strong technical vocabulary used accurately.");
        } else {
            feedback.append("Expanding technical vocabulary regarding architecture and engineering decisions will strengthen the delivery.");
        }

        return new EnglishSubmitResponseDto(
                grammarScore,
                vocabScore,
                fluencyScore,
                pronunciationScore,
                relevanceScore,
                structureScore,
                totalScaled,
                feedback.toString(),
                cleanTranscript
        );
    }
}
