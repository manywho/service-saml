package com.manywho.services.saml.adapters;

import com.manywho.services.saml.entities.Configuration;
import com.onelogin.saml2.settings.Saml2Settings;
import org.apache.commons.lang3.StringUtils;
import org.apache.xml.security.exceptions.Base64DecodingException;
import org.apache.xml.security.utils.Base64;
import java.io.ByteArrayInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public class ManyWhoSaml2Settings extends Saml2Settings {

    public ManyWhoSaml2Settings(Configuration configuration) {
        super();

        this.setCompressRequest(false);

        try {
            this.setIdpSingleSignOnServiceUrl(new URL(configuration.getLoginUrl()));
        } catch (MalformedURLException e) {
            throw new RuntimeException("The login url is not valid");
        }

        if (configuration.getNoXmlValidation()) {
            this.setWantXMLValidation(false);
        }

        if (!StringUtils.isEmpty(configuration.getIdpEntityId())) {
            this.setIdpEntityId(configuration.getIdpEntityId());
        }

        if (!StringUtils.isEmpty(configuration.getSpEntityId())) {
            this.setSpEntityId(configuration.getSpEntityId());
        }

        if (!StringUtils.isEmpty(configuration.getAssertionConsumer())) {
            try {
                URL url = new URL(configuration.getAssertionConsumer());
                this.setSpAssertionConsumerServiceUrl(url);
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }

        if (configuration.getDebug()) {
            this.setDebug(true);
        }

        try {
            this.setIdpx509cert(parseCertificate(configuration.getCertificate()));
        } catch (CertificateException | Base64DecodingException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    public static X509Certificate parseCertificate(String certificate) throws CertificateException, Base64DecodingException {
        String certificateBody = certificate.replaceAll("-----BEGIN CERTIFICATE-----", "")
                .replaceAll("-----END CERTIFICATE-----", "");
        byte[] decoded = Base64.decode(certificateBody);
        ByteArrayInputStream is = new ByteArrayInputStream(decoded);

        return (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(is);
    }
}
