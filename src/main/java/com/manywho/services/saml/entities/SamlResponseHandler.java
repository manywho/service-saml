package com.manywho.services.saml.entities;

import com.manywho.services.saml.services.ManyWhoSaml2Settings;
import com.manywho.services.saml.services.ManyWhoSamlResponse;
import com.onelogin.saml2.exception.ValidationError;
import javax.xml.xpath.XPathExpressionException;
import java.util.List;

public class SamlResponseHandler {
    private ManyWhoSamlResponse response;

    public SamlResponseHandler(Configuration configuration, String samlResponse, String currentURL) {
        ManyWhoSaml2Settings manyWhoSaml2Settings = new ManyWhoSaml2Settings(configuration);
        try {
            this.response = new ManyWhoSamlResponse(manyWhoSaml2Settings, samlResponse, currentURL);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    public String getEmailAddress() {
        return this.getAttribute("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/emailaddress");
    }

    public String getError()
    {
        return this.response.getError();
    }

    public String getFirstName() {
        return this.getAttribute("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/name");
    }

    public String getLastName() {
        return this.getAttribute("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/surname");
    }

    public String getGroup() {
        return this.getAttribute("http://manywho.com/saml/group");
    }

    public String getNameIdentifier() throws Exception {
        return this.response.getNameId();
    }

    private String getAttribute(String key) {
        try {
            List<String> attributes = response.getAttributes().get(key);
            if (attributes != null && attributes.size()>0) {
                return attributes.get(0);
            } else {
                return "";
            }

        } catch (XPathExpressionException | ValidationError e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    public boolean isValid() {
        return response.isValid();
    }
}
