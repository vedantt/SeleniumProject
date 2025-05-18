package com.uitests.utils;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {
    private static final Properties properties = new Properties();
    private static final String CONFIG_FILE_NAME = "env.properties";
    private static final Logger logger = LoggerFactory.getLogger(ConfigReader.class);

    static {
        try (InputStream input = ConfigReader.class.getClassLoader().getResourceAsStream(CONFIG_FILE_NAME)) {
            if (input == null) {
                logger.error("Sorry, unable to find " + CONFIG_FILE_NAME);
                throw new FileNotFoundException("Property file '" + CONFIG_FILE_NAME + "' not found in the classpath");
            }
            properties.load(input);
            logger.info("Configuration properties loaded successfully from {}", CONFIG_FILE_NAME);
        } catch (IOException ex) {
            logger.error("Error loading configuration properties from " + CONFIG_FILE_NAME, ex);
            throw new RuntimeException("Error loading configuration properties", ex);
        }
    }

    public static String getProperty(String key) {
        String value = properties.getProperty(key);
        if (value != null) {
            return value.trim();
        } else {
            logger.warn("Property not found for key: {}", key);
            return null;
        }
    }

    public static String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue).trim();
    }

    public static int getIntProperty(String key, int defaultValue) {
        String value = getProperty(key);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                logger.warn("Failed to parse integer for key: {}. Value: '{}'. Using default value: {}", key, value, defaultValue);
                return defaultValue;
            }
        }
        return defaultValue;
    }

    public static boolean getBooleanProperty(String key, boolean defaultValue) {
        String value = getProperty(key);
        if (value != null) {
            return Boolean.parseBoolean(value);
        }
        return defaultValue;
    }
}