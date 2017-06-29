package com.manywho.services.saml.controllers;

import com.manywho.sdk.entities.ConfigurationValuesAware;
import com.manywho.sdk.entities.run.elements.type.*;
import com.manywho.sdk.entities.run.elements.type.Object;
import com.manywho.sdk.entities.security.AuthenticatedWhoResult;
import com.manywho.sdk.entities.security.AuthenticationCredentials;
import com.manywho.sdk.services.controllers.AbstractController;
import com.manywho.services.saml.entities.Configuration;
import com.manywho.services.saml.managers.AuthManager;
import org.apache.commons.lang3.StringUtils;

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

        if (!StringUtils.isEmpty(configuration.getSupportedGroups())) {
            String[] stringName = configuration.getSupportedGroups().split(";");

            for (String name : stringName) {
                Object object = authorizationRestriction("GroupAuthorizationGroup",name, name);
                objectCollection.add(object);
            }
        }
        return new ObjectDataResponse(objectCollection);
    }

    @Path("/authorization/group/attribute")
    @POST
    public ObjectDataResponse groupAttributes(ObjectDataRequest objectDataRequest) throws Exception {
        ObjectCollection objectCollection = new ObjectCollection();
        PropertyCollection properties = new PropertyCollection();
        properties.add(new Property("Label", "Users"));
        properties.add(new Property("Value", "users"));

        Object object = new Object();
        object.setDeveloperName("AuthenticationAttribute");
        object.setExternalId("users");
        object.setProperties(properties);
        objectCollection.add(object);

        return new ObjectDataResponse(objectCollection);
    }

    @Path("/authorization/user")
    @POST
    public ObjectDataResponse users(ObjectDataRequest objectDataRequest) throws Exception {
        Configuration configuration = getConfigurationValues(objectDataRequest);

        ObjectCollection objectCollection = new ObjectCollection();

        if (!StringUtils.isEmpty(configuration.getSupportedUsers())) {
            String[] stringName = configuration.getSupportedUsers().split(";");

            for (String userNameAndId : stringName) {
                String[] userParts = userNameAndId.split(",");
                if (userParts.length != 2) {
                    throw new RuntimeException("Error in the format for Supported Users.");
                }

                Object object = authorizationRestriction("GroupAuthorizationUser",userParts[1], userParts[0]);
                objectCollection.add(object);
            }
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
