package com.manywho.services.saml;

import com.manywho.sdk.api.run.elements.config.Authorization;
import com.manywho.sdk.api.run.elements.config.Group;
import com.manywho.sdk.api.run.elements.config.User;
import com.manywho.sdk.api.security.AuthenticatedWho;
import com.manywho.services.saml.managers.CacheManager;
import com.manywho.services.saml.services.AuthorizationService;
import com.manywho.services.saml.services.JwtService;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AuthorizationServiceTest {
    @Test
    public void testGetStatusPublicFlow() throws Exception {
        CacheManager cacheManager = mock(CacheManager.class);
        JwtService jwtService = mock(JwtService.class);

        AuthorizationService authorizationService = new AuthorizationService(cacheManager, jwtService);
        Authorization authorization = new Authorization();
        AuthenticatedWho authenticatedWho = new AuthenticatedWho();

        authorization.setGlobalAuthenticationType(Authorization.AuthenticationType.Public);
        String actualStatus = authorizationService.getStatus(authorization, authenticatedWho);
        Assert.assertEquals("200", actualStatus);
    }

    @Test
    public void testGetStatusAllUsersFlow401() throws Exception {
        CacheManager cacheManager = mock(CacheManager.class);
        JwtService jwtService = mock(JwtService.class);

        AuthorizationService authorizationService = new AuthorizationService(cacheManager, jwtService);
        Authorization authorization = new Authorization();
        AuthenticatedWho authenticatedWho = new AuthenticatedWho();
        authenticatedWho.setUserId("PUBLIC_USER");

        authorization.setGlobalAuthenticationType(Authorization.AuthenticationType.AllUsers);
        String actualStatus = authorizationService.getStatus(authorization, authenticatedWho);
        Assert.assertEquals("401", actualStatus);
    }

    @Test
    public void testGetStatusAllUsersFlow200() throws Exception {
        CacheManager cacheManager = mock(CacheManager.class);
        JwtService jwtService = mock(JwtService.class);

        when(jwtService.isValid(any())).thenReturn(true);

        AuthorizationService authorizationService = new AuthorizationService(cacheManager, jwtService);
        Authorization authorization = new Authorization();
        AuthenticatedWho authenticatedWho = new AuthenticatedWho();
        authenticatedWho.setUserId("123456");

        authorization.setGlobalAuthenticationType(Authorization.AuthenticationType.AllUsers);
        String actualStatus = authorizationService.getStatus(authorization, authenticatedWho);
        Assert.assertEquals("200", actualStatus);
    }


    @Test
    public void testGetStatusAllUsersFlow401ExpiredToken() throws Exception {
        CacheManager cacheManager = mock(CacheManager.class);
        JwtService jwtService = mock(JwtService.class);

        when(jwtService.isValid(any()))
                .thenReturn(false);

        AuthorizationService authorizationService = new AuthorizationService(cacheManager, jwtService);
        Authorization authorization = new Authorization();
        AuthenticatedWho authenticatedWho = new AuthenticatedWho();
        authenticatedWho.setUserId("123456");

        authorization.setGlobalAuthenticationType(Authorization.AuthenticationType.AllUsers);
        String actualStatus = authorizationService.getStatus(authorization, authenticatedWho);
        Assert.assertEquals("401", actualStatus);
    }

    @Test
    public void testGetStatusSpecifiedFlow401ExpiredToken() throws Exception {
        CacheManager cacheManager = mock(CacheManager.class);
        JwtService jwtService = mock(JwtService.class);

        when(jwtService.isValid(any()))
                .thenReturn(false);

        AuthorizationService authorizationService = new AuthorizationService(cacheManager, jwtService);
        Authorization authorization = new Authorization();
        AuthenticatedWho authenticatedWho = new AuthenticatedWho();
        authenticatedWho.setUserId("123456");

        authorization.setGlobalAuthenticationType(Authorization.AuthenticationType.Specified);
        String actualStatus = authorizationService.getStatus(authorization, authenticatedWho);
        Assert.assertEquals("401", actualStatus);
    }

    @Test
    public void testGetStatusSpecifiedFlow401GroupOrUserNotAuthorized() throws Exception {
        CacheManager cacheManager = mock(CacheManager.class);
        JwtService jwtService = mock(JwtService.class);

        when(jwtService.isValid(any()))
                .thenReturn(true);

        AuthorizationService authorizationService = new AuthorizationService(cacheManager, jwtService);
        Authorization authorization = new Authorization();
        AuthenticatedWho authenticatedWho = new AuthenticatedWho();
        authenticatedWho.setUserId("123456");

        authorization.setGlobalAuthenticationType(Authorization.AuthenticationType.Specified);
        String actualStatus = authorizationService.getStatus(authorization, authenticatedWho);
        Assert.assertEquals("401", actualStatus);
    }

    @Test
    public void testGetStatusSpecifiedFlow200UserAuthorized() throws Exception {
        CacheManager cacheManager = mock(CacheManager.class);
        JwtService jwtService = mock(JwtService.class);

        when(jwtService.isValid(any()))
                .thenReturn(true);

        AuthorizationService authorizationService = new AuthorizationService(cacheManager, jwtService);
        Authorization authorization = new Authorization();
        AuthenticatedWho authenticatedWho = new AuthenticatedWho();
        authenticatedWho.setUserId("user-test");

        authorization.setGlobalAuthenticationType(Authorization.AuthenticationType.Specified);
        User userTest = new User();
        userTest.setAuthenticationId("user-test");
        List<User> users = new ArrayList<>();
        users.add(userTest);
        authorization.setUsers(users);

        String actualStatus = authorizationService.getStatus(authorization, authenticatedWho);
        Assert.assertEquals("200", actualStatus);
    }

    @Test
    public void testGetStatusSpecifiedFlow200GroupAuthorized() throws Exception {
        CacheManager cacheManager = mock(CacheManager.class);
        JwtService jwtService = mock(JwtService.class);
        when(jwtService.isValid(any()))
                .thenReturn(true);
        ArrayList<String> userGroups = new ArrayList<>();
        userGroups.add("group-test");
        when(cacheManager.getUserGroups("user-test"))
                .thenReturn(userGroups);
        AuthorizationService authorizationService = new AuthorizationService(cacheManager, jwtService);
        Authorization authorization = new Authorization();
        AuthenticatedWho authenticatedWho = new AuthenticatedWho();
        authenticatedWho.setUserId("user-test");
        authorization.setGlobalAuthenticationType(Authorization.AuthenticationType.Specified);
        Group groupTest = new Group();
        groupTest.setAuthenticationId("group-test");
        List<Group> groups = new ArrayList<>();
        groups.add(groupTest);
        authorization.setGroups(groups);
        String actualStatus = authorizationService.getStatus(authorization, authenticatedWho);
        Assert.assertEquals("200", actualStatus);
    }
}
