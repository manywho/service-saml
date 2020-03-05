package com.manywho.services.saml.security;

import com.manywho.services.saml.configuration.ServiceConfigurationDefault;
import com.manywho.services.saml.configuration.ServiceConfigurationEnvironmentVariables;
import com.manywho.services.saml.configuration.ServiceConfigurationProperties;

import javax.inject.Inject;

public class RedisConfiguration extends ServiceConfigurationDefault {

    @Inject
    public RedisConfiguration(ServiceConfigurationEnvironmentVariables environment, ServiceConfigurationProperties properties) {
        super(environment, properties);
    }

    public String geRedisUrl() {
        return this.get("redis.url");
    }
}
