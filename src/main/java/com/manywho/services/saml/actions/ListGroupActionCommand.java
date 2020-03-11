package com.manywho.services.saml.actions;

import com.google.inject.Provider;
import com.manywho.sdk.api.InvokeType;
import com.manywho.sdk.api.run.ServiceProblemException;
import com.manywho.sdk.api.run.elements.config.ServiceRequest;
import com.manywho.sdk.api.security.AuthenticatedWho;
import com.manywho.sdk.services.actions.ActionCommand;
import com.manywho.sdk.services.actions.ActionResponse;
import com.manywho.services.saml.entities.ApplicationConfiguration;
import com.manywho.services.saml.services.JwtService;
import javax.inject.Inject;

public class ListGroupActionCommand implements ActionCommand<ApplicationConfiguration, ListGroupAction, ListGroupAction.Input, ListGroupAction.Output> {
    private final Provider<AuthenticatedWho> authenticatedWhoProvider;

    @Inject
    public ListGroupActionCommand(Provider<AuthenticatedWho> authenticatedWhoProvider) {
        this.authenticatedWhoProvider = authenticatedWhoProvider;
    }

    @Override
    public ActionResponse<ListGroupAction.Output> execute(ApplicationConfiguration applicationConfiguration, ServiceRequest serviceRequest, ListGroupAction.Input input) {
        AuthenticatedWho authenticatedWho = authenticatedWhoProvider.get();
        if (applicationConfiguration.getGroupsInRuntime() != true) {
            throw new ServiceProblemException(400, "This action is not supported unless you have configured `Support Groups In Runtime`");
        }

        ListGroupAction.Output output = new ListGroupAction.Output(JwtService.getGroups(authenticatedWho.getToken()));

        return new ActionResponse<>(output, InvokeType.Forward);
    }
}
