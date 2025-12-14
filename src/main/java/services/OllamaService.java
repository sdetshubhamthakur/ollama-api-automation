package models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatResponse {
    
    @JsonProperty("model")
    private String model;
    
    @JsonProperty("created_at")
    private String createdAt;
    
    @JsonProperty("message")
    private ResponseMessage message;
    
    @JsonProperty("done")
    private Boolean done;
    
    @JsonProperty("total_duration")
    private Long totalDuration;
    
    @JsonProperty("load_duration")
    private Long loadDuration;
    
    @JsonProperty("prompt_eval_count")
    private Integer promptEvalCount;
    
    @JsonProperty("prompt_eval_duration")
    private Long promptEvalDuration;
    
    @JsonProperty("eval_count")
    private Integer evalCount;
    
    @JsonProperty("eval_duration")
    private Long evalDuration;
    
    // Getters
    public String getModel() { return model; }
    public String getCreatedAt() { return createdAt; }
    public ResponseMessage getMessage() { return message; }
    public Boolean getDone() { return done; }
    public Long getTotalDuration() { return totalDuration; }
    public Long getLoadDuration() { return loadDuration; }
    public Integer getPromptEvalCount() { return promptEvalCount; }
    public Long getPromptEvalDuration() { return promptEvalDuration; }
    public Integer getEvalCount() { return evalCount; }
    public Long getEvalDuration() { return evalDuration; }
    
    // Utility methods
    public String getContent() {
        return message != null ? message.getContent() : null;
    }
    
    public long getTotalDurationInSeconds() {
        return totalDuration != null ? totalDuration / 1_000_000_000 : 0;
    }
    
    public double getTokensPerSecond() {
        if (evalCount != null && evalDuration != null && evalDuration > 0) {
            return (evalCount.doubleValue() * 1_000_000_000) / evalDuration;
        }
        return 0;
    }
    
    // Setters
    public void setModel(String model) { this.model = model; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public void setMessage(ResponseMessage message) { this.message = message; }
    public void setDone(Boolean done) { this.done = done; }
    public void setTotalDuration(Long totalDuration) { this.totalDuration = totalDuration; }
    public void setLoadDuration(Long loadDuration) { this.loadDuration = loadDuration; }
    public void setPromptEvalCount(Integer promptEvalCount) { this.promptEvalCount = promptEvalCount; }
    public void setPromptEvalDuration(Long promptEvalDuration) { this.promptEvalDuration = promptEvalDuration; }
    public void setEvalCount(Integer evalCount) { this.evalCount = evalCount; }
    public void setEvalDuration(Long evalDuration) { this.evalDuration = evalDuration; }
}

@JsonIgnoreProperties(ignoreUnknown = true)
class ResponseMessage {
    @JsonProperty("role")
    private String role;
    
    @JsonProperty("content")
    private String content;
    
    // Getters and Setters
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
