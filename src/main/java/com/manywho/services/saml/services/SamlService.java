package com.manywho.services.saml.services;

import com.manywho.services.saml.adapters.ManyWhoSaml2Settings;
import com.manywho.services.saml.entities.Configuration;
import com.manywho.services.saml.entities.SamlResponseHandler;
import com.onelogin.saml2.authn.*;
import com.onelogin.saml2.settings.Saml2Settings;
import javax.inject.Inject;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;

public class SamlService {

    @Inject
    public SamlService() {}

    public SamlResponseHandler decryptResponse(Configuration configuration, String samlResponse, String redirectUri) {
        try {
            return new SamlResponseHandler(configuration, samlResponse, redirectUri);
        } catch (Exception e) {
            throw new RuntimeException("Unable to decrypt the SAML response: " + e.getMessage(), e);
        }
    }

    public String generateSamlLoginUrl(com.manywho.services.saml.entities.Configuration configuration) throws IOException, XMLStreamException {
        Saml2Settings appSettings = new ManyWhoSaml2Settings(configuration);

        AuthnRequest authReq = new AuthnRequest(appSettings, false, false, false);


        return configuration.getLoginUrl() + "?SAMLRequest=" + authReq.getEncodedAuthnRequest();
    }
}
