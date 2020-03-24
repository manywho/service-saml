package com.manywho.services.saml.services;

import com.manywho.sdk.api.security.AuthenticatedWhoResult;
import com.manywho.services.saml.entities.ApplicationConfiguration;
import com.manywho.services.saml.entities.SamlResponseHandler;
import com.manywho.services.saml.managers.CacheManager;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import java.util.ArrayList;

public class AuthenticationService {
    private final JwtService jwtService;
    private CacheManager cacheManager;

    @Inject
    public AuthenticationService(JwtService jwtService, CacheManager cacheManager) {
        this.jwtService = jwtService;
        this.cacheManager = cacheManager;
    }

    public AuthenticatedWhoResult createAuthenticatedWhoResultWithError(String error) {
        AuthenticatedWhoResult result = AuthenticatedWhoResult.createDeniedResult();
        result.setStatusMessage("Unable to authenticate with the identity provider: " + error);

        return result;
    }

    public AuthenticatedWhoResult createAuthenticatedWhoResult(ApplicationConfiguration configuration, SamlResponseHandler response) throws Exception {

        if (response.isValid() == false) {
            return createAuthenticatedWhoResultWithError(response.getError());
        }

        String jwtToken;

        try {
            ArrayList<String> groups = response.getGroups();


            jwtToken = jwtService.sign(response.getNameIdentifier(), response.getResponse().getNotBefore(), response.getResponse().getNotAfter(), groups);
            jwtService.validate(jwtToken);
        } catch (Exception e) {
            return createAuthenticatedWhoResultWithError(e.getMessage());
        }

        AuthenticatedWhoResult result = new AuthenticatedWhoResult();

        result.setDirectoryId("SAML");
        result.setDirectoryName("SAML");
        result.setEmail(response.getEmailAddress());
        result.setFirstName(response.getFirstName());
        result.setIdentityProvider("SAML");

        if (!StringUtils.isEmpty(response.getLastName())) {
            result.setLastName(response.getLastName());
        } else {
            result.setLastName(response.getFirstName());
        }

        result.setStatus(AuthenticatedWhoResult.AuthenticationStatus.Authenticated);
        result.setTenantName("SAML");
        result.setToken(jwtToken);
        result.setUserId(response.getNameIdentifier());
        result.setUsername(response.getNameIdentifier());

        cacheManager.removeUserGroups(result.getUserId());

        if (!response.getGroups().isEmpty()) {
            cacheManager.saveUserGroups(result.getUserId(), response.getGroups());
        }

        return result;
    }
}
