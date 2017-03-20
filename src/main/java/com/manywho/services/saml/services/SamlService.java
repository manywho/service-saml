package com.manywho.services.saml.services;

import com.auth0.jwt.internal.org.apache.commons.codec.binary.Base64;
import com.manywho.services.saml.entities.Configuration;
import com.manywho.services.saml.entities.SamlResponse;
import com.onelogin.AccountSettings;
import com.onelogin.saml.Response;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import javax.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

public class SamlService {

    @Inject
    public SamlService() {}

    public SamlResponse decryptResponse(String certificate, String samlResponse, String redirectUri) {
        try {
            // user account specific settings. Import the certificate here
            AccountSettings accountSettings = new AccountSettings();
            accountSettings.setCertificate(certificate);
            accountSettings.getIdpCert();

            Response response = new Response(accountSettings, samlResponse, redirectUri);

            return new SamlResponse(response);
        } catch (Exception e) {
            throw new RuntimeException("Unable to decrypt the SAML response: " + e.getMessage(), e);
        }
    }

    public String generateSamlLoginUrl(Configuration configuration) throws IOException {
        if (StringUtils.isEmpty(configuration.getSamlRequest())) {
            return configuration.getLoginUrl();
        }
        String replaceCurrentTimestamp = overwriteIssueInstant(configuration.getSamlRequest());

        return String.format("%s?SAMLRequest=%s", configuration.getLoginUrl(), encodedSamlRequest(replaceCurrentTimestamp));
    }

    private String encodedSamlRequest(String samlXML ) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Deflater deflater = new Deflater( Deflater.DEFAULT_COMPRESSION, true );
        DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(outputStream, deflater);
        deflaterOutputStream.write(samlXML.getBytes("UTF-8"));
        deflaterOutputStream.close();
        outputStream.close();
        String base64 = Base64.encodeBase64String(outputStream.toByteArray());

        return URLEncoder.encode( base64, "UTF-8" );
    }

    private String overwriteIssueInstant(String original){
        DateTime dateTime = new DateTime();
        DateTimeFormatter formatterDateTime = DateTimeFormat.forPattern("yyyy - MM - dd'T'HH:mm:ss");

        return original.replace("CURRENT_TIMESTAMP", formatterDateTime.print(dateTime));
    }
}
