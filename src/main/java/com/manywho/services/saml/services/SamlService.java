package com.manywho.services.saml.services;

import com.manywho.services.saml.adapters.ManyWhoSaml2Settings;
import com.manywho.services.saml.entities.ApplicationConfiguration;
import com.manywho.services.saml.entities.SamlLogoutRequestHandler;
import com.manywho.services.saml.entities.SamlResponseHandler;
import com.manywho.services.saml.utils.SamlRequestGenerator;
import com.onelogin.saml2.authn.*;
import com.onelogin.saml2.settings.Saml2Settings;
import javax.inject.Inject;
import java.io.IOException;
import java.net.URISyntaxException;

public class SamlService {

    @Inject
    public SamlService() {}

    public SamlResponseHandler decryptResponse(ApplicationConfiguration configuration, String samlResponse, String redirectUri) {
        try {
            return new SamlResponseHandler(configuration, samlResponse, redirectUri);
        } catch (Exception e) {
            throw new RuntimeException("Unable to decrypt the SAML response: " + e.getMessage(), e);
        }
    }

    public String generateSamlLoginUrl(ApplicationConfiguration configuration) throws URISyntaxException, IOException {
        Saml2Settings appSettings = new ManyWhoSaml2Settings(configuration);
        AuthnRequest authReq = new AuthnRequest(appSettings, false, false, false);

        return SamlRequestGenerator.generateLoginRequest(configuration.getLoginUrl(), authReq.getEncodedAuthnRequest());
    }

    public SamlLogoutRequestHandler decryptLogoutRequest(ApplicationConfiguration configuration, String samlLogoutRequest, String logoutUrl) {
        try {
            return new SamlLogoutRequestHandler(configuration, samlLogoutRequest, logoutUrl);
        } catch (Exception e) {
            throw new RuntimeException("Unable to decrypt the SAML logout request: " + e.getMessage(), e);
        }
    }
}
