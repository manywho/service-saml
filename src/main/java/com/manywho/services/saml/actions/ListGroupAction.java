package com.manywho.services.saml.actions;

import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.services.actions.Action;
import java.util.List;

@Action.Metadata(name = "Get Groups", summary = "Get Groups from the Identity Provider Assertion", uri = "get-groups")
public class ListGroupAction implements Action {
    public static class Input {
    }

    public static class Output {
        @Action.Output(name = "Groups", contentType = ContentType.List)
        private List<Group> groups;

        public Output(List<Group> groups) {
            this.groups = groups;
        }

        public List<Group> getGroups() {
            return groups;
        }
    }
}
