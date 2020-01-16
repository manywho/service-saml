package com.manywho.services.saml.factories;

import com.manywho.services.saml.services.JwtService;
import org.glassfish.hk2.api.Factory;

import javax.inject.Singleton;

public class JwtServiceFactory implements Factory<JwtService> {
    @Singleton
    public JwtService provide() {
        return new JwtService(System.getenv("secret"));
    }

    @Override
    public void dispose(JwtService jwtSigner) {

    }
}
