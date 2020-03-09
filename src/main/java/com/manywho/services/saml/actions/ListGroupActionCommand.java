package com.manywho.services.saml.actions;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.inject.Provider;
import com.manywho.sdk.api.InvokeType;
import com.manywho.sdk.api.run.elements.config.ServiceRequest;
import com.manywho.sdk.api.security.AuthenticatedWho;
import com.manywho.sdk.services.actions.ActionCommand;
import com.manywho.sdk.services.actions.ActionResponse;
import com.manywho.services.saml.entities.ApplicationConfiguration;
import com.manywho.services.saml.services.JwtService;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;


public class ListGroupActionCommand implements ActionCommand<ApplicationConfiguration, ListGroupAction, ListGroupAction.Input, ListGroupAction.Output> {
    private final Provider<AuthenticatedWho> authenticatedWhoProvider;
    private final JwtService jwtService;

    @Inject
    public ListGroupActionCommand(Provider<AuthenticatedWho> authenticatedWhoProvider, JwtService jwtService) {
        this.authenticatedWhoProvider = authenticatedWhoProvider;
        this.jwtService = jwtService;
    }


    @Override
    public ActionResponse<ListGroupAction.Output> execute(ApplicationConfiguration applicationConfiguration, ServiceRequest serviceRequest, ListGroupAction.Input input) {
        AuthenticatedWho authenticatedWho = authenticatedWhoProvider.get();
        ListGroupAction.Output output = new ListGroupAction.Output(JwtService.getGroups(authenticatedWho.getToken()));

        return new ActionResponse<>(output, InvokeType.Forward);
    }
}
