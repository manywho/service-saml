package com.manywho.services.saml.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.joda.time.DateTime;
import javax.inject.Inject;

public class JwtService {
    private Algorithm algorithm;
    private JWTVerifier verifier;

    @Inject
    public JwtService(String secret) {
        algorithm = Algorithm.HMAC256(secret);
        verifier = JWT.require(algorithm)
                .withIssuer("saml-service")
                .acceptLeeway(1)   //1 sec for nbf and iat
                .acceptExpiresAt(5)   //5 secs for exp
                .build();
    }

    public String sign(String identifier, DateTime notBefore, DateTime notAfter) {

        long notAfterSeconds = DateTime.now().plusHours(1).getMillis() / 1000;
        long notBeforeSeconds = DateTime.now().getMillis() / 1000;

        if (notBefore != null) {
            notBeforeSeconds = notBefore.getMillis() / 1000;
        }

        if( notAfter != null) {
            notAfterSeconds = notAfter.getMillis() / 1000;
        }

        return JWT.create()
                .withIssuer("saml-service")
                .withClaim("sub", identifier)
                .withClaim("iat", DateTime.now().getMillis() / 1000)
                .withClaim("exp", notAfterSeconds)
                .withClaim("nbf", notBeforeSeconds)
                .sign(algorithm);
    }

    public boolean isValid(String token) {
        try{
            DecodedJWT jwt = verifier.verify(token);
        } catch (JWTVerificationException exception){
            return false;
        }
        return true;
    }


    void validate(String token) {
        // it will throw an exception if the window time between not before and not after is wrong
        verifier.verify(token);
    }
}
