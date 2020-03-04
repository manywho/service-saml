package com.manywho.services.saml.entities;

import com.google.common.collect.Lists;
import com.manywho.services.saml.adapters.ManyWhoSaml2Settings;
import com.onelogin.saml2.http.HttpRequest;
import com.onelogin.saml2.logout.LogoutRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SamlLogoutRequestHandler {
    private LogoutRequest request;
    private ManyWhoSaml2Settings saml2Settings;

    public SamlLogoutRequestHandler(ApplicationConfiguration configuration, String logoutRequestCode, String currentUrl) {
        try {
            saml2Settings = new ManyWhoSaml2Settings(configuration);

            Map<String, List<String>> requestParams = new HashMap<>();
            requestParams.put("SAMLRequest", Lists.newArrayList(logoutRequestCode));
            HttpRequest httpRequest = new HttpRequest(currentUrl, requestParams, "");

            request = new LogoutRequest(saml2Settings, httpRequest);

        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    public String getUserId() {
        try {
            return LogoutRequest.getNameId(request.getLogoutRequestXml());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getError() {
        return request.getError();
    }

    public boolean isValid() {
        try {
            return request.isValid();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ManyWhoSaml2Settings getSaml2Settings() {
        return saml2Settings;
    }
}
