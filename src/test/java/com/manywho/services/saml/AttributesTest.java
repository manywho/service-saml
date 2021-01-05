package com.manywho.services.saml;

import com.manywho.sdk.api.security.AuthenticatedWhoResult;
import com.manywho.services.saml.adapters.ManyWhoSaml2Settings;
import com.manywho.services.saml.adapters.ManyWhoSamlResponse;
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
import org.apache.xml.security.exceptions.XMLSecurityException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Base64;

import javax.xml.xpath.XPathExpressionException;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AttributesTest {

    @Test
    public void testEmailAddress1() throws Exception {
        AuthenticatedWhoResult who = getWho("email1.xml");
        Assert.assertEquals("test@test.com", who.getEmail());
    }

    @Test
    public void testEmailAddress2() throws Exception {
        AuthenticatedWhoResult who = getWho("email2.xml");
        Assert.assertEquals("test@test.com", who.getEmail());
    }

    @Test
    public void testEmailAddress3() throws Exception {
        AuthenticatedWhoResult who = getWho("email3.xml");
        Assert.assertEquals("test@test.com", who.getEmail());
    }

    @Test
    public void testEmailAddress4() throws Exception {
        AuthenticatedWhoResult who = getWho("email4.xml");
        Assert.assertEquals("test@test.com", who.getEmail());
    }

    @Test
    public void testName1() throws Exception {
        AuthenticatedWhoResult who = getWho("name1.xml");
        Assert.assertEquals("name", who.getFirstName());
    }

    @Test
    public void testName2() throws Exception {
        AuthenticatedWhoResult who = getWho("name2.xml");
        Assert.assertEquals("name", who.getFirstName());
    }

    @Test
    public void testName3() throws Exception {
        AuthenticatedWhoResult who = getWho("name3.xml");
        Assert.assertEquals("name", who.getFirstName());
    }

    @Test
    public void testName4() throws Exception {
        AuthenticatedWhoResult who = getWho("name4.xml");
        Assert.assertEquals("name", who.getFirstName());
    }

    @Test
    public void testName5() throws Exception {
        AuthenticatedWhoResult who = getWho("name5.xml");
        Assert.assertEquals("name", who.getFirstName());
    }

    @Test
    public void testName6() throws Exception {
        AuthenticatedWhoResult who = getWho("name6.xml");
        Assert.assertEquals("name", who.getFirstName());
    }

    @Test
    public void testName7() throws Exception {
        AuthenticatedWhoResult who = getWho("name7.xml");
        Assert.assertEquals("name", who.getFirstName());
    }

    @Test
    public void testLastName1() throws Exception {
        AuthenticatedWhoResult who = getWho("lastname1.xml");
        Assert.assertEquals("surname", who.getLastName());
    }

    @Test
    public void testLastName2() throws Exception {
        AuthenticatedWhoResult who = getWho("lastname2.xml");
        Assert.assertEquals("surname", who.getLastName());
    }

    @Test
    public void testLastName3() throws Exception {
        AuthenticatedWhoResult who = getWho("lastname3.xml");
        Assert.assertEquals("surname", who.getLastName());
    }

    @Test
    public void testLastName4() throws Exception {
        AuthenticatedWhoResult who = getWho("lastname4.xml");
        Assert.assertEquals("surname", who.getLastName());
    }

    @Test
    public void testLastName5() throws Exception {
        AuthenticatedWhoResult who = getWho("lastname5.xml");
        Assert.assertEquals("surname", who.getLastName());
    }

    @Test
    public void testGetGroups() throws Exception {
        AuthenticatedWhoResult who = getWho("groups1.xml");
        Assert.assertEquals("group1,group2", who.getPrimaryGroupName());
    }

    @Test
    public void testPrimaryGroupName1() throws Exception {
        AuthenticatedWhoResult who = getWho("primarygroupname1.xml");
        Assert.assertEquals("primarygroupname", who.getPrimaryGroupName());
    }

    @Test
    public void testPrimaryGroupName2() throws Exception {
        AuthenticatedWhoResult who = getWho("primarygroupname2.xml");
        Assert.assertEquals("primarygroupname", who.getPrimaryGroupName());
    }

    @Test
    public void testPrimaryGroupName3() throws Exception {
        AuthenticatedWhoResult who = getWho("primarygroupname3.xml");
        Assert.assertEquals("primarygroupname", who.getPrimaryGroupName());
    }

    @Test
    public void testPrimaryGroupId1() throws Exception {
        AuthenticatedWhoResult who = getWho("primarygroupid1.xml");
        Assert.assertEquals("primarygroupid", who.getPrimaryGroupId());
    }

    @Test
    public void testPrimaryGroupId2() throws Exception {
        AuthenticatedWhoResult who = getWho("primarygroupid2.xml");
        Assert.assertEquals("primarygroupid", who.getPrimaryGroupId());
    }

    @Test
    public void testPrimaryGroupId3() throws Exception {
        AuthenticatedWhoResult who = getWho("primarygroupid3.xml");
        Assert.assertEquals("primarygroupid", who.getPrimaryGroupId());
    }

    private AuthenticatedWhoResult getWho(String file)
            throws Exception, IOException, CertificateException, GeneralSecurityException, XMLSecurityException, XPathExpressionException {
        // if I remove this line the encryption doesn't works
        org.apache.xml.security.Init.init();

        Document document = Util.loadXML(getFileContent(file));

        X509Certificate cert = Util.loadCert(getFileContent("public-certificate.txt"));
        PrivateKey privateKey = Util.loadPrivateKey(getFileContent("private-certificate.txt"));

        String signedResponse = Util.addSign(document, privateKey, cert, null);

        String samlResponse = base64String(signedResponse.getBytes());
        // String samlResponse = base64String(getFileContent(file).getBytes());

        String publicCertificate = getFileContent("public-certificate.txt");
        String privateCertificate = getFileContent("private-certificate.txt");

        String issuer = "https://capriza.github.io/samling/samling.html";

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

        SamlResponseHandler handler = new SamlResponseHandler(configuration, samlResponse, "https://flow.manywho.com/api/run/1/saml");

        JwtService jwtService = new JwtService("test-secret");
        JwtService jwtServiceSpy = Mockito.spy(jwtService);

        doNothing().when(jwtServiceSpy).validate(anyString());

        AuthenticationService service = new AuthenticationService(jwtServiceSpy, cacheManager);

        return service.createAuthenticatedWhoResult(configuration, handler, false);
    }

    private String getFileContent(String path) throws IOException {
        return IOUtils.toString(getClass().getClassLoader().getResourceAsStream(path), StandardCharsets.UTF_8);
    }

    private String base64String(byte[] bytes) {
        return new String(Base64.getEncoder().encode(bytes));
    }
}
