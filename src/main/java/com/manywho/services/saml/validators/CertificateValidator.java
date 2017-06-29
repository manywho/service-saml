package com.manywho.services.saml.validators;

import com.onelogin.AccountSettings;
import com.onelogin.saml.Response;
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

            AccountSettings accountSettings = new AccountSettings();
            accountSettings.setCertificate(value);

            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
