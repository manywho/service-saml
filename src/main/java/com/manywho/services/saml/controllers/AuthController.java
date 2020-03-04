package com.manywho.services.saml.controllers;

import com.google.inject.Provider;
import com.manywho.sdk.api.run.elements.type.ObjectDataRequest;
import com.manywho.sdk.api.run.elements.type.ObjectDataResponse;
import com.manywho.sdk.api.security.*;
import com.manywho.sdk.services.configuration.ConfigurationParser;
import com.manywho.sdk.services.controllers.AbstractAuthenticationController;
import com.manywho.sdk.services.types.TypeBuilder;
import com.manywho.sdk.services.types.system.AuthorizationAttribute;
import com.manywho.sdk.services.types.system.AuthorizationGroup;
import com.manywho.sdk.services.types.system.AuthorizationUser;
import com.manywho.services.saml.entities.ApplicationConfiguration;
import com.manywho.services.saml.entities.UserAllowed;
import com.manywho.services.saml.managers.AuthManager;
import com.manywho.services.saml.utils.RestrictionsUtils;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.ArrayList;
import java.util.List;

@Path("/")
@Consumes("application/json")
@Produces("application/json")
public class AuthController extends AbstractAuthenticationController {
    private final AuthManager authManager;
    private final TypeBuilder typeBuilder;
    private final ConfigurationParser configurationParser;
    private final Provider<AuthenticatedWho> authenticatedWhoProvider;

    @Inject
    public AuthController(AuthManager authManager, TypeBuilder typeBuilder, ConfigurationParser configurationParser, Provider<AuthenticatedWho> authenticatedWhoProvider) {
        this.authManager = authManager;
        this.typeBuilder = typeBuilder;
        this.configurationParser = configurationParser;
        this.authenticatedWhoProvider = authenticatedWhoProvider;
    }

    @Path("/authentication")
    @POST
    public AuthenticatedWhoResult authentication(AuthenticationCredentials authenticationCredentials) throws Exception {
        return authManager.authentication(configurationParser.from(authenticationCredentials), authenticationCredentials);
    }

    @Path("/logout")
    @POST
    public LogoutResponse logout(LogoutRequest logoutRequest) throws Exception {
        return authManager.logout(configurationParser.from(logoutRequest), logoutRequest);
    }

    @Path("/authorization")
    @POST
    public ObjectDataResponse authorization(ObjectDataRequest objectDataRequest) throws Exception {
        AuthenticatedWho authenticatedWho = authenticatedWhoProvider.get();
        return authManager.authorization(configurationParser.from(objectDataRequest), objectDataRequest, authenticatedWho);
    }

    @Path("/authorization/group")
    @POST
    public ObjectDataResponse groups(ObjectDataRequest objectDataRequest) throws Exception {
        ApplicationConfiguration configuration = configurationParser.from(objectDataRequest);
        List<AuthorizationGroup> groups = new ArrayList<>();

        for (String name : RestrictionsUtils.listOfGroups(configuration.getSupportedGroups())) {
            AuthorizationGroup group = new AuthorizationGroup(name, name, name);
            groups.add(group);
        }

        return new ObjectDataResponse(typeBuilder.from(groups));
    }

    @Path("/authorization/group/attribute")
    @POST
    public ObjectDataResponse groupAttributes(ObjectDataRequest objectDataRequest) throws Exception {
        return new ObjectDataResponse(
                typeBuilder.from(new AuthorizationAttribute("MEMBERS", "Members"))
        );
    }

    @Path("/authorization/user")
    @POST
    public ObjectDataResponse users(ObjectDataRequest objectDataRequest) throws Exception {
        ApplicationConfiguration configuration = configurationParser.from(objectDataRequest);
        List<AuthorizationUser> users = new ArrayList<>();

        for (UserAllowed userRestriction : RestrictionsUtils.listOfUsers(configuration.getSupportedUsers())) {
            AuthorizationUser user = new AuthorizationUser(userRestriction.getId(), userRestriction.getFriendlyName());
            users.add(user);
        }

        return new ObjectDataResponse(typeBuilder.from(users));
    }

    @Path("/authorization/user/attribute")
    @POST
    public ObjectDataResponse userAttributes(ObjectDataRequest objectDataRequest) throws Exception {
        return new ObjectDataResponse(
                typeBuilder.from(new AuthorizationAttribute("accountId", "Account ID"))
        );
    }
}
