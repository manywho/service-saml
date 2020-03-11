package com.manywho.services.saml.entities;

import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.services.configuration.Configuration;
import com.manywho.services.saml.validators.Certificate;

public class ApplicationConfiguration  implements Configuration {
    @Certificate
    @Configuration.Setting(name ="Certificate", contentType = ContentType.Encrypted)
    private String certificate;

    @Configuration.Setting(name ="SP Private Key", contentType = ContentType.Encrypted, required = false)
    private String privateKey;

    @Configuration.Setting(name ="Login URL", contentType = ContentType.String)
    private String loginUrl;

    @Configuration.Setting(name ="Identifier of the IdP entity (URI)", contentType = ContentType.String, required = false)
    private String idpEntityId;

    @Configuration.Setting(name ="Identifier of the SP entity (URI)", contentType = ContentType.String, required = false)
    private String spEntityId;

    @Configuration.Setting(name ="Assertion Consumer Service (URL)", contentType = ContentType.String)
    private String assertionConsumer;

    @Configuration.Setting(name ="No XML Validation", contentType = ContentType.Boolean, required = false)
    private boolean noXmlValidation;

    @Configuration.Setting(name ="Debug", contentType = ContentType.Boolean, required = false)
    private boolean debug;

    @Configuration.Setting(name ="Supported Users", contentType = ContentType.String, required = false)
    private String supportedUsers;

    @Configuration.Setting(name ="Supported Groups", contentType = ContentType.String, required = false)
    private String supportedGroups;

    @Configuration.Setting(name ="Compress Request", contentType = ContentType.Boolean, required = false)
    private boolean compressRequest;

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

    public boolean getCompressRequest() {
        return compressRequest;
    }
}
