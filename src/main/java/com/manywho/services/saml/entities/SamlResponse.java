package com.manywho.services.saml.entities;

import com.onelogin.saml.Response;

public class SamlResponse {
    private final Response response;

    public SamlResponse(Response response) {
        this.response = response;
    }

    public String getEmailAddress() {
        return response.getAttribute("http://manywho.com/saml/emailaddress");
    }

    public String getError() {
        return response.getError();
    }

    public String getFirstName() {
        return response.getAttribute("http://manywho.com/saml/firstname");
    }

    public String getLastName() {
        return response.getAttribute("http://manywho.com/saml/lastname");
    }

    public String getGroup() {
        return response.getAttribute("http://manywho.com/saml/group");
    }

    public String getNameIdentifier() throws Exception {
        return response.getNameId();
    }

    public boolean isValid() {
        return response.isValid();
    }
}
