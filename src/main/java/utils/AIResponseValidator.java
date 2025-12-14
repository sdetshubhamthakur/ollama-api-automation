package utils;

import models.ChatResponse;
import org.testng.Assert;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class AIResponseValidator {
    
    // BASIC VALIDATIONS
    
    public static void assertResponseNotEmpty(ChatResponse response) {
        Assert.assertNotNull(response, "Response is null");
        Assert.assertNotNull(response.getContent(), "Response content is null");
        Assert.assertFalse(
            response.getContent().trim().isEmpty(),
            "Response content is empty"
        );
    }
    
    public static void assertResponseComplete(ChatResponse response) {
        assertResponseNotEmpty(response);
        Assert.assertTrue(
            response.getDone(),
            "Response generation not complete"
        );
    }
    
    // LENGTH VALIDATIONS
    
    public static void assertMinLength(ChatResponse response, int minLength) {
        assertResponseNotEmpty(response);
        int actualLength = response.getContent().length();
        Assert.assertTrue(
            actualLength >= minLength,
            String.format("Response too short. Expected >= %d, got %d", 
                         minLength, actualLength)
        );
    }
    
    public static void assertMaxLength(ChatResponse response, int maxLength) {
        assertResponseNotEmpty(response);
        int actualLength = response.getContent().length();
        Assert.assertTrue(
            actualLength <= maxLength,
            String.format("Response too long. Expected <= %d, got %d", 
                         maxLength, actualLength)
        );
    }
    
    public static void assertLengthRange(ChatResponse response, int minLength, int maxLength) {
        assertMinLength(response, minLength);
        assertMaxLength(response, maxLength);
    }
    
    // CONTENT VALIDATIONS
    
    public static void assertContainsKeyword(ChatResponse response, String keyword) {
        assertResponseNotEmpty(response);
        Assert.assertTrue(
            response.getContent().toLowerCase().contains(keyword.toLowerCase()),
            String.format("Response does not contain keyword: '%s'", keyword)
        );
    }
    
    public static void assertContainsAnyKeyword(ChatResponse response, String... keywords) {
        assertResponseNotEmpty(response);
        String content = response.getContent().toLowerCase();
        boolean found = Arrays.stream(keywords)
            .anyMatch(keyword -> content.contains(keyword.toLowerCase()));
        
        Assert.assertTrue(
            found,
            String.format("Response does not contain any of: %s", 
                         Arrays.toString(keywords))
        );
    }
    
    public static void assertContainsAllKeywords(ChatResponse response, String... keywords) {
        assertResponseNotEmpty(response);
        String content = response.getContent().toLowerCase();
        
        for (String keyword : keywords) {
            Assert.assertTrue(
                content.contains(keyword.toLowerCase()),
                String.format("Response missing keyword: '%s'", keyword)
            );
        }
    }
    
    public static void assertDoesNotContain(ChatResponse response, String text) {
        assertResponseNotEmpty(response);
        Assert.assertFalse(
            response.getContent().toLowerCase().contains(text.toLowerCase()),
            String.format("Response should not contain: '%s'", text)
        );
    }
    
    // PATTERN VALIDATIONS
    
    public static void assertMatchesPattern(ChatResponse response, String regexPattern) {
        assertResponseNotEmpty(response);
        Pattern pattern = Pattern.compile(regexPattern, Pattern.CASE_INSENSITIVE);
        Assert.assertTrue(
            pattern.matcher(response.getContent()).find(),
            String.format("Response does not match pattern: %s", regexPattern)
        );
    }
    
    // FORMAT VALIDATIONS
    
    public static void assertIsJson(ChatResponse response) {
        assertResponseNotEmpty(response);
        String content = response.getContent().trim();
        Assert.assertTrue(
            content.startsWith("{") && content.endsWith("}") ||
            content.startsWith("[") && content.endsWith("]"),
            "Response is not valid JSON format"
        );
    }
    
    public static void assertContainsList(ChatResponse response) {
        assertResponseNotEmpty(response);
        String content = response.getContent();
        Assert.assertTrue(
            content.contains("1.") || content.contains("1)") || 
            content.contains("•") || content.contains("-"),
            "Response does not contain a list"
        );
    }
    
    // QUALITY VALIDATIONS
    
    public static void assertNoRepeatedSentences(ChatResponse response) {
        assertResponseNotEmpty(response);
        String content = response.getContent();
        String[] sentences = content.split("[.!?]");
        
        for (int i = 0; i < sentences.length - 1; i++) {
            for (int j = i + 1; j < sentences.length; j++) {
                Assert.assertFalse(
                    sentences[i].trim().equals(sentences[j].trim()),
                    "Response contains repeated sentences"
                );
            }
        }
    }
    
    public static void assertSentenceCount(ChatResponse response, int minCount) {
        assertResponseNotEmpty(response);
        String[] sentences = response.getContent().split("[.!?]");
        int count = (int) Arrays.stream(sentences)
            .filter(s -> s.trim().length() > 0)
            .count();
        
        Assert.assertTrue(
            count >= minCount,
            String.format("Expected at least %d sentences, got %d", minCount, count)
        );
    }
    
    // PERFORMANCE VALIDATIONS
    
    public static void assertResponseTime(ChatResponse response, long maxSeconds) {
        Assert.assertNotNull(response.getTotalDuration(), "Duration not available");
        long actualSeconds = response.getTotalDurationInSeconds();
        Assert.assertTrue(
            actualSeconds <= maxSeconds,
            String.format("Response time %ds exceeded limit %ds", 
                         actualSeconds, maxSeconds)
        );
    }
    
    public static void assertTokensPerSecond(ChatResponse response, double minTPS) {
        double actualTPS = response.getTokensPerSecond();
        Assert.assertTrue(
            actualTPS >= minTPS,
            String.format("Tokens per second %.2f below threshold %.2f", 
                         actualTPS, minTPS)
        );
    }
    
    // METADATA VALIDATIONS
    
    public static void assertModel(ChatResponse response, String expectedModel) {
        Assert.assertEquals(
            response.getModel(),
            expectedModel,
            "Model mismatch"
        );
    }
    
    public static void assertTokenCount(ChatResponse response, int minTokens) {
        Assert.assertNotNull(response.getEvalCount(), "Token count not available");
        Assert.assertTrue(
            response.getEvalCount() >= minTokens,
            String.format("Generated tokens %d below minimum %d", 
                         response.getEvalCount(), minTokens)
        );
    }
    
    // LANGUAGE VALIDATIONS
    
    public static void assertLanguage(ChatResponse response, String language) {
        assertResponseNotEmpty(response);
        String content = response.getContent();
        
        switch (language.toLowerCase()) {
            case "english":
                Assert.assertTrue(
                    content.matches(".*[a-zA-Z].*"),
                    "Response does not appear to be in English"
                );
                break;
            case "code":
                Assert.assertTrue(
                    content.contains("{") || content.contains("(") || 
                    content.contains("def") || content.contains("function"),
                    "Response does not appear to be code"
                );
                break;
            default:
                throw new IllegalArgumentException("Language validation not implemented for: " + language);
        }
    }
    
    // SENTIMENT VALIDATIONS (Simple)
    
    public static void assertPositiveTone(ChatResponse response) {
        assertResponseNotEmpty(response);
        String content = response.getContent().toLowerCase();
        List<String> positiveWords = Arrays.asList(
            "good", "great", "excellent", "wonderful", "amazing", 
            "positive", "success", "happy", "best"
        );
        
        boolean hasPositiveWord = positiveWords.stream()
            .anyMatch(content::contains);
        
        Assert.assertTrue(
            hasPositiveWord,
            "Response does not have positive tone"
        );
    }
}
