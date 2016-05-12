package com.manywho.services.saml.controllers;

import com.manywho.sdk.entities.ConfigurationValuesAware;
import com.manywho.sdk.entities.run.elements.type.*;
import com.manywho.sdk.entities.run.elements.type.Object;
import com.manywho.sdk.entities.security.AuthenticatedWhoResult;
import com.manywho.sdk.entities.security.AuthenticationCredentials;
import com.manywho.sdk.services.controllers.AbstractController;
import com.manywho.services.saml.entities.Configuration;
import com.manywho.services.saml.managers.AuthManager;

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

        return new ObjectDataResponse(new ObjectCollection() {{
            add(new Object() {{
                setDeveloperName("GroupAuthorizationGroup");
                setExternalId("user");
                setProperties(new PropertyCollection() {{
                    add(new Property("AuthenticationId", "user"));
                    add(new Property("FriendlyName", "User Group"));
                    add(new Property("DeveloperSummary", "A user without admin privileges"));
                }});
            }});
            add(new Object() {{
                setDeveloperName("GroupAuthorizationGroup");
                setExternalId("admin");
                setProperties(new PropertyCollection() {{
                    add(new Property("AuthenticationId", "admin"));
                    add(new Property("FriendlyName", "Admin Group"));
                    add(new Property("DeveloperSummary", "A user with admin privileges"));
                }});
            }});
        }});
    }

    @Path("/authorization/group/attribute")
    @POST
    public ObjectDataResponse groupAttributes(ObjectDataRequest objectDataRequest) throws Exception {
        return new ObjectDataResponse(new ObjectCollection() {{
            add(new Object() {{
                setDeveloperName("AuthenticationAttribute");
                setProperties(new PropertyCollection() {{
                    add(new Property("Label", "Members"));
                    add(new Property("Value", "MEMBERS"));
                }});
            }});
        }});
    }

    @Path("/authorization/user")
    @POST
    public ObjectDataResponse users(ObjectDataRequest objectDataRequest) throws Exception {
        return new ObjectDataResponse();
    }

    @Path("/authorization/user/attribute")
    @POST
    public ObjectDataResponse userAttributes(ObjectDataRequest objectDataRequest) throws Exception {
        return new ObjectDataResponse();
    }

    private Configuration getConfigurationValues(ConfigurationValuesAware configurationValuesAware) throws Exception {
        return parseConfigurationValues(configurationValuesAware, Configuration.class);
    }
}
