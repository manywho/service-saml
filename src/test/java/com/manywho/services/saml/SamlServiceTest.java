package com.manywho.services.saml;

import com.manywho.sdk.enums.AuthenticationType;
import com.manywho.sdk.entities.run.elements.config.Authorization;
import com.manywho.sdk.entities.run.elements.config.Group;
import com.manywho.sdk.entities.run.elements.config.GroupCollection;
import com.manywho.sdk.entities.security.AuthenticatedWho;
import com.manywho.services.saml.managers.CacheManager;
import com.manywho.services.saml.services.AuthorizationService;
import com.manywho.services.saml.services.JwtService;
import org.junit.Assert;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;


import java.time.LocalDateTime;
import java.util.ArrayList;

public class SamlServiceTest {
    @Test
    public void testSignVerify() throws Exception {
        JwtService service = new JwtService("test-secret");
        String token = service.sign("123456", LocalDateTime.now().minusSeconds(10), LocalDateTime.now().plusSeconds(10));

        Assert.assertTrue(service.isValid(token));
    }

    @Test
    public void testSignVerifyExpiredToken() throws Exception {
        JwtService service = new JwtService("test-secret");
        String token = service.sign("123456", LocalDateTime.now().minusSeconds(20), LocalDateTime.now().minusSeconds(10));

        Assert.assertFalse(service.isValid(token));
    }

    @Test
    public void testSignVerifyTokenInFuture() throws Exception {
        JwtService service = new JwtService("test-secret");
        // the not before date is set in the future so the verification fails
        String token = service.sign("123456", LocalDateTime.now().plusSeconds(10), LocalDateTime.now().plusSeconds(20));

        Assert.assertFalse(service.isValid(token));
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
        authorization.setGlobalAuthenticationType(AuthenticationType.Specified);
        Group groupTest = new Group();
        groupTest.setAuthenticationId("group-test");
        GroupCollection groups = new GroupCollection();
        groups.add(groupTest);
        authorization.setGroups(groups);
        String actualStatus = authorizationService.getStatus(authorization, authenticatedWho);
        Assert.assertEquals("200", actualStatus);
    }
}
