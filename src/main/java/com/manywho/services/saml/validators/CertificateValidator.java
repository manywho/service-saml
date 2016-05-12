package com.manywho.services.saml.validators;

import org.apache.commons.codec.binary.Base64;
import sun.security.provider.X509Factory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.io.ByteArrayInputStream;
import java.security.cert.CertificateFactory;

public class CertificateValidator implements ConstraintValidator<Certificate, String> {

    @Override
    public void initialize(Certificate constraintAnnotation) {

    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        try {
            // Remove the header and footer if either is supplied
            String certificate = value.replaceAll(X509Factory.BEGIN_CERT, "")
                    .replaceAll(X509Factory.END_CERT, "");

            ByteArrayInputStream stream = new ByteArrayInputStream(Base64.decodeBase64(certificate.getBytes()));

            CertificateFactory.getInstance("X.509")
                    .generateCertificate(stream);

            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
