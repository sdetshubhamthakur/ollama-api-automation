package tests;

import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.*;
import services.OllamaService;

import java.util.List;

public class ModelManagementTests {
    
    private OllamaService ollamaService;
    
    @BeforeClass
    public void setup() {
        ollamaService = new OllamaService();
    }
    
    // ==================== MODEL LISTING TESTS ====================
    
    @Test(priority = 1, description = "Test list all models")
    public void testListModels() {
        Response response = ollamaService.listModels();
        
        // Validate response
        Assert.assertEquals(response.statusCode(), 200, "Should return 200 OK");
        Assert.assertTrue(response.time() < 1000, "Should respond quickly");
        
        // Validate structure
        response.then()
            .body("models", org.hamcrest.Matchers.notNullValue())
            .body("models.size()", org.hamcrest.Matchers.greaterThan(0));
        
        // Extract model names
        List<String> modelNames = response.jsonPath().getList("models.name");
        System.out.println("Installed models: " + modelNames);
        
        Assert.assertFalse(modelNames.isEmpty(), "Should have at least one model");
    }
    
    @Test(priority = 2, description = "Verify specific model exists")
    public void testModelExists() {
        String expectedModel = "llama3.2:3b";
        
        boolean exists = ollamaService.isModelAvailable(expectedModel);
        
        Assert.assertTrue(exists, 
            "Model should be available: " + expectedModel);
    }
    
    @Test(priority = 3, description = "Test model count")
    public void testModelCount() {
        int count = ollamaService.getInstalledModelCount();
        
        System.out.println("Total installed models: " + count);
        Assert.assertTrue(count > 0, "Should have at least one model installed");
    }
    
    // ==================== MODEL DETAILS TESTS ====================
    
    @Test(priority = 4, description = "Test show model details")
    public void testShowModelDetails() {
        String modelName = "llama3.2:3b";
        
        Response response = ollamaService.showModel(modelName);
        
        Assert.assertEquals(response.statusCode(), 200, "Should return model details");
        
        // Validate response structure
        response.then()
            .body("modelfile", org.hamcrest.Matchers.notNullValue())
            .body("parameters", org.hamcrest.Matchers.notNullValue())
            .body("template", org.hamcrest.Matchers.notNullValue());
        
        System.out.println("=== Model Details ===");
        System.out.println("Parameters: " + response.jsonPath().getString("parameters"));
    }
    
    @Test(priority = 5, description = "Test show model with invalid name")
    public void testShowInvalidModel() {
        Response response = ollamaService.showModel("nonexistent-model");
        
        // Should return error
        Assert.assertEquals(response.statusCode(), 404, 
            "Should return 404 for nonexistent model");
    }
    
    // ==================== MODEL METADATA VALIDATION ====================
    
    @Test(priority = 6, description = "Validate model metadata structure")
    public void testModelMetadata() {
        Response response = ollamaService.listModels();
        
        // Get first model
        String firstName = response.jsonPath().getString("models[0].name");
        String firstDigest = response.jsonPath().getString("models[0].digest");
        Long firstSize = response.jsonPath().getLong("models[0].size");
        
        Assert.assertNotNull(firstName, "Model name should not be null");
        Assert.assertNotNull(firstDigest, "Model digest should not be null");
        Assert.assertNotNull(firstSize, "Model size should not be null");
        Assert.assertTrue(firstSize > 0, "Model size should be positive");
        
        System.out.println("=== Model Metadata ===");
        System.out.println("Name: " + firstName);
        System.out.println("Digest: " + firstDigest);
        System.out.println("Size: " + formatBytes(firstSize));
    }
    
    @Test(priority = 7, description = "Validate all models have required fields")
    public void testAllModelsHaveRequiredFields() {
        Response response = ollamaService.listModels();
        List<String> modelNames = response.jsonPath().getList("models.name");
        
        for (int i = 0; i < modelNames.size(); i++) {
            String name = response.jsonPath().getString("models[" + i + "].name");
            String digest = response.jsonPath().getString("models[" + i + "].digest");
            Long size = response.jsonPath().getLong("models[" + i + "].size");
            
            Assert.assertNotNull(name, "Model name missing at index " + i);
            Assert.assertNotNull(digest, "Model digest missing at index " + i);
            Assert.assertNotNull(size, "Model size missing at index " + i);
        }
        
        System.out.println("All " + modelNames.size() + " models have required fields");
    }
    
    // ==================== MODEL COMPARISON TESTS ====================
    
    @Test(priority = 8, description = "Compare response quality across models", 
          enabled = false) // Enable if you have multiple models
    public void testCompareModels() {
        String[] models = {"llama3.2:1b", "llama3.2:3b"};
        String testPrompt = "What is artificial intelligence? Answer in one sentence.";
        
        for (String model : models) {
            if (ollamaService.isModelAvailable(model)) {
                models.ChatRequest request = new models.ChatRequest.Builder()
                    .model(model)
                    .addUserMessage(testPrompt)
                    .build();
                
                models.ChatResponse response = ollamaService.chatTyped(request);
                
                System.out.println("=== " + model + " ===");
                System.out.println("Response: " + response.getContent());
                System.out.println("Time: " + response.getTotalDurationInSeconds() + "s");
                System.out.println("Tokens: " + response.getEvalCount());
                System.out.println();
            }
        }
    }
    
    // ==================== HELPER METHODS ====================
    
    private String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp-1) + "";
        return String.format("%.2f %sB", bytes / Math.pow(1024, exp), pre);
    }
}
