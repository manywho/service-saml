package com.manywho.services.saml.configuration;

public interface ServiceConfiguration {
    String get(String key);
    boolean has(String key);
}