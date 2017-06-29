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

    @Property("Identifier of the IdP entity (URI)")
    private String idpEntityId;

    @Property("Identifier of the SP entity (URI)")
    private String spEntityId;

    @Property("Assertion Consumer Service (URL)")
    private String assertionConsumer;

    @Property("No XML Validation")
    private Boolean noXmlValidation;

    @Property("No Strict Validation")
    private Boolean noStrictValidation;

    @Property("Debug")
    private Boolean debug;

    @Property("Supported Users")
    private String supportedUsers;

    @Property("Supported Groups")
    private String supportedGroups;

    public String getCertificate() {
        return certificate;
    }

    public String getLoginUrl() {
        return loginUrl;
    }

    public String getIdpEntityId() {
        return idpEntityId;
    }

    public String getSpEntityId() {
        return spEntityId;
    }

    public String getAssertionConsumer() {
        return assertionConsumer;
    }

    public Boolean getNoXmlValidation() {
        return noXmlValidation;
    }

    public Boolean getNoStrictValidation() {
        return noStrictValidation;
    }

    public Boolean getDebug() {
        return debug;
    }

    public String getSupportedUsers() {
        return supportedUsers;
    }

    public String getSupportedGroups() {
        return supportedGroups;
    }
}
