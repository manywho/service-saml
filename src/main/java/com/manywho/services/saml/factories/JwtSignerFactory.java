package com.manywho.services.saml.factories;

import com.auth0.jwt.JWTSigner;
import org.glassfish.hk2.api.Factory;

import javax.inject.Singleton;

public class JwtSignerFactory implements Factory<JWTSigner> {
    @Singleton
    public JWTSigner provide() {
        return new JWTSigner("secret");
    }

    @Override
    public void dispose(JWTSigner jwtSigner) {

    }
}
