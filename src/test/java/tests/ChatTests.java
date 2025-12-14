package tests;

import config.OllamaConfig;
import io.restassured.response.Response;
import models.ChatRequest;
import models.ChatResponse;
import org.testng.Assert;
import org.testng.annotations.*;
import services.OllamaService;
import utils.AIResponseValidator;

import java.util.Arrays;
import java.util.List;

public class ChatTests {
    
    private OllamaService ollamaService;
    private String testModel;
    
    @BeforeClass
    public void setup() {
        ollamaService = new OllamaService();
        testModel = OllamaConfig.getInstance().getDefaultModel();
        
        // Verify model is available
        Assert.assertTrue(
            ollamaService.isModelAvailable(testModel),
            "Test model not available: " + testModel
        );
    }
    
    // ==================== BASIC CHAT TESTS ====================
    
    @Test(priority = 1, description = "Test simple chat completion")
    public void testSimpleChat() {
        // Arrange
        ChatRequest request = new ChatRequest.Builder()
            .model(testModel)
            .addUserMessage("Say hello in one sentence")
            .build();
        
        // Act
        ChatResponse response = ollamaService.chatTyped(request);
        
        // Assert
        AIResponseValidator.assertResponseComplete(response);
        AIResponseValidator.assertMinLength(response, 5);
        AIResponseValidator.assertModel(response, testModel);
    }
    
    @Test(priority = 2, description = "Test question answering")
    public void testQuestionAnswering() {
        // Arrange
        ChatRequest request = new ChatRequest.Builder()
            .model(testModel)
            .addUserMessage("What is the capital of France?")
            .build();
        
        // Act
        ChatResponse response = ollamaService.chatTyped(request);
        
        // Assert
        AIResponseValidator.assertResponseComplete(response);
        AIResponseValidator.assertContainsKeyword(response, "Paris");
    }
    
    @Test(priority = 3, description = "Test response completeness")
    public void testResponseCompleteness() {
        // Arrange
        ChatRequest request = new ChatRequest.Builder()
            .model(testModel)
            .addUserMessage("Explain photosynthesis in 3 sentences")
            .build();
        
        // Act
        ChatResponse response = ollamaService.chatTyped(request);
        
        // Assert
        AIResponseValidator.assertResponseComplete(response);
        AIResponseValidator.assertSentenceCount(response, 3);
        AIResponseValidator.assertContainsAnyKeyword(response, 
            "plant", "light", "oxygen", "carbon dioxide");
    }
    
    // ==================== MULTI-TURN CONVERSATION ====================
    
    @Test(priority = 4, description = "Test multi-turn conversation with context")
    public void testMultiTurnConversation() {
        // Turn 1: Introduce a topic
        ChatRequest request1 = new ChatRequest.Builder()
            .model(testModel)
            .addUserMessage("My name is John and I love pizza")
            .build();
        
        ChatResponse response1 = ollamaService.chatTyped(request1);
        AIResponseValidator.assertResponseComplete(response1);
        
        // Turn 2: Ask about previous context
        ChatRequest request2 = new ChatRequest.Builder()
            .model(testModel)
            .addUserMessage("My name is John and I love pizza")
            .addAssistantMessage(response1.getContent())
            .addUserMessage("What is my name?")
            .build();
        
        ChatResponse response2 = ollamaService.chatTyped(request2);
        
        // Assert context retention
        AIResponseValidator.assertResponseComplete(response2);
        AIResponseValidator.assertContainsKeyword(response2, "John");
    }
    
    @Test(priority = 5, description = "Test conversation memory over multiple turns")
    public void testConversationMemory() {
        List<String> history = Arrays.asList(
            "I have a red car",
            "That's nice! Red is a vibrant color for a car.",
            "I drive it to work every day"
        );
        
        ChatResponse response = ollamaService.continueConversation(
            history, 
            "What color is my car?"
        );
        
        AIResponseValidator.assertResponseComplete(response);
        AIResponseValidator.assertContainsKeyword(response, "red");
    }
    
    // ==================== PARAMETER TESTING ====================
    
    @Test(priority = 6, description = "Test with different temperature settings")
    public void testTemperatureVariation() {
        String prompt = "Write a creative sentence about the moon";
        
        // Low temperature (deterministic)
        ChatRequest lowTemp = new ChatRequest.Builder()
            .model(testModel)
            .addUserMessage(prompt)
            .temperature(0.1)
            .build();
        
        ChatResponse response1 = ollamaService.chatTyped(lowTemp);
        
        // High temperature (creative)
        ChatRequest highTemp = new ChatRequest.Builder()
            .model(testModel)
            .addUserMessage(prompt)
            .temperature(1.5)
            .build();
        
        ChatResponse response2 = ollamaService.chatTyped(highTemp);
        
        // Assert both completed
        AIResponseValidator.assertResponseComplete(response1);
        AIResponseValidator.assertResponseComplete(response2);
        
        // Responses should be different due to temperature
        Assert.assertNotEquals(
            response1.getContent(), 
            response2.getContent(),
            "Responses with different temperatures should vary"
        );
    }
    
    @Test(priority = 7, description = "Test max token limit")
    public void testMaxTokens() {
        ChatRequest request = new ChatRequest.Builder()
            .model(testModel)
            .addUserMessage("Write a long story")
            .maxTokens(50)  // Limit tokens
            .build();
        
        ChatResponse response = ollamaService.chatTyped(request);
        
        AIResponseValidator.assertResponseComplete(response);
        
        // Verify token limit was respected
        Assert.assertTrue(
            response.getEvalCount() <= 60, // Some tolerance
            "Token count exceeded limit"
        );
    }
    
