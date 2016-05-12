package com.manywho.services.saml.entities;

import com.onelogin.saml.Response;
import java.util.ArrayList;

public class SamlResponse {
    private final Response response;

    public SamlResponse(Response response) {
        this.response = response;
    }

    public String getEmailAddress() {
        return getFirstItem(response.getAttribute("http://manywho.com/saml/emailaddress"));
    }

    public String getError() {
        return response.getError();
    }

    public String getFirstName() {
        return getFirstItem(response.getAttribute("http://manywho.com/saml/firstname"));
    }

    public String getLastName() {
        return getFirstItem(response.getAttribute("http://manywho.com/saml/lastname"));
    }

    public ArrayList<String> getGroup() {
        return response.getAttribute("http://manywho.com/saml/group");
    }

    public String getNameIdentifier() throws Exception {
        return response.getNameId();
    }

    public boolean isValid() {
        return response.isValid();
    }

    private String getFirstItem(ArrayList<String> arrayList) {
        if (arrayList!= null && arrayList.size()>0) {
            return arrayList.get(0);
        }

        return null;
    }
}
