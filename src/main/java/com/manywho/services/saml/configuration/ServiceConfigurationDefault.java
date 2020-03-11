package com.manywho.services.saml.configuration;

import com.google.common.base.CaseFormat;

import javax.inject.Inject;

public class ServiceConfigurationDefault implements ServiceConfiguration {
    private ServiceConfiguration environment;
    private ServiceConfiguration properties;

    @Inject
    public ServiceConfigurationDefault(ServiceConfigurationEnvironmentVariables environment,
                                       ServiceConfigurationProperties properties) {
        this.environment = environment;
        this.properties = properties;
    }

    @Override
    public String get(String key) {
        // Load from the service.properties first
        String value = properties.get(key);

        // Overwrite with any environment variables passed in
        if (environment.has(key)) {
            value = environment.get(key);
        }

        // Look for any environment variables with the formatting convention of VARIABLE_NAME, converted from camelCase
        String environmentFormatKeyCamelcase = convertCamelcaseKeyToEnvironmentFormat(key);
        if (environment.has(environmentFormatKeyCamelcase)) {
            value = environment.get(environmentFormatKeyCamelcase);
        }

        // Look for any environment variables with the formatting convention of VARIABLE_NAME, converted from dot.notation
        String environmentFormatKeyDotNotation = convertDotNotationKeyToEnvironmentFormat(key);
        if (environment.has(environmentFormatKeyDotNotation)) {
            value = environment.get(environmentFormatKeyDotNotation);
        }

        // Look for any environment variables with the combined formatting conventions from above
        String combinedFormatKey = convertCombinedNotationKeyToEnvironmentFormat(key);
        if (environment.has(combinedFormatKey)) {
            value = environment.get(combinedFormatKey);
        }

        return value;
    }

    @Override
    public boolean has(String key) {
        return properties.has(key) ||
                environment.has(key) ||
                environment.has(convertCamelcaseKeyToEnvironmentFormat(key)) ||
                environment.has(convertDotNotationKeyToEnvironmentFormat(key)) ||
                environment.has(convertCombinedNotationKeyToEnvironmentFormat(key));

    }

    private static String convertCamelcaseKeyToEnvironmentFormat(String key) {
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, key);
    }

    private static String convertDotNotationKeyToEnvironmentFormat(String key) {
        if (key == null) {
            return null;
        }

        return key.replace(".", "_").toUpperCase();
    }

    private static String convertCombinedNotationKeyToEnvironmentFormat(String key) {
        return convertDotNotationKeyToEnvironmentFormat(convertCamelcaseKeyToEnvironmentFormat(key));
    }
}