package com.manywho.services.saml.test;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.manywho.sdk.api.security.AuthenticatedWhoResult;
import com.manywho.services.saml.entities.ApplicationConfiguration;
import com.manywho.services.saml.entities.SamlResponseHandler;
import com.manywho.services.saml.managers.CacheManager;
import com.manywho.services.saml.services.AuthenticationService;
import com.manywho.services.saml.services.JwtService;
import com.manywho.services.saml.services.SamlService;
import org.junit.Assert;
import org.junit.Test;
import org.apache.commons.io.IOUtils;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PrimaryGroupNameTest {

    @Test
    public void testPrimaryGroupName() throws Exception {
        // if I remove this line the encryption doesn't works
        org.apache.xml.security.Init.init();

        String samlResponse = base64String(getFileContent("saml-response.xml").getBytes());

        String publicCertificate = getFileContent("public-certificate.txt");
        String privateCertificate = getFileContent("private-certificate.txt");

        String issuer = "https://capriza.github.io/samling/samling.html";

        SamlService samlService = new SamlService();
        ApplicationConfiguration configuration = mock(ApplicationConfiguration.class);
        CacheManager cacheManager = mock(CacheManager.class);

        when(configuration.getCertificate())
                .thenReturn(publicCertificate);

        when(configuration.getSpPrivateKey())
                .thenReturn(privateCertificate);

        when(configuration.getIdpEntityId())
                .thenReturn(issuer);

        when(configuration.getLoginUrl())
                .thenReturn("https://capriza.github.io/samling/samling.html");

        doNothing().when(cacheManager).removeUserGroups(anyString());
        doNothing().when(cacheManager).saveUserGroups(anyString(), any(ArrayList.class));

        SamlResponseHandler handler = samlService.decryptResponse(configuration, samlResponse, "https://flow.manywho.com/api/run/1/saml");

        JwtService jwtService = new JwtService("test-secret");
        AuthenticationService service = new AuthenticationService(jwtService, cacheManager);

        AuthenticatedWhoResult result = service.createAuthenticatedWhoResult(configuration, handler, false);

        JWTVerifier verifier = JWT.require(Algorithm.HMAC256("test-secret"))
            .withIssuer("saml-service")
            .acceptExpiresAt(Long.MAX_VALUE / 1000)
            .build();

        DecodedJWT token = verifier.verify(result.getToken());

        Assert.assertEquals("group1", token.getClaim("pgn").asString());
    }

    private String getFileContent(String path) throws IOException {
        return IOUtils.toString(getClass().getClassLoader().getResourceAsStream(path), StandardCharsets.UTF_8);
    }

    private String base64String(byte[] bytes) {
        return new String(Base64.getEncoder().encode(bytes));
    }
}