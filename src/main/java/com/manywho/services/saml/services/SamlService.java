package com.manywho.services.saml.services;

import com.manywho.services.saml.entities.Configuration;
import com.manywho.services.saml.entities.SamlResponseHandler;
import com.onelogin.AccountSettings;
import com.onelogin.AppSettings;
import com.onelogin.saml.AuthRequest;
import javax.inject.Inject;
import javax.xml.stream.XMLStreamException;
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

    public String generateSamlLoginUrl(com.manywho.services.saml.entities.Configuration configuration) throws IOException, XMLStreamException {

        AppSettings appSettings = new AppSettings();

        // set the URL of the consumer e.g. "http://localhost:22935/api/run/1/saml". The SAML Response
        // will be posted to this URL
        appSettings.setAssertionConsumerServiceUrl(configuration.getAssertionConsumer());

        // set the issuer of the authentication request. This would usually be the URL of the
        // issuing web application
        appSettings.setIssuer(configuration.getIdpEntityId());

        // the accSettings object contains settings specific to the users account.

        // At this point, your application must have identified the users origin
        AccountSettings accSettings = new AccountSettings();

        // The URL at the Identity Provider where to the authentication request should be sent
        accSettings.setIdpSsoTargetUrl(configuration.getLoginUrl());

        // Generate an AuthRequest and send it to the identity provider
        AuthRequest authReq = new AuthRequest(appSettings, accSettings);

        return accSettings.getIdp_sso_target_url()+"?SAMLRequest=" + URLEncoder.encode(authReq.getRequest(1), "UTF-8");
    }
}
