package com.manywho.services.saml.configuration;

import java.util.Map;

public class ServiceConfigurationEnvironmentVariables implements ServiceConfiguration {
    protected Map<String, String> environmentVariables;

    public ServiceConfigurationEnvironmentVariables() {
        environmentVariables = System.getenv();
    }

    @Override
    public String get(String key) {
        return environmentVariables.get(key);
    }

    @Override
    public boolean has(String key) {
        return environmentVariables.containsKey(key);
    }
}