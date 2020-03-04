package com.manywho.services.saml.entities;

import com.manywho.services.saml.adapters.ManyWhoSaml2Settings;
import com.manywho.services.saml.adapters.ManyWhoSamlResponse;
import com.onelogin.saml2.exception.ValidationError;
import javax.xml.xpath.XPathExpressionException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SamlResponseHandler {
    private ManyWhoSamlResponse response;
    private static final String GROUPS_NAMESPACE = "http://schemas.microsoft.com/ws/2008/06/identity/claims/groups";
    private HashMap<String, List<String>> attributes;

    public SamlResponseHandler(ApplicationConfiguration configuration, String samlResponse, String currentURL) {
        ManyWhoSaml2Settings manyWhoSaml2Settings = new ManyWhoSaml2Settings(configuration);
        try {
            this.response = new ManyWhoSamlResponse(manyWhoSaml2Settings, samlResponse, currentURL);
            this.attributes = response.getAttributes();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public ManyWhoSamlResponse getResponse() {
        return response;
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

    public ArrayList<String> getGroups() {

        if (!attributes.isEmpty() && attributes.get(GROUPS_NAMESPACE) != null) {

            return listToArrayList(attributes.get(GROUPS_NAMESPACE));
        } else {

            return new ArrayList<>();
        }
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

    private ArrayList<String> listToArrayList (List<String> list) {
        ArrayList<String> arrayList = new ArrayList<>();
        list.forEach(arrayList::add);
        return arrayList;
    }

    public String getIssuer() {

        try {
            return response.getIssuers().get(0);

        } catch (Exception e) {
            throw new RuntimeException("Error reading the Issuer information", e);
        }
    }

    public boolean isValid() {
        return response.isValid();
    }
}
