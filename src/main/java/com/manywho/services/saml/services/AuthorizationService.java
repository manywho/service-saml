package com.manywho.services.saml.services;

import com.auth0.jwt.interfaces.DecodedJWT;
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

    public $User createUserObject(AuthenticatedWho authenticatedWho, String loginUrl, String status) {
        $User result = new $User();
        result.setDirectoryId("SAML");
        result.setDirectoryName("SAML");
        result.setAuthenticationType(AuthorizationType.SAML);
        result.setLoginUrl(loginUrl);
        result.setStatus(status);
        result.setUserId(authenticatedWho.getUserId());
        result.setUsername(authenticatedWho.getUsername());
        result.setPrimaryGroupId(authenticatedWho.getPrimaryGroupId());
        result.setPrimaryGroupName(authenticatedWho.getPrimaryGroupName());
        result.setEmail(authenticatedWho.getEmail());
        result.setFirstName(authenticatedWho.getFirstName());

        addPrimaryGroup(status, authenticatedWho.getToken(), result);

        return result;
    }

    /**
     * If the user is authorized we add the PrimaryGroupId and PrimaryGroupName (we don't add those values if they are
     * empty)
     *
     * @param status status of the authorization
     * @param token our service generated jwt token
     * @param user we populate PrimaryGroupId and PrimaryGroupName in this object using the values of the token
     */
    public void addPrimaryGroup(String status, String token, $User user) {
        if ("200".equals(status)) {
            DecodedJWT decodedJwt = jwtService.decode(token);
            if (decodedJwt.getClaim("pgi") != null) {
                user.setPrimaryGroupId(decodedJwt.getClaim("pgi").asString());
            }

            if (decodedJwt.getClaim("pgn") != null) {
                user.setPrimaryGroupName(decodedJwt.getClaim("pgn").asString());
            }
        }
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
