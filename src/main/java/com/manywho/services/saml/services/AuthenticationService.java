package com.manywho.services.saml.services;

import com.manywho.sdk.api.security.AuthenticatedWhoResult;
import com.manywho.sdk.api.security.LogoutResponse;
import com.manywho.services.saml.entities.SamlLogoutRequestHandler;
import com.manywho.services.saml.entities.SamlResponseHandler;
import com.manywho.services.saml.managers.CacheManager;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;

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

    public AuthenticatedWhoResult createAuthenticatedWhoResult(SamlResponseHandler response) throws Exception {

        if (response.isValid() == false) {
            return createAuthenticatedWhoResultWithError(response.getError());
        }

        String jwtToken;

        try {
            jwtToken = jwtService.sign(response.getNameIdentifier(), response.getResponse().getNotBefore(), response.getResponse().getNotAfter());
            jwtService.validate(jwtToken);
        } catch (Exception e) {
            return createAuthenticatedWhoResultWithError(e.getMessage());
        }

        AuthenticatedWhoResult result = new AuthenticatedWhoResult();

        result.setDirectoryId("SAML");
        result.setDirectoryName("SAML");
        result.setEmail(response.getEmailAddress());
        result.setFirstName(response.getFirstName());
        result.setIdentityProvider(response.getIssuer());

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

    public LogoutResponse createLogoutResponse(SamlLogoutRequestHandler request) {

        LogoutResponse response = new LogoutResponse();

        if(request.isValid() == false) {

            response.setStatus("INVALID_REQUEST"); //todo: not a real status
            response.setErrorMessage(request.getError());
            //response.setCode(); //todo:

        } else {

            response.setStatus("OK");
            response.setUserId(request.getUserId());

            String encodedLogoutResponse = generateSamlLogoutResponse(request);
            response.setCode(encodedLogoutResponse);
         }

        return response;
    }

    private String generateSamlLogoutResponse(SamlLogoutRequestHandler request) {

        try {

            //todo: maybe naming my class LogoutResponse wasn't such a great idea after all...
            com.onelogin.saml2.logout.LogoutResponse samlLogoutResponse = new com.onelogin.saml2.logout.LogoutResponse(request.getSaml2Settings(), null);
            samlLogoutResponse.build();

            if (samlLogoutResponse.isValid() == false) {
                throw new RuntimeException("Created logout response is not valid: " + samlLogoutResponse.getError());
            }

            return samlLogoutResponse.getEncodedLogoutResponse();

        } catch (Throwable t) {
            throw new RuntimeException("Error creating SAML logout response: " + t.getMessage(), t);
        }
    }
}
