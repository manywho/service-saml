package com.manywho.services.saml;
import com.manywho.services.saml.entities.ApplicationConfiguration;
import com.manywho.services.saml.entities.SamlResponseHandler;
import com.manywho.services.saml.services.SamlService;
import org.junit.Assert;
import org.junit.Test;
import org.apache.commons.io.IOUtils;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;


import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AttributesTest {

    @Test
    public void testEmailAddress1() throws Exception {
        SamlResponseHandler handler = this.getHandler("email1.xml");
        Assert.assertEquals("test@test.com", handler.getEmailAddress());
    }

    @Test
    public void testEmailAddress2() throws Exception {
        SamlResponseHandler handler = this.getHandler("email2.xml");
        Assert.assertEquals("test@test.com", handler.getEmailAddress());
    }

    @Test
    public void testEmailAddress3() throws Exception {
        SamlResponseHandler handler = this.getHandler("email3.xml");
        Assert.assertEquals("test@test.com", handler.getEmailAddress());
    }

    @Test
    public void testEmailAddress4() throws Exception {
        SamlResponseHandler handler = this.getHandler("email4.xml");
        Assert.assertEquals("test@test.com", handler.getEmailAddress());
    }

    @Test
    public void testName1() throws Exception {
        SamlResponseHandler handler = this.getHandler("name1.xml");
        Assert.assertEquals("name", handler.getFirstName());
    }

    @Test
    public void testName2() throws Exception {
        SamlResponseHandler handler = this.getHandler("name2.xml");
        Assert.assertEquals("name", handler.getFirstName());
    }

    @Test
    public void testName3() throws Exception {
        SamlResponseHandler handler = this.getHandler("name3.xml");
        Assert.assertEquals("name", handler.getFirstName());
    }

    @Test
    public void testName4() throws Exception {
        SamlResponseHandler handler = this.getHandler("name4.xml");
        Assert.assertEquals("name", handler.getFirstName());
    }

    @Test
    public void testName5() throws Exception {
        SamlResponseHandler handler = this.getHandler("name5.xml");
        Assert.assertEquals("name", handler.getFirstName());
    }

    @Test
    public void testName6() throws Exception {
        SamlResponseHandler handler = this.getHandler("name6.xml");
        Assert.assertEquals("name", handler.getFirstName());
    }

    @Test
    public void testName7() throws Exception {
        SamlResponseHandler handler = this.getHandler("name7.xml");
        Assert.assertEquals("name", handler.getFirstName());
    }

    @Test
    public void testLastName1() throws Exception {
        SamlResponseHandler handler = this.getHandler("lastname1.xml");
        Assert.assertEquals("surname", handler.getLastName());
    }

    @Test
    public void testLastName2() throws Exception {
        SamlResponseHandler handler = this.getHandler("lastname2.xml");
        Assert.assertEquals("surname", handler.getLastName());
    }

    @Test
    public void testLastName3() throws Exception {
        SamlResponseHandler handler = this.getHandler("lastname3.xml");
        Assert.assertEquals("surname", handler.getLastName());
    }

    @Test
    public void testLastName4() throws Exception {
        SamlResponseHandler handler = this.getHandler("lastname4.xml");
        Assert.assertEquals("surname", handler.getLastName());
    }

    @Test
    public void testLastName5() throws Exception {
        SamlResponseHandler handler = this.getHandler("lastname5.xml");
        Assert.assertEquals("surname", handler.getLastName());
    }

    @Test
    public void testGetGroups() throws Exception {
        SamlResponseHandler handler = this.getHandler("groups.xml");
        Assert.assertEquals("groups", handler.getGroups().get(0));
    }

    @Test
    public void testPrimaryGroupName1() throws Exception {
        SamlResponseHandler handler = this.getHandler("primarygroupname1.xml");
        Assert.assertEquals("primarygroupname", handler.getPrimaryGroupName());
    }

    @Test
    public void testPrimaryGroupName2() throws Exception {
        SamlResponseHandler handler = this.getHandler("primarygroupname2.xml");
        Assert.assertEquals("primarygroupname", handler.getPrimaryGroupName());
    }

    @Test
    public void testPrimaryGroupName3() throws Exception {
        SamlResponseHandler handler = this.getHandler("primarygroupname3.xml");
        Assert.assertEquals("primarygroupname", handler.getPrimaryGroupName());
    }

    @Test
    public void testPrimaryGroupId1() throws Exception {
        SamlResponseHandler handler = this.getHandler("primarygroupid1.xml");
        Assert.assertEquals("primarygroupid", handler.getPrimaryGroupId());
    }

    @Test
    public void testPrimaryGroupId2() throws Exception {
        SamlResponseHandler handler = this.getHandler("primarygroupid2.xml");
        Assert.assertEquals("primarygroupid", handler.getPrimaryGroupId());
    }

    @Test
    public void testPrimaryGroupId3() throws Exception {
        SamlResponseHandler handler = this.getHandler("primarygroupid3.xml");
        Assert.assertEquals("primarygroupid", handler.getPrimaryGroupId());
    }

    private SamlResponseHandler getHandler(String file) throws IOException {
        // if I remove this line the encryption doesn't works
        org.apache.xml.security.Init.init();

        String samlResponse = base64String(getFileContent(file).getBytes());

        String publicCertificate = getFileContent("public-certificate.txt");
        String privateCertificate = getFileContent("private-certificate.txt");

        String issuer = "https://capriza.github.io/samling/samling.html";

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

        return samlService.decryptResponse(configuration, samlResponse, "https://flow.manywho.com/api/run/1/saml");
    }

    private String getFileContent(String path) throws IOException {
        return IOUtils.toString(getClass().getClassLoader().getResourceAsStream(path), StandardCharsets.UTF_8);
    }

    private String base64String(byte[] bytes) {
        return new String(Base64.getEncoder().encode(bytes));
    }
}
