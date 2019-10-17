package com.manywho.services.saml;
import com.manywho.services.saml.entities.Configuration;
import com.manywho.services.saml.entities.SamlResponseHandler;
import com.manywho.services.saml.services.SamlService;
import org.junit.Assert;
import org.junit.Test;
import org.apache.commons.io.IOUtils;
import java.io.IOException;
import java.util.Base64;


import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AssertionEncryptionTest {

    @Test
    public void testEncryptSamlAssertion() throws Exception {
        // if I remove this line the encryption doesn't works
        org.apache.xml.security.Init.init();

        String samlResponse = base64String(getFileContent("saml-response.xml").getBytes());

        String publicCertificate = getFileContent("public-certificate.txt");
        String privateCertificate = getFileContent("private-certificate.txt");

        String issuer = "https://capriza.github.io/samling/samling.html";

        SamlService samlService = new SamlService();
        Configuration configuration = mock(Configuration.class);

        when(configuration.getCertificate())
                .thenReturn(publicCertificate);

        when(configuration.getSpPrivateKey())
                .thenReturn(privateCertificate);

        when(configuration.getIdpEntityId())
                .thenReturn(issuer);

        when(configuration.getLoginUrl())
                .thenReturn("https://capriza.github.io/samling/samling.html");

        SamlResponseHandler handler = samlService.decryptResponse(configuration, samlResponse, "https://flow.manywho.com/api/run/1/saml");
        Assert.assertEquals("test@test.com", handler.getEmailAddress());
        Assert.assertEquals("Test", handler.getNameIdentifier());
        Assert.assertEquals("name", handler.getFirstName());
        Assert.assertEquals("surname", handler.getLastName());
        Assert.assertNull(handler.getError());
        Assert.assertEquals(1, handler.getGroups().size());
        Assert.assertEquals("", handler.getGroups().get(0));

    }

    private String getFileContent(String path) throws IOException {
        return IOUtils.toString(getClass().getClassLoader().getResourceAsStream(path));
    }

    private String base64String(byte[] bytes) {
        return new String(Base64.getEncoder().encode(bytes));
    }
}
