package com.manywho.services.saml.configuration;

import java.util.Properties;

public class ServiceConfigurationProperties implements ServiceConfiguration {
    protected Properties properties;

    public ServiceConfigurationProperties() {
        try {
            properties = new Properties();
            properties.load(getClass().getClassLoader().getResourceAsStream("service.properties"));
        } catch (Exception exception) {
            properties = new Properties();
        }
    }

    @Override
    public String get(String key) {
        return properties.getProperty(key);
    }

    @Override
    public boolean has(String key) {
        return properties.containsKey(key);
    }
}