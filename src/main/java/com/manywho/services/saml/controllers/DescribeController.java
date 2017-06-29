package com.manywho.services.saml.controllers;

import com.manywho.sdk.entities.describe.DescribeServiceResponse;
import com.manywho.sdk.entities.describe.DescribeValue;
import com.manywho.sdk.entities.describe.DescribeValueCollection;
import com.manywho.sdk.entities.run.elements.config.ServiceRequest;
import com.manywho.sdk.entities.translate.Culture;
import com.manywho.sdk.enums.ContentType;
import com.manywho.sdk.services.describe.DescribeServiceBuilder;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/")
@Consumes("application/json")
@Produces("application/json")
public class DescribeController {
    @Path("/metadata")
    @POST
    public DescribeServiceResponse describe(ServiceRequest serviceRequest) throws Exception {
        return new DescribeServiceBuilder()
                .setProvidesIdentity(true)
                .setCulture(new Culture("EN", "US"))
                .setConfigurationValues(new DescribeValueCollection(
                        new DescribeValue("Certificate", ContentType.Encrypted, true),
                        new DescribeValue("Login URL", ContentType.String, true),
                        new DescribeValue("Assertion Consumer Service (URL)", ContentType.String, true),
                        new DescribeValue("Identifier of the IdP entity (URI)", ContentType.String, false),
                        new DescribeValue("Identifier of the SP entity (URI)", ContentType.String, false),
                        new DescribeValue("No XML Validation", ContentType.Boolean, false),
                        new DescribeValue("Debug", ContentType.Boolean, false),
                        new DescribeValue("Supported Users", ContentType.String, false),
                        new DescribeValue("Supported Groups", ContentType.String, false)
                ))
                .createDescribeService()
                .createResponse();
    }
}