# Ollama API Test Automation Framework

> **Enterprise-grade API test automation framework for testing LLM/AI chatbot applications using RestAssured, TestNG, and Ollama**

[![Java](https://img.shields.io/badge/Java-11+-orange.svg)](https://www.oracle.com/java/)
[![RestAssured](https://img.shields.io/badge/RestAssured-5.3.2-green.svg)](https://rest-assured.io/)
[![TestNG](https://img.shields.io/badge/TestNG-7.8.0-red.svg)](https://testng.org/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

## 📋 Table of Contents
- [Overview](#overview)
- [Key Features](#key-features)
- [Architecture](#architecture)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Quick Start](#quick-start)
- [Project Structure](#project-structure)
- [Running Tests](#running-tests)
- [Test Scenarios](#test-scenarios)
- [Configuration](#configuration)
- [Reporting](#reporting)
- [Best Practices](#best-practices)
- [Contributing](#contributing)

---

## 🎯 Overview

This framework provides a comprehensive solution for testing AI chatbot APIs with a focus on **non-deterministic response validation**, **conversation context management**, and **performance benchmarking**. Built specifically to address the unique challenges of testing LLM applications where traditional assertion methods fall short.

### Why This Framework?

Testing AI chatbots differs fundamentally from traditional API testing:
- **Non-deterministic outputs**: Same input → Different outputs
- **Context dependency**: Multi-turn conversations require state management
- **Quality over exactness**: Validate response quality, not exact matches
- **Performance metrics**: Tokens/sec, context window usage, generation speed

This framework addresses these challenges with custom validators, intelligent assertions, and comprehensive test coverage.

---

## ✨ Key Features

### 🔧 Core Capabilities
- ✅ **Custom AI Response Validators** - Keyword matching, length validation, pattern recognition
- ✅ **Multi-turn Conversation Testing** - Context retention and session isolation
- ✅ **Performance Benchmarking** - Tokens/sec, response time, load testing
- ✅ **Concurrent User Simulation** - Thread-safe parallel execution
- ✅ **POJO-based Response Mapping** - Type-safe, maintainable code
- ✅ **Builder Pattern Implementation** - Clean, readable request construction
- ✅ **Flexible Configuration** - Environment-based property management

### 🎨 Advanced Features
- 📊 **Performance Metrics Tracking** - Token usage, generation speed, latency
- 🔄 **Streaming Response Support** - SSE (Server-Sent Events) handling ready
- 🌐 **Multi-model Testing** - Compare different LLM models
- 🛡️ **Error Handling & Recovery** - Retry mechanisms, graceful degradation
- 📈 **Comprehensive Reporting** - TestNG reports, Extent Reports integration
- 🔍 **Quality Assertions** - Sentiment, relevance, factual accuracy validation

---

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────┐
│                    Test Layer (TestNG)                   │
│  • ChatTests.java                                       │
│  • AdvancedTests.java                                   │
│  • ModelManagementTests.java                            │
└─────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────┐
│              Service Layer (Business Logic)              │
│  • OllamaService.java - API interactions                │
│  • ModelService.java - Model management                 │
└─────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────┐
│           API Client Layer (RestAssured)                 │
│  • SpecBuilder.java - Request/Response specs            │
│  • Custom Filters & Logging                             │
└─────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────┐
│        Models Layer (POJOs & DTOs)                       │
│  • ChatRequest.java - Request builder                   │
│  • ChatResponse.java - Response mapper                  │
└─────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────┐
│           Utilities & Configuration                      │
│  • AIResponseValidator.java - Custom validators         │
│  • OllamaConfig.java - Configuration management         │
└─────────────────────────────────────────────────────────┘
```

### Design Patterns Used
- **Builder Pattern** - Request construction
- **Service Layer Pattern** - Business logic separation
- **Singleton Pattern** - Configuration management
- **Factory Pattern** - Test data generation
- **Strategy Pattern** - Validation strategies

---

## 📦 Prerequisites

### Required
- **Java JDK** 11 or higher
- **Maven** 3.6+
- **Ollama** installed and running locally
- **Git** for version control

### Recommended
- **IntelliJ IDEA** or **Eclipse** IDE
- **Postman** for API exploration
- **At least one Ollama model** downloaded

---

## 🚀 Installation

### 1. Install Ollama
```bash
# macOS/Linux
curl https://ollama.ai/install.sh | sh

# Windows
# Download from: https://ollama.ai/download
```

### 2. Download LLM Models
```bash
# Recommended for testing (fast and good quality)
ollama pull llama3.2:3b

# Alternative options
ollama pull llama3.2:1b    # Smaller, faster
ollama pull mistral:7b     # Different model for comparison
ollama pull phi3:mini      # Compact model
```

### 3. Start Ollama Server
```bash
ollama serve
# Server runs on http://localhost:11434
```

### 4. Clone & Setup Framework
```bash
# Clone repository
git clone https://github.com/yourusername/ollama-api-automation.git
cd ollama-api-automation

# Install dependencies
mvn clean install

# Verify setup
mvn test -Dtest=ModelManagementTests#testListModels
```

---

## ⚡ Quick Start

### Run Your First Test
```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=ChatTests

# Run specific test method
mvn test -Dtest=ChatTests#testSimpleChat

# Run with custom model
mvn test -DdefaultModel=mistral:7b
```

### Basic Test Example
```java
@Test
public void testSimpleChat() {
    // Arrange
    ChatRequest request = new ChatRequest.Builder()
        .model("llama3.2:3b")
        .addUserMessage("What is AI?")
        .build();
    
    // Act
    ChatResponse response = ollamaService.chatTyped(request);
    
    // Assert
    AIResponseValidator.assertResponseComplete(response);
    AIResponseValidator.assertContainsKeyword(response, "artificial");
    AIResponseValidator.assertMinLength(response, 50);
}
```

---

## 📁 Project Structure

```
ollama-api-automation/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── config/
│   │   │   │   └── OllamaConfig.java          # Configuration management
│   │   │   ├── models/
│   │   │   │   ├── request/
│   │   │   │   │   ├── ChatRequest.java       # Request builder
│   │   │   │   │   └── Message.java           # Message POJO
│   │   │   │   └── response/
│   │   │   │       └── ChatResponse.java      # Response mapper
│   │   │   ├── services/
│   │   │   │   └── OllamaService.java         # API service layer
│   │   │   └── utils/
│   │   │       ├── SpecBuilder.java           # RestAssured specs
│   │   │       └── AIResponseValidator.java   # Custom validators
│   │   └── resources/
│   │       ├── config.properties              # Environment config
│   │       └── testdata/
│   │           └── prompts.json               # Test data
│   └── test/
│       └── java/
│           └── tests/
│               ├── ChatTests.java              # Basic chat tests
│               ├── AdvancedTests.java          # Advanced scenarios
│               └── ModelManagementTests.java   # Model operations
├── pom.xml                                     # Maven dependencies
├── testng.xml                                  # TestNG configuration
└── README.md                                   # This file
```

---

## 🧪 Running Tests

### Test Suites

#### 1. Smoke Tests (Fast - 2 minutes)
```bash
mvn test -Dgroups=smoke
```

#### 2. Regression Suite (Complete - 10 minutes)
```bash
mvn test -DsuiteXmlFile=testng.xml
```

#### 3. Performance Tests
```bash
mvn test -Dtest=AdvancedTests#testConcurrentRequests,testLoadSequential
```

#### 4. Specific Scenarios
```bash
# Multi-turn conversations
mvn test -Dtest=ChatTests#testMultiTurnConversation

# Context retention
mvn test -Dtest=ChatTests#testConversationMemory

# Model comparison
mvn test -Dtest=ModelManagementTests
```

### Parallel Execution
```xml
<!-- testng.xml -->
<suite name="Parallel Suite" parallel="tests" thread-count="3">
    <!-- Tests run in parallel -->
</suite>
```

---

## 🎯 Test Scenarios

### ✅ Basic Functionality (16 tests)
- Simple chat completion
- Question answering
- Response completeness
- Multi-turn conversations
- Context retention
- Parameter variation (temperature, max_tokens)
- System prompt instructions
- Code generation
- List generation

### ⚡ Performance Testing (5 tests)
- Response time validation
- Concurrent user simulation
- Load testing (sequential)
- Session isolation
- Tokens per second benchmarking

### 🛡️ Error Handling (4 tests)
- Invalid model handling
- Empty message handling
- Malformed request recovery
- Rate limit testing

### 🧪 Advanced Scenarios (7 tests)
- Context window limits
- Response consistency
- Factual accuracy
- Prompt injection resistance
- Multi-language support
- Edge cases (emojis, special chars)

### 📊 Quality Validation (5 tests)
- Response relevance
- Sentiment analysis
- No repeated sentences
- Appropriate response length
- Language detection

**Total: 37+ automated test scenarios**

---

## ⚙️ Configuration

### config.properties
```properties
# Ollama Configuration
base.uri=http://localhost:11434
default.model=llama3.2:3b
timeout=30000

# Performance Thresholds
max.response.time=10000
min.tokens.per.second=10

# Test Configuration
retry.count=3
parallel.threads=5
```

### Environment Variables
```bash
# Override configuration
export OLLAMA_BASE_URI=http://custom-host:11434
export DEFAULT_MODEL=mistral:7b

# Run with custom config
mvn test -DbaseUri=$OLLAMA_BASE_URI -DdefaultModel=$DEFAULT_MODEL
```

---

## 📊 Reporting

### TestNG Reports
```bash
# Default location
target/surefire-reports/index.html
```

### Extent Reports (Optional)
```java
// Add listener in testng.xml
<listeners>
    <listener class-name="utils.ExtentReportListener"/>
</listeners>
```

### Console Output
```
=== Performance Metrics ===
Total Duration: 2.5s
Prompt Tokens: 15
Generated Tokens: 87
Tokens/Second: 34.80

Test: testSimpleChat - PASSED ✓
Test: testMultiTurnConversation - PASSED ✓
Test: testConcurrentRequests - PASSED ✓
```

---

## 💡 Best Practices

### 1. Validation Strategy
```java
// ❌ Don't do this (too brittle)
Assert.assertEquals(response.getContent(), "Paris is the capital of France.");

// ✅ Do this (flexible, robust)
AIResponseValidator.assertContainsKeyword(response, "Paris");
AIResponseValidator.assertMinLength(response, 10);
AIResponseValidator.assertResponseComplete(response);
```

### 2. Test Independence
```java
// Each test should be self-contained
@Test
public void testExample() {
    // Arrange - Create fresh data
    // Act - Perform action
    // Assert - Validate results
    // Cleanup - If needed
}
```

### 3. Performance Testing
```java
// Always track metrics
System.out.println("Response time: " + response.getTotalDurationInSeconds());
System.out.println("Tokens/sec: " + response.getTokensPerSecond());
```

### 4. Error Handling
```java
// Use try-catch for flaky scenarios
try {
    response = ollamaService.chat(request);
} catch (Exception e) {
    // Log and retry
}
```

---

## 🔍 Troubleshooting

### Common Issues

#### 1. Connection Refused
```bash
# Check if Ollama is running
curl http://localhost:11434/api/tags

# Start Ollama
ollama serve
```

#### 2. Model Not Found
```bash
# List installed models
ollama list

# Pull required model
ollama pull llama3.2:3b
```

#### 3. Timeout Errors
```java
// Increase timeout in config.properties
timeout=60000  # 60 seconds
```

#### 4. OOM Errors
```bash
# Reduce concurrent threads
mvn test -Dparallel.threads=2
```

---

## 📈 Future Enhancements

- [ ] Database integration for test result storage
- [ ] Streaming response validation (SSE)
- [ ] CI/CD pipeline integration (Jenkins/GitHub Actions)
- [ ] Docker containerization
- [ ] API response caching
- [ ] Custom Allure reporting
- [ ] BDD support with Cucumber
- [ ] GraphQL API testing

---

## 🤝 Contributing

Contributions welcome! Please:
1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

---

## 📝 License

This project is licensed under the MIT License - see [LICENSE](LICENSE) file for details.

---

## 👤 Author

**Shubham Thakur**
- LinkedIn: [Shubham Thakur](https://www.linkedin.com/in/shubhamthakur01/)
- GitHub: [@sdetshubhamthakur](https://github.com/sdetshubhamthakur)

---

## 🙏 Acknowledgments

- [Ollama](https://ollama.ai/) - Local LLM runtime
- [RestAssured](https://rest-assured.io/) - API testing framework
- [TestNG](https://testng.org/) - Testing framework
- [DeepEval](https://deepeval.com/) - LLM evaluation inspiration

---

## 📚 Additional Resources

- [Ollama API Documentation](https://github.com/ollama/ollama/blob/main/docs/api.md)
- [RestAssured Documentation](https://github.com/rest-assured/rest-assured/wiki)

---

## 🎯 Interview Preparation

This framework demonstrates:
- ✅ Enterprise-grade test automation design
- ✅ Understanding of AI/LLM testing challenges
- ✅ SOLID principles and design patterns
- ✅ Performance and load testing
- ✅ CI/CD readiness
- ✅ Clean, maintainable code

Perfect for **SDET Agentic AI** interview preparation!

---

**⭐ Star this repo if you found it helpful!**

**🐛 Found a bug? Open an issue!**

**💬 Questions? Start a discussion!**