    // ==================== SYSTEM PROMPT TESTING ====================
    
    @Test(priority = 8, description = "Test system prompt instruction following")
    public void testSystemPrompt() {
        ChatRequest request = new ChatRequest.Builder()
            .model(testModel)
            .addSystemMessage("You are a pirate. Always talk like a pirate.")
            .addUserMessage("Tell me about the weather")
            .build();
        
        ChatResponse response = ollamaService.chatTyped(request);
        
        AIResponseValidator.assertResponseComplete(response);
        AIResponseValidator.assertContainsAnyKeyword(response, 
            "arr", "matey", "ship", "sea");
    }
    
    // ==================== CONTENT VALIDATION ====================
    
    @Test(priority = 9, description = "Test code generation")
    public void testCodeGeneration() {
        ChatRequest request = new ChatRequest.Builder()
            .model(testModel)
            .addUserMessage("Write a Python function to add two numbers")
            .build();
        
        ChatResponse response = ollamaService.chatTyped(request);
        
        AIResponseValidator.assertResponseComplete(response);
        AIResponseValidator.assertContainsAllKeywords(response, "def", "return");
        AIResponseValidator.assertLanguage(response, "code");
    }
    
    @Test(priority = 10, description = "Test list generation")
    public void testListGeneration() {
        ChatRequest request = new ChatRequest.Builder()
            .model(testModel)
            .addUserMessage("Give me 5 programming languages")
            .build();
        
        ChatResponse response = ollamaService.chatTyped(request);
        
        AIResponseValidator.assertResponseComplete(response);
        AIResponseValidator.assertContainsList(response);
    }
    
    // ==================== PERFORMANCE TESTS ====================
    
    @Test(priority = 11, description = "Test response time for short prompt")
    public void testResponseTimeShortPrompt() {
        ChatRequest request = new ChatRequest.Builder()
            .model(testModel)
            .addUserMessage("Hi")
            .build();
        
        ChatResponse response = ollamaService.chatTyped(request);
        
        AIResponseValidator.assertResponseComplete(response);
        AIResponseValidator.assertResponseTime(response, 10); // 10 seconds max
        
        System.out.println("Response time: " + response.getTotalDurationInSeconds() + "s");
        System.out.println("Tokens/sec: " + response.getTokensPerSecond());
    }
    
    @Test(priority = 12, description = "Test performance metrics availability")
    public void testPerformanceMetrics() {
        ChatRequest request = new ChatRequest.Builder()
            .model(testModel)
            .addUserMessage("Explain AI in one sentence")
            .build();
        
        ChatResponse response = ollamaService.chatTyped(request);
        
        // Verify all performance metrics are present
        Assert.assertNotNull(response.getTotalDuration(), "Total duration missing");
        Assert.assertNotNull(response.getPromptEvalCount(), "Prompt eval count missing");
        Assert.assertNotNull(response.getEvalCount(), "Eval count missing");
        Assert.assertNotNull(response.getEvalDuration(), "Eval duration missing");
        
        // Log metrics
        System.out.println("=== Performance Metrics ===");
        System.out.println("Total Duration: " + response.getTotalDurationInSeconds() + "s");
        System.out.println("Prompt Tokens: " + response.getPromptEvalCount());
        System.out.println("Generated Tokens: " + response.getEvalCount());
        System.out.println("Tokens/Second: " + String.format("%.2f", response.getTokensPerSecond()));
    }
    
    // ==================== ERROR HANDLING ====================
    
    @Test(priority = 13, description = "Test with invalid model name")
    public void testInvalidModel() {
        ChatRequest request = new ChatRequest.Builder()
            .model("invalid-model-name")
            .addUserMessage("Hello")
            .build();
        
        Response response = ollamaService.chat(request);
        
        // Should return error status
        Assert.assertEquals(response.statusCode(), 404, "Should return 404 for invalid model");
    }
    
    @Test(priority = 14, description = "Test with empty message")
    public void testEmptyMessage() {
        ChatRequest request = new ChatRequest.Builder()
            .model(testModel)
            .addUserMessage("")
            .build();
        
        ChatResponse response = ollamaService.chatTyped(request);
        
        // Model should still respond (likely asking for clarification)
        AIResponseValidator.assertResponseComplete(response);
    }
    
    // ==================== EDGE CASES ====================
    
    @Test(priority = 15, description = "Test with very long input")
    public void testLongInput() {
        StringBuilder longPrompt = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            longPrompt.append("This is sentence number ").append(i).append(". ");
        }
        longPrompt.append("Summarize the above in one sentence.");
        
        ChatRequest request = new ChatRequest.Builder()
            .model(testModel)
            .addUserMessage(longPrompt.toString())
            .build();
        
        ChatResponse response = ollamaService.chatTyped(request);
        
        AIResponseValidator.assertResponseComplete(response);
        Assert.assertTrue(
            response.getPromptEvalCount() > 100,
            "Should process large prompt"
        );
    }
    
    @Test(priority = 16, description = "Test with special characters")
    public void testSpecialCharacters() {
        ChatRequest request = new ChatRequest.Builder()
            .model(testModel)
            .addUserMessage("What does this mean: @#$%^&*()? Explain simply.")
            .build();
        
        ChatResponse response = ollamaService.chatTyped(request);
        
        AIResponseValidator.assertResponseComplete(response);
        AIResponseValidator.assertMinLength(response, 20);
    }
}
