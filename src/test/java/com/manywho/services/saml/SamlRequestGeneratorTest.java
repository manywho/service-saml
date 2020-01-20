package com.manywho.services.saml;

import com.manywho.services.saml.utils.SamlRequestGenerator;
import org.junit.Assert;
import org.junit.Test;

import java.net.URISyntaxException;

public class SamlRequestGeneratorTest {
    @Test
    public void testGenerateUrl() throws URISyntaxException {
        String samlEncodedAuthRequest = "jadsifdsf=fsjdsof!";

        Assert.assertEquals("https://test.idp?abc=123&SAMLRequest=jadsifdsf%3Dfsjdsof%21",
                SamlRequestGenerator.generateLoginRequest("https://test.idp?abc=123", samlEncodedAuthRequest));
    }
}
