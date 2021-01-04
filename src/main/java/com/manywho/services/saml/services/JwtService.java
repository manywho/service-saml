package com.manywho.services.saml.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

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

    public String sign(String identifier, String primaryGroupId, String primaryGroupName, LocalDateTime notBefore, LocalDateTime notAfter) {
        long notAfterSeconds = LocalDateTime.now().plusMinutes(14).toEpochSecond(ZoneOffset.UTC);
        long notBeforeSeconds = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);

        if (notBefore != null) {
            notBeforeSeconds = notBefore.atOffset(ZoneOffset.UTC).toEpochSecond();
        }

        if( notAfter != null) {
            notAfterSeconds = notAfter.atOffset(ZoneOffset.UTC).toEpochSecond();
        }

        JWTCreator.Builder jwtBuilder = JWT.create()
                .withIssuer("saml-service")
                .withClaim("sub", identifier)
                .withClaim("iat", LocalDateTime.now().toEpochSecond(ZoneOffset.UTC))
                .withClaim("exp", notAfterSeconds)
                .withClaim("nbf", notBeforeSeconds);

        // the primaryGroupId and PrimaryGroupName are ignored by engine when we return them during authentication
        // we pass those values into the jwt token then they can be returned during authorization

        if (primaryGroupId != null && primaryGroupId.isEmpty() == false) {
            jwtBuilder.withClaim("pgi", primaryGroupId);
        }

        if (primaryGroupId != null && primaryGroupName.isEmpty() == false) {
            jwtBuilder.withClaim("pgn", primaryGroupName);
        }

        return jwtBuilder.sign(algorithm);
    }

    public boolean isValid(String token) {
        try{
            verifier.verify(token);
        } catch (JWTVerificationException exception){
            return false;
        }
        return true;
    }

    public DecodedJWT decode(String token) {
        return verifier.verify(token);
    }

    /**
     * This method throws an exception if the window time between not before and not after is wrong
     *
     * @param token jwt string version of a token generated by this class
     */
    public void validate(String token) {
        verifier.verify(token);
    }
}
