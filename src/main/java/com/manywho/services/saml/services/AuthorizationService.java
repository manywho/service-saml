package com.manywho.services.saml.services;

import com.manywho.sdk.api.AuthorizationType;
import com.manywho.sdk.api.run.elements.config.Authorization;
import com.manywho.sdk.api.security.AuthenticatedWho;
import com.manywho.sdk.services.types.system.$User;
import com.manywho.services.saml.managers.CacheManager;
import org.apache.commons.collections4.CollectionUtils;
import javax.inject.Inject;
import java.util.ArrayList;

public class AuthorizationService {

    private CacheManager cacheManager;
    private JwtService jwtService;

    @Inject
    public AuthorizationService(CacheManager cacheManager, JwtService jwtService) {
        this.cacheManager = cacheManager;
        this.jwtService = jwtService;
    }

    public $User createUserObject(AuthenticatedWho authenticatedWho, String loginUrl, String status) throws Exception {
        $User result = new $User();
        result.setDirectoryId("SAML");
        result.setDirectoryName("SAML");
        result.setAuthenticationType(AuthorizationType.SAML);
        result.setLoginUrl(loginUrl);
        result.setStatus(status);
        result.setUserId(authenticatedWho.getUserId());
        result.setUsername(authenticatedWho.getUsername());
        result.setEmail(authenticatedWho.getEmail());
        result.setFirstName(authenticatedWho.getFirstName());

        if ("200".equals(result.getStatus())) {
            String groups = String.join(",", cacheManager.getUserGroups(result.getUserId()));
            result.setPrimaryGroupName(groups);
        }

        return result;
    }

    public String getStatus(Authorization authorization, AuthenticatedWho user) throws Exception {
        switch (authorization.getGlobalAuthenticationType()) {
            case Public:
                return "200";
            case AllUsers:
                if (user.getUserId().equalsIgnoreCase("PUBLIC_USER")) {
                    return "401";
                } else if (jwtService.isValid(user.getToken())) {
                    return "200";
                }

                return "401";
            case Specified:
                if (user.getUserId().equalsIgnoreCase("PUBLIC_USER")) {
                    return "401";
                } else if (jwtService.isValid(user.getToken())) {
                    boolean validGroup = false;
                    boolean validUser = false;

                    if (CollectionUtils.isNotEmpty(authorization.getGroups())) {
                        ArrayList<String> userGroups = cacheManager.getUserGroups(user.getUserId());
                        for (String group: userGroups) {
                            if (authorization.getGroups().stream().anyMatch(m -> m.getAuthenticationId().equals(group))) {
                                validGroup = true;
                            }
                        }
                    }

                    if (CollectionUtils.isNotEmpty(authorization.getUsers())) {
                        if (authorization.getUsers().stream().anyMatch(m -> m.getAuthenticationId().equals(user.getUserId()))) {
                            validUser = true;
                        }
                    }

                    if (validGroup || validUser) {
                        return "200";
                    }
                }

                return "401";
            default:
                return "401";
        }
    }


    public boolean shouldSendLoginUrl(Authorization authorization, AuthenticatedWho user, String status) {
        if (user.getUserId().equalsIgnoreCase("PUBLIC_USER") == false &&
                authorization.getGlobalAuthenticationType() == Authorization.AuthenticationType.Specified &&
                jwtService.isValid(user.getToken()) &&
                status.equalsIgnoreCase("401")) {

            return false;
        }

        return true;
    }
}
