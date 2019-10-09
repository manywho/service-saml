package com.manywho.services.saml.adapters;

import com.manywho.services.saml.entities.Configuration;
import com.onelogin.saml2.settings.Saml2Settings;
import org.apache.commons.lang3.StringUtils;
import org.apache.xml.security.exceptions.Base64DecodingException;
import org.apache.xml.security.utils.Base64;
import java.io.ByteArrayInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

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

        if (!StringUtils.isEmpty(configuration.getSpPrivateKey())) {
            try {
                this.setSpPrivateKey(parsePrivateKey(configuration.getSpPrivateKey()));
            } catch (Base64DecodingException | InvalidKeySpecException | NoSuchAlgorithmException e) {
                e.printStackTrace();
                throw new RuntimeException("Error parsing Service Provider Private Key", e);
            }
        }
    }

    public static X509Certificate parseCertificate(String certificate) throws CertificateException, Base64DecodingException {
        String certificateBody = certificate.replaceAll("-----BEGIN CERTIFICATE-----", "")
                .replaceAll("-----END CERTIFICATE-----", "");
        byte[] decoded = Base64.decode(certificateBody);
        ByteArrayInputStream is = new ByteArrayInputStream(decoded);

        return (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(is);
    }

    public static PrivateKey parsePrivateKey(String privateKey) throws Base64DecodingException, InvalidKeySpecException, NoSuchAlgorithmException {
        String privateKeyBody = privateKey
            .replaceAll("-----BEGIN PRIVATE KEY-----", "")
            .replaceAll("-----END PRIVATE KEY-----", "");

        byte[] decoded = Base64.decode(privateKeyBody);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        try {
            return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(decoded));
        }
        catch (InvalidKeySpecException e) {
            throw new RuntimeException("There was an error decoding the Private Key (only RSA is currently supported", e);
        }

    }
}
