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

    @Property("SP Private Key")
    private String privateKey;

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
    private boolean noXmlValidation;

    @Property("Debug")
    private boolean debug;

    @Property("Supported Users")
    private String supportedUsers;

    @Property("Supported Groups")
    private String supportedGroups;

    public String getCertificate() {
        return certificate;
    }

    public String getSpPrivateKey() {
        return privateKey;
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
