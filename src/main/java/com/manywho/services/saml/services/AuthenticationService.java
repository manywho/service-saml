package com.manywho.services.saml.services;

import com.manywho.sdk.entities.security.AuthenticatedWhoResult;
import com.manywho.sdk.enums.AuthenticationStatus;
import com.manywho.services.saml.entities.SamlResponse;
import com.manywho.services.saml.managers.CacheManager;

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

    public AuthenticatedWhoResult createAuthenticatedWhoResult(SamlResponse response) throws Exception {
        AuthenticatedWhoResult result = new AuthenticatedWhoResult();

        ArrayList<String> listOfGroups = new ArrayList<>();
        listOfGroups.add(response.getGroup());

        result.setDirectoryId("SAML");
        result.setDirectoryName("SAML");
        result.setEmail(response.getEmailAddress());
        result.setFirstName(response.getFirstName());
        result.setIdentityProvider("SAML");
        result.setLastName(response.getLastName());
        result.setStatus(AuthenticationStatus.Authenticated);
        result.setTenantName("SAML");
        result.setToken(jwtService.sign(response.getNameIdentifier()));
        result.setUserId(response.getNameIdentifier());
        result.setUsername(response.getNameIdentifier());

        cacheManager.removeUserGroups(result.getUserId());
        cacheManager.saveUserGroups(result.getUserId(), listOfGroups);

        return result;
    }
}
