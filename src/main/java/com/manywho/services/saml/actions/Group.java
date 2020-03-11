package com.manywho.services.saml.actions;

import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.services.types.Type;

@Type.Element(name = "Group", summary = "SAML Claim Group")
public class Group implements Type {
    @Type.Property(name = "Name", contentType = ContentType.String, bound = false)
    private String name;

    public Group(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
