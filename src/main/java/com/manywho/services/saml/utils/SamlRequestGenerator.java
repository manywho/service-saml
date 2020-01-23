package com.manywho.services.saml.utils;

import org.apache.http.client.utils.URIBuilder;
import java.net.URISyntaxException;

public class SamlRequestGenerator {

    public static String generateLoginRequest(String loginUrl, String encodedAuthnRequest) throws URISyntaxException {
        return new URIBuilder(loginUrl)
                .addParameter("SAMLRequest", encodedAuthnRequest)
                .build()
                .toString();
    }
}
