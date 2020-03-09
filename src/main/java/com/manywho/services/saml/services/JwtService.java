package com.manywho.services.saml.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.manywho.services.saml.actions.Group;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

public class JwtService {
    private Algorithm algorithm;
    private JWTVerifier verifier;

    public JwtService(String secret) {
        algorithm = Algorithm.HMAC256(secret);
        verifier = JWT.require(algorithm)
                .withIssuer("saml-service")
                .acceptLeeway(1)   //1 sec for nbf and iat
                .acceptExpiresAt(5)   //5 secs for exp
                .build();
    }

    public String sign(String identifier, LocalDateTime notBefore, LocalDateTime notAfter, List<String> groups) {
        long notAfterSeconds = LocalDateTime.now().plusMinutes(14).toEpochSecond(ZoneOffset.UTC);
        long notBeforeSeconds = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);

        if (notBefore != null) {
            notBeforeSeconds = notBefore.atOffset(ZoneOffset.UTC).toEpochSecond();
        }

        if( notAfter != null) {
            notAfterSeconds = notAfter.atOffset(ZoneOffset.UTC).toEpochSecond();
        }

        return JWT.create()
                .withIssuer("saml-service")
                .withClaim("sub", identifier)
                .withClaim("iat", LocalDateTime.now().toEpochSecond(ZoneOffset.UTC))
                .withClaim("exp", notAfterSeconds)
                .withClaim("nbf", notBeforeSeconds)
                .withArrayClaim("groups", groups.toArray(new String[0]))
                .sign(algorithm);
    }

    static public List<Group> getGroups(String token) {
        DecodedJWT tokenDecoded = JWT.decode(token);
        List<Group> groups  = new ArrayList<>();

        if (tokenDecoded.getClaim("groups").isNull() == false) {
            for (String group: tokenDecoded.getClaim("groups").asList(String.class)) {
                groups.add(new Group(group));
            }
        }

        return groups;
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
