package com.manywho.services.saml.entities;

import com.manywho.sdk.services.annotations.Property;
import com.manywho.services.saml.validators.Certificate;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

public class Configuration {
    @NotBlank(message = "A certificate must be provided")
    @Certificate
    @Property("Certificate")
    private String certificate;

    @NotBlank(message = "A valid login URL must be provided")
    @URL(message = "A valid login URL must be provided")
    @Property("Login URL")
    private String loginUrl;

    @Property("SAMLRequest")
    private String SamlRequest;

    public String getCertificate() {
        return certificate;
    }

    public String getLoginUrl() {
        return loginUrl;
    }

    public String getSamlRequest() {
        return SamlRequest;
    }
}
