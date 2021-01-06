package com.manywho.services.saml;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.manywho.sdk.api.security.AuthenticatedWhoResult;
import com.manywho.services.saml.adapters.ManyWhoSamlResponse;
import com.manywho.services.saml.adapters.ManyWhoSaml2Settings;
import com.manywho.services.saml.entities.ApplicationConfiguration;
import com.manywho.services.saml.entities.SamlResponseHandler;
import com.manywho.services.saml.managers.CacheManager;
import com.manywho.services.saml.services.AuthenticationService;
import com.manywho.services.saml.services.JwtService;
import com.manywho.services.saml.services.SamlService;
import com.onelogin.saml2.util.Util;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.w3c.dom.Document;
import org.apache.commons.io.IOUtils;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SessionNotAfterTest {

    @Test
    public void testSessionNotAfterSamlResponse() throws Exception {
        // if I remove this line the encryption doesn't works
        org.apache.xml.security.Init.init();

        String publicCertificate = getFileContent("public-certificate.txt");
        String privateCertificate = getFileContent("private-certificate.txt");
        String issuer = "https://capriza.github.io/samling/samling.html";

        Document document = Util.loadXML(getFileContent("saml-response.xml"));

        X509Certificate cert = Util.loadCert(publicCertificate);
        PrivateKey privateKey = Util.loadPrivateKey(privateCertificate);

        String signedResponse = Util.addSign(document, privateKey, cert, null);

        String samlResponse = base64String(signedResponse.getBytes());

        SamlService samlService = new SamlService();
        ApplicationConfiguration configuration = mock(ApplicationConfiguration.class);

        when(configuration.getCertificate())
                .thenReturn(publicCertificate);

        when(configuration.getSpPrivateKey())
                .thenReturn(privateCertificate);

        when(configuration.getIdpEntityId())
                .thenReturn(issuer);

        when(configuration.getLoginUrl())
                .thenReturn("https://capriza.github.io/samling/samling.html");

        SamlResponseHandler handler = samlService.decryptResponse(configuration, samlResponse, "https://flow.manywho.com/api/run/1/saml");
        
        Assert.assertNotNull(handler.getResponse().getSessionNotAfter());
    }

    @Test
    public void testSessionNotAfterJWT() throws Exception {
        // if I remove this line the encryption doesn't works
        org.apache.xml.security.Init.init();

        String publicCertificate = getFileContent("public-certificate.txt");
        String privateCertificate = getFileContent("private-certificate.txt");

        String issuer = "https://capriza.github.io/samling/samling.html";

        Document document = Util.loadXML(getFileContent("saml-response.xml"));

        X509Certificate cert = Util.loadCert(getFileContent("public-certificate.txt"));
        PrivateKey privateKey = Util.loadPrivateKey(getFileContent("private-certificate.txt"));

        String signedResponse = Util.addSign(document, privateKey, cert, null);

        String samlResponse = base64String(signedResponse.getBytes());

        ApplicationConfiguration configuration = mock(ApplicationConfiguration.class);
        CacheManager cacheManager = mock(CacheManager.class);
        SamlResponseHandler handler = mock(SamlResponseHandler.class);

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

        when(handler.isValid()).thenReturn(true);

        ManyWhoSamlResponse response = new ManyWhoSamlResponse(new ManyWhoSaml2Settings(configuration), samlResponse, "https://flow.manywho.com/api/run/1/saml");
        when(handler.getResponse()).thenReturn(response);

        JwtService jwtService = new JwtService("test-secret");
        JwtService jwtServiceSpy = Mockito.spy(jwtService);

        doNothing().when(jwtServiceSpy).validate(anyString());

        AuthenticationService service = new AuthenticationService(jwtServiceSpy, cacheManager);

        AuthenticatedWhoResult result = service.createAuthenticatedWhoResult(configuration, handler);

        JWTVerifier verifier = JWT.require(Algorithm.HMAC256("test-secret"))
            .withIssuer("saml-service")
            .acceptExpiresAt(Long.MAX_VALUE / 1000)
            .build();

        DecodedJWT token = verifier.verify(result.getToken());

        Calendar expected = new GregorianCalendar(2030, 0, 1);
        expected.set(Calendar.HOUR, 0);
        expected.set(Calendar.MINUTE, 0);
        expected.set(Calendar.SECOND, 0);

        Calendar actual = new GregorianCalendar();
        actual.setTime(token.getExpiresAt());

        Assert.assertEquals(expected.get(Calendar.YEAR), actual.get(Calendar.YEAR));
        Assert.assertEquals(expected.get(Calendar.MONTH), actual.get(Calendar.MONTH));
        Assert.assertEquals(expected.get(Calendar.DAY_OF_MONTH), actual.get(Calendar.DAY_OF_MONTH));
        Assert.assertEquals(expected.get(Calendar.HOUR), actual.get(Calendar.HOUR));
        Assert.assertEquals(expected.get(Calendar.MINUTE), actual.get(Calendar.MINUTE));
        Assert.assertEquals(expected.get(Calendar.SECOND), actual.get(Calendar.SECOND));
    }

    private String getFileContent(String path) throws IOException {
        return IOUtils.toString(getClass().getClassLoader().getResourceAsStream(path), StandardCharsets.UTF_8);
    }

    private String base64String(byte[] bytes) {
        return new String(Base64.getEncoder().encode(bytes));
    }
}
