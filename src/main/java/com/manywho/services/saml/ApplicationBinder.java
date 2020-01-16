package com.manywho.services.saml;

import com.manywho.services.saml.factories.JwtServiceFactory;
import com.manywho.services.saml.managers.AuthManager;
import com.manywho.services.saml.managers.CacheManager;
import com.manywho.services.saml.security.SecurityConfiguration;
import com.manywho.services.saml.services.AuthenticationService;
import com.manywho.services.saml.services.AuthorizationService;
import com.manywho.services.saml.services.JwtService;
import com.manywho.services.saml.services.SamlService;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import javax.inject.Singleton;

public class ApplicationBinder extends AbstractBinder {
    @Override
    protected void configure() {
        bindFactory(JwtServiceFactory.class).to(JwtService.class).in(Singleton.class);
        bind(AuthManager.class).to(AuthManager.class);
        bind(AuthenticationService.class).to(AuthenticationService.class);
        bind(SecurityConfiguration.class).to(SecurityConfiguration.class);
        bind(AuthorizationService.class).to(AuthorizationService.class);
        bind(SamlService.class).to(SamlService.class);
        bind(CacheManager.class).to(CacheManager.class);
    }
}
