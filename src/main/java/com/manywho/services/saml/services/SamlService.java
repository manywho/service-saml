package com.manywho.services.saml.services;

import com.manywho.services.saml.adapters.ManyWhoSaml2Settings;
import com.manywho.services.saml.entities.Configuration;
import com.manywho.services.saml.entities.SamlResponseHandler;
import com.onelogin.saml2.authn.*;
import com.onelogin.saml2.settings.Saml2Settings;
import org.apache.http.client.utils.URIBuilder;
import javax.inject.Inject;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
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

    public String generateSamlLoginUrl(Configuration configuration) throws IOException, URISyntaxException {
        Saml2Settings appSettings = new ManyWhoSaml2Settings(configuration);

        AuthnRequest authReq = new AuthnRequest(appSettings, false, false, false);

        URI uriRedirect = new URIBuilder(configuration.getLoginUrl())
                .addParameter("SAMLRequest", URLEncoder.encode(authReq.getEncodedAuthnRequest(), "UTF-8")).build();

        return uriRedirect.toString();
    }
}
