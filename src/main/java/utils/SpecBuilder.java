package utils;

import config.OllamaConfig;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import static org.hamcrest.Matchers.*;

public class SpecBuilder {
    
    private static RequestSpecification requestSpec;
    private static ResponseSpecification responseSpec;
    
    public static RequestSpecification getRequestSpec() {
        if (requestSpec == null) {
            requestSpec = new RequestSpecBuilder()
                .setBaseUri(OllamaConfig.getInstance().getBaseUri())
                .setContentType(ContentType.JSON)
                .addHeader("Accept", "application/json")
                .setRelaxedHTTPSValidation() // For local testing
                .addFilter(new RequestLoggingFilter())
                .addFilter(new ResponseLoggingFilter())
                .build();
        }
        return requestSpec;
    }
    
    public static ResponseSpecification getResponseSpec() {
        if (responseSpec == null) {
            responseSpec = new ResponseSpecBuilder()
                .expectContentType(ContentType.JSON)
                .expectResponseTime(lessThan(
                    (long) OllamaConfig.getInstance().getTimeout()
                ))
                .build();
        }
        return responseSpec;
    }
    
    // Spec for quick responses (non-AI endpoints)
    public static ResponseSpecification getFastResponseSpec() {
        return new ResponseSpecBuilder()
            .expectContentType(ContentType.JSON)
            .expectResponseTime(lessThan(1000L))
            .build();
    }
    
    // Reset specs (useful for test cleanup)
    public static void resetSpecs() {
        requestSpec = null;
        responseSpec = null;
    }
}
