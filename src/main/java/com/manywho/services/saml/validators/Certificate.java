package com.manywho.services.saml.validators;


import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CertificateValidator.class)
@Documented
public @interface Certificate {

    Class<?>[] groups() default {};

    String message() default "An invalid certificate was given";

    Class<? extends Payload>[] payload() default {};
}