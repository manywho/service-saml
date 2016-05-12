package com.manywho.services.saml.services;

import com.manywho.services.saml.entities.SamlResponse;
import com.onelogin.AccountSettings;
import com.onelogin.saml.Response;

import javax.inject.Inject;
import javax.ws.rs.core.UriInfo;

public class SamlService {
    private final UriInfo uriInfo;

    @Inject
    public SamlService(UriInfo uriInfo) {
        this.uriInfo = uriInfo;
    }

    public SamlResponse decryptResponse(String certificate, String samlResponse, String redirectUri) {
        try {
            // user account specific settings. Import the certificate here
            AccountSettings accountSettings = new AccountSettings();
            accountSettings.setCertificate(certificate);
            accountSettings.getIdpCert();

            Response response = new Response(accountSettings, samlResponse, redirectUri);

            return new SamlResponse(response);
        } catch (Exception e) {
            throw new RuntimeException("Unable to decrypt the SAML response: " + e.getMessage(), e);
        }
    }
}
