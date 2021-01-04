package com.manywho.services.saml.entities;

import com.manywho.services.saml.adapters.ManyWhoSaml2Settings;
import com.manywho.services.saml.adapters.ManyWhoSamlResponse;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class SamlResponseHandler {
    private ManyWhoSamlResponse response;
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

    public String getError()
    {
        return this.response.getError();
    }

    public String getEmailAddress() {
        return this.getAttribute(
            Arrays.asList(
                "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/emailaddress",
                "email",
                "emailaddress",
                "email_address",
                "mail"
            )
        );
    }

    public String getFirstName() {
        return this.getAttribute(
            Arrays.asList(
                "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/name",
                "name",
                "firstname",
                "first_name",
                "first name",
                "givenname",
                "given_name",
                "given name"
            )
        );
    }

    public String getLastName() {
        return this.getAttribute(
            Arrays.asList(
                "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/surname",
                "surname",
                "sur_name",
                "lastname",
                "last_name",
                "last name"
            )
        );
    }

    public String getPrimaryGroupId() {
        return this.getAttribute(
            Arrays.asList(
                "http://schemas.manywho.com/2020/identity/claims/primarygroupid",
                "primarygroupid",
                "primary_group_id",
                "primary group id"
            )
        );
    }

    public String getPrimaryGroupName() {
        return this.getAttribute(
            Arrays.asList(
                "http://schemas.manywho.com/2020/identity/claims/primarygroupname",
                "primarygroupname",
                "primary_group_name",
                "primary group name"
            )
        );
    }

    public ArrayList<String> getGroups() {
        List<String> groups = this.getAttributes(
            Arrays.asList(
                "http://schemas.microsoft.com/ws/2008/06/identity/claims/groups",
                "groups"
            )
        );

        if (groups != null) {
            return listToArrayList(groups);
        } else {
            return new ArrayList<>();
        }
    }

    public String getNameIdentifier() throws Exception {
        return this.response.getNameId();
    }

    private List<String> getAttributes(String key) {
        List<String> attributes = null;

        Optional<String> attributeName = this.attributes
            .keySet()
            .stream()
            .filter(x -> x.equalsIgnoreCase(key))
            .findFirst();

        if (attributeName.isPresent()) {
            attributes = this.attributes.get(attributeName.get());
        }

        return attributes;
    }

    private List<String> getAttributes(List<String> keys) {
        for (int i = 0; i < keys.size(); i++) {
            List<String> attributes = this.getAttributes(keys.get(i));
            if (attributes != null) {
                return attributes;
            }
        }

        return new ArrayList<String>();
    }

    private String getAttribute(String key) {
        List<String> attributes = this.getAttributes(key);

        if (attributes != null && attributes.size()>0) {
            return attributes.get(0);
        } else {
            return "";
        }
    }

    private String getAttribute(List<String> keys) {
        for (int i = 0; i < keys.size(); i++) {
            String attribute = this.getAttribute(keys.get(i));
            if (StringUtils.isNotEmpty(attribute)) {
                return attribute;
            }
        }

        return "";
    }

    private ArrayList<String> listToArrayList (List<String> list) {
        ArrayList<String> arrayList = new ArrayList<>();
        list.forEach(arrayList::add);
        return arrayList;
    }

    public boolean isValid() {
        return response.isValid();
    }
}
