package com.manywho.services.saml.services;

import com.manywho.sdk.entities.UserObject;
import com.manywho.sdk.entities.run.elements.config.Authorization;
import com.manywho.sdk.entities.security.AuthenticatedWho;
import com.manywho.sdk.enums.AuthenticationType;
import com.manywho.sdk.enums.AuthorizationType;
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

    public UserObject createUserObject(AuthenticatedWho authenticatedWho, String loginUrl, String status) {
        return new UserObject("SAML", AuthorizationType.SAML, loginUrl, status, authenticatedWho.getUserId(),
                authenticatedWho.getUsername(), authenticatedWho.getEmail(), authenticatedWho.getFirstName());
    }

    public String getStatus(Authorization authorization,  AuthenticatedWho user) throws Exception {
        switch (authorization.getGlobalAuthenticationType()) {
            case Public:
                return "200";
            case AllUsers:
                if (!user.getUserId().equalsIgnoreCase("PUBLIC_USER")) {
                    if (jwtService.isValid(user.getToken()) == false) {
                        return "401";
                    }
                    return "200";
                } else {
                    return "401";
                }
            case Specified:
                if (!user.getUserId().equalsIgnoreCase("PUBLIC_USER")) {
                    if (jwtService.isValid(user.getToken()) == false) {
                        return "401";
                    }

                    boolean validGroup = false;
                    boolean validUser = false;

                    if (CollectionUtils.isNotEmpty(authorization.getGroups())) {
                        ArrayList<String> userGroups = cacheManager.getUserGroups(user.getUserId());
                        for (String group: userGroups) {
                            if (authorization.getGroups().stream().anyMatch(m -> m.equals(group))) {
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
            default:
                return "401";
        }
    }


    public boolean shouldCleanLoginUrl(Authorization authorization,  AuthenticatedWho user, String status) {
        if (authorization.getGlobalAuthenticationType() == AuthenticationType.Specified &&
                user.getUserId().equalsIgnoreCase("PUBLIC_USER") == false &&
                jwtService.isValid(user.getToken()) &&
                status.equalsIgnoreCase("401")) {
            return true;
        }

        return false;
    }
}
