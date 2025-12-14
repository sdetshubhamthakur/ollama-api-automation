package models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatRequest {
    
    @JsonProperty("model")
    private String model;
    
    @JsonProperty("messages")
    private List<Message> messages;
    
    @JsonProperty("stream")
    private Boolean stream;
    
    @JsonProperty("options")
    private Options options;
    
    private ChatRequest(Builder builder) {
        this.model = builder.model;
        this.messages = builder.messages;
        this.stream = builder.stream;
        this.options = builder.options;
    }
    
    public static class Builder {
        private String model;
        private List<Message> messages;
        private Boolean stream;
        private Options options;
        
        public Builder() {
            this.messages = new ArrayList<>();
            this.stream = false; // Default to non-streaming
        }
        
        public Builder model(String model) {
            this.model = model;
            return this;
        }
        
        public Builder addMessage(String role, String content) {
            this.messages.add(new Message(role, content));
            return this;
        }
        
        public Builder addUserMessage(String content) {
            return addMessage("user", content);
        }
        
        public Builder addAssistantMessage(String content) {
            return addMessage("assistant", content);
        }
        
        public Builder addSystemMessage(String content) {
            return addMessage("system", content);
        }
        
        public Builder messages(List<Message> messages) {
            this.messages = messages;
            return this;
        }
        
        public Builder stream(Boolean stream) {
            this.stream = stream;
            return this;
        }
        
        public Builder temperature(Double temperature) {
            if (this.options == null) {
                this.options = new Options();
            }
            this.options.setTemperature(temperature);
            return this;
        }
        
        public Builder maxTokens(Integer maxTokens) {
            if (this.options == null) {
                this.options = new Options();
            }
            this.options.setNumPredict(maxTokens);
            return this;
        }
        
        public Builder options(Options options) {
            this.options = options;
            return this;
        }
        
        public ChatRequest build() {
            if (model == null || model.isEmpty()) {
                throw new IllegalStateException("Model is required");
            }
            if (messages == null || messages.isEmpty()) {
                throw new IllegalStateException("At least one message is required");
            }
            return new ChatRequest(this);
        }
    }
    
    // Getters
    public String getModel() { return model; }
    public List<Message> getMessages() { return messages; }
    public Boolean getStream() { return stream; }
    public Options getOptions() { return options; }
    
    // Options class for model parameters
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Options {
        @JsonProperty("temperature")
        private Double temperature;
        
        @JsonProperty("num_predict")
        private Integer numPredict;
        
        @JsonProperty("top_p")
        private Double topP;
        
        @JsonProperty("top_k")
        private Integer topK;
        
        // Getters and Setters
        public Double getTemperature() { return temperature; }
        public void setTemperature(Double temperature) { this.temperature = temperature; }
        
        public Integer getNumPredict() { return numPredict; }
        public void setNumPredict(Integer numPredict) { this.numPredict = numPredict; }
        
        public Double getTopP() { return topP; }
        public void setTopP(Double topP) { this.topP = topP; }
        
        public Integer getTopK() { return topK; }
        public void setTopK(Integer topK) { this.topK = topK; }
    }
}

// Message class
class Message {
    @JsonProperty("role")
    private String role;
    
    @JsonProperty("content")
    private String content;
    
    public Message() {}
    
    public Message(String role, String content) {
        this.role = role;
        this.content = content;
    }
    
    // Getters and Setters
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
