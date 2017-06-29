package com.manywho.services.saml.entities;

public class UserAllowed {
    private String id;
    private String friendlyName;

    public UserAllowed(String id, String friendlyName) {
        this.id = id;
        this.friendlyName = friendlyName;
    }

    public String getId() {
        return id;
    }

    public String getFriendlyName() {
        return friendlyName;
    }
}
