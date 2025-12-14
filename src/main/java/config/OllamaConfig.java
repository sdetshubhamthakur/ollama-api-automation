package config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class OllamaConfig {
	
	private static OllamaConfig instance;
	private Properties properties;
	
	private OllamaConfig() {
		properties = new Properties();
		loadProperties();
	}
	
	public static OllamaConfig getInstance() {
        if (instance == null) {
            synchronized (OllamaConfig.class) {
                if (instance == null) {
                    instance = new OllamaConfig();
                }
            }
        }
        return instance;
    }

	
	private void loadProperties() {
        try (InputStream input = getClass()
                .getClassLoader()
                .getResourceAsStream("config.properties")) {
            if (input != null) {
                properties.load(input);
            } else {
                setDefaultProperties();
            }
        } catch (IOException e) {
            setDefaultProperties();
        }
    }
	
	private void setDefaultProperties() {
        properties.setProperty("base.uri", "http://localhost:11434");
        properties.setProperty("default.model", "llama3.2:3b");
        properties.setProperty("timeout", "30000");
        properties.setProperty("max.retries", "3");
    }
    
    public String getBaseUri() {
        return properties.getProperty("base.uri");
    }
    
    public String getDefaultModel() {
        return properties.getProperty("default.model");
    }
    
    public int getTimeout() {
        return Integer.parseInt(properties.getProperty("timeout", "30000"));
    }
    
    public int getMaxRetries() {
        return Integer.parseInt(properties.getProperty("max.retries", "3"));
    }
}
