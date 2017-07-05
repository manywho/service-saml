package com.manywho.services.saml.validators;

import com.manywho.services.saml.adapters.ManyWhoSaml2Settings;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CertificateValidator implements ConstraintValidator<Certificate, String> {

    @Override
    public void initialize(Certificate constraintAnnotation) {

    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        try {

            ManyWhoSaml2Settings.parseCertificate(value);

            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
