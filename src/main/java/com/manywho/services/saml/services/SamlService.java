package com.manywho.services.saml.services;

import com.manywho.sdk.entities.run.elements.config.Authorization;
import com.manywho.services.saml.adapters.ManyWhoSaml2Settings;
import com.manywho.services.saml.entities.Configuration;
import com.manywho.services.saml.entities.SamlResponseHandler;
import com.onelogin.saml2.authn.*;
import com.onelogin.saml2.settings.Saml2Settings;
import javax.inject.Inject;
import java.io.IOException;
import java.net.URLEncoder;

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

    public String generateSamlLoginUrl(Configuration configuration) throws IOException {
        Saml2Settings appSettings = new ManyWhoSaml2Settings(configuration);

        AuthnRequest authReq = new AuthnRequest(appSettings, false, false, false);

        return configuration.getLoginUrl() + "?SAMLRequest=" + URLEncoder.encode(authReq.getEncodedAuthnRequest(), "UTF-8");
    }
}
