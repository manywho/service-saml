package com.manywho.services.saml.controllers;

import com.manywho.sdk.entities.ConfigurationValuesAware;
import com.manywho.sdk.entities.run.elements.type.*;
import com.manywho.sdk.entities.run.elements.type.Object;
import com.manywho.sdk.entities.security.AuthenticatedWhoResult;
import com.manywho.sdk.entities.security.AuthenticationCredentials;
import com.manywho.sdk.services.controllers.AbstractController;
import com.manywho.services.saml.entities.Configuration;
import com.manywho.services.saml.entities.UserAllowed;
import com.manywho.services.saml.managers.AuthManager;
import com.manywho.services.saml.utils.RestrictionsUtils;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/")
@Consumes("application/json")
@Produces("application/json")
public class AuthController extends AbstractController {
    private final AuthManager authManager;

    @Inject
    public AuthController(AuthManager authManager) {
        this.authManager = authManager;
    }

    @Path("/authentication")
    @POST
    public AuthenticatedWhoResult authentication(AuthenticationCredentials authenticationCredentials) throws Exception {
        return authManager.authentication(getConfigurationValues(authenticationCredentials), authenticationCredentials);
    }

    @Path("/authorization")
    @POST
    public ObjectDataResponse authorization(ObjectDataRequest objectDataRequest) throws Exception {
        return authManager.authorization(getConfigurationValues(objectDataRequest), objectDataRequest, getAuthenticatedWho());
    }

    @Path("/authorization/group")
    @POST
    public ObjectDataResponse groups(ObjectDataRequest objectDataRequest) throws Exception {
        Configuration configuration = getConfigurationValues(objectDataRequest);
        ObjectCollection objectCollection = new ObjectCollection();

        for (String name : RestrictionsUtils.listOfGroups(configuration.getSupportedGroups())) {
            Object object = authorizationRestriction("GroupAuthorizationGroup",name, name);
            objectCollection.add(object);
        }

        return new ObjectDataResponse(objectCollection);
    }

    @Path("/authorization/group/attribute")
    @POST
    public ObjectDataResponse groupAttributes(ObjectDataRequest objectDataRequest) throws Exception {
        ObjectCollection objectCollection = new ObjectCollection();
        PropertyCollection properties = new PropertyCollection();
        properties.add(new Property("Label", "Members"));
        properties.add(new Property("Value", "MEMBERS"));

        Object object = new Object();
        object.setDeveloperName("AuthenticationAttribute");
        object.setExternalId("MEMBERS");
        object.setProperties(properties);
        objectCollection.add(object);

        return new ObjectDataResponse(objectCollection);
    }

    @Path("/authorization/user")
    @POST
    public ObjectDataResponse users(ObjectDataRequest objectDataRequest) throws Exception {
        Configuration configuration = getConfigurationValues(objectDataRequest);
        ObjectCollection objectCollection = new ObjectCollection();

        for (UserAllowed userRestriction : RestrictionsUtils.listOfUsers(configuration.getSupportedUsers())) {
            Object object = authorizationRestriction("GroupAuthorizationUser", userRestriction.getId(), userRestriction.getFriendlyName());
            objectCollection.add(object);
        }

        return new ObjectDataResponse(objectCollection);
    }

    @Path("/authorization/user/attribute")
    @POST
    public ObjectDataResponse userAttributes(ObjectDataRequest objectDataRequest) throws Exception {
        ObjectCollection objectCollection = new ObjectCollection();

        PropertyCollection properties = new PropertyCollection();
        properties.add(new Property("Label", "Account ID"));
        properties.add(new Property("Value", "accountId"));

        Object object = new Object();
        object.setDeveloperName("AuthenticationAttribute");
        object.setExternalId("accountID");
        object.setProperties(properties);
        objectCollection.add(object);

        return new ObjectDataResponse(objectCollection);
    }

    private Object authorizationRestriction(String developerName, String name, String friendlyName) {
        Object object = new Object();
        object.setDeveloperName(developerName);
        object.setExternalId(name);

        PropertyCollection properties = new PropertyCollection();
        properties.add(new Property("AuthenticationId", name));
        properties.add(new Property("FriendlyName", friendlyName));
        properties.add(new Property("DeveloperSummary", friendlyName));
        object.setProperties(properties);
        return object;
    }

    private Configuration getConfigurationValues(ConfigurationValuesAware configurationValuesAware) throws Exception {
        return parseConfigurationValues(configurationValuesAware, Configuration.class);
    }
}
