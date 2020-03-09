package com.manywho.services.saml.managers;

import com.manywho.sdk.api.run.elements.type.ObjectDataRequest;
import com.manywho.sdk.api.run.elements.type.ObjectDataResponse;
import com.manywho.sdk.api.security.AuthenticatedWho;
import com.manywho.sdk.api.security.AuthenticatedWhoResult;
import com.manywho.sdk.api.security.AuthenticationCredentials;
import com.manywho.sdk.services.types.TypeBuilder;
import com.manywho.sdk.services.types.system.$User;
import com.manywho.services.saml.entities.ApplicationConfiguration;
import com.manywho.services.saml.entities.SamlResponseHandler;
import com.manywho.services.saml.services.AuthenticationService;
import com.manywho.services.saml.services.AuthorizationService;
import com.manywho.services.saml.services.SamlService;

import javax.inject.Inject;

public class AuthManager {
    private final SamlService samlService;
    private final AuthenticationService authenticationService;
    private final AuthorizationService authorizationService;
    private final TypeBuilder typeBuilder;

    @Inject
    public AuthManager(SamlService samlService, AuthenticationService authenticationService, AuthorizationService authorizationService, TypeBuilder typeBuilder) {
        this.samlService = samlService;
        this.authenticationService = authenticationService;
        this.authorizationService = authorizationService;
        this.typeBuilder = typeBuilder;
    }

    /**
     * Decrypt the incoming SAML response using the certificate provided in the Configuration Values, and create an
     * AuthenticatedWhoResult from the included attributes.
     *
     * @param configuration       The provided configuration values for the Flow
     * @param authenticationCredentials The credentials to decrypt
     * @return information about the authenticated user
     */
    public AuthenticatedWhoResult authentication(ApplicationConfiguration configuration, AuthenticationCredentials authenticationCredentials) throws Exception {
        SamlResponseHandler response = samlService.decryptResponse(configuration, authenticationCredentials.getCode(), authenticationCredentials.getRedirectUri());

        return authenticationService.createAuthenticatedWhoResult(configuration, response);
    }

    /**
     * Check that the user is authorized (currently only checks if the user is PUBLIC_USER or not, so if the user was
     * authenticated, then assume it is always authorized)
     *
     * @param configuration The provided configuration values for the Flow
     * @param authenticatedWho    The currently authenticated user
     * @return a UserObject that says whether the authenticated user is authorized
     */
    public ObjectDataResponse authorization(ApplicationConfiguration configuration, ObjectDataRequest objectDataRequest, AuthenticatedWho authenticatedWho) throws Exception {
        String status = authorizationService.getStatus(objectDataRequest.getAuthorization(), authenticatedWho);
        String loginUrl = "";

        if (authorizationService.shouldSendLoginUrl(objectDataRequest.getAuthorization(), authenticatedWho, status)) {
            loginUrl = samlService.generateSamlLoginUrl(configuration);
        }

        $User user = authorizationService.createUserObject(authenticatedWho, loginUrl, status);

        return new ObjectDataResponse(typeBuilder.from(user));
    }
}
