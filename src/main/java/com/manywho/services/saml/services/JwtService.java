package com.manywho.services.saml.services;

import com.auth0.jwt.JWTSigner;
import org.joda.time.DateTime;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

public class JwtService {
    private final JWTSigner jwtSigner;

    @Inject
    public JwtService(JWTSigner jwtSigner) {
        this.jwtSigner = jwtSigner;
    }

    public String sign(String identifier) {
        Map<String, Object> jwtClaims = new HashMap<>();
        jwtClaims.put("sub", identifier);
        jwtClaims.put("iat", DateTime.now().getMillis() / 1000);
        jwtClaims.put("exp", new DateTime().plusWeeks(2).getMillis() / 1000);

        return jwtSigner.sign(jwtClaims);
    }
}
