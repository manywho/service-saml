package com.manywho.services.saml.security;

import com.manywho.services.saml.configuration.ServiceConfigurationDefault;
import com.manywho.services.saml.configuration.ServiceConfigurationEnvironmentVariables;
import com.manywho.services.saml.configuration.ServiceConfigurationProperties;

import javax.inject.Inject;

public class SecurityConfiguration extends ServiceConfigurationDefault {
    @Inject
    public SecurityConfiguration(ServiceConfigurationEnvironmentVariables environment, ServiceConfigurationProperties properties) {
        super(environment, properties);
    }

    public String getSecret() {
        return this.get("secret");
    }
}
