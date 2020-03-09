package com.manywho.services.saml;

import com.manywho.services.saml.actions.Group;
import com.manywho.services.saml.services.JwtService;
import org.junit.Assert;
import org.junit.Test;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SamlServiceTest {
    @Test
    public void testSignVerify() throws Exception {
        JwtService service = new JwtService("test-secret");
        String token = service.sign("123456", LocalDateTime.now().minusSeconds(10), LocalDateTime.now().plusSeconds(10), new ArrayList<>());

        Assert.assertTrue(service.isValid(token));
    }

    @Test
    public void testSignVerifyExpiredToken() throws Exception {
        JwtService service = new JwtService("test-secret");
        String token = service.sign("123456", LocalDateTime.now().minusSeconds(20), LocalDateTime.now().minusSeconds(10), new ArrayList<>());

        Assert.assertFalse(service.isValid(token));
    }

    @Test
    public void testSignVerifyTokenInFuture() throws Exception {
        JwtService service = new JwtService("test-secret");
        // the not before date is set in the future so the verification fails
        String token = service.sign("123456", LocalDateTime.now().plusSeconds(10), LocalDateTime.now().plusSeconds(20), new ArrayList<>());

        Assert.assertFalse(service.isValid(token));
    }


    @Test
    public void testGetGroups() throws Exception {
        JwtService service = new JwtService("test-secret");
        List<String> groups = new ArrayList<>();
        groups.add("group 1");
        groups.add("group 2");
        groups.add("group 3");

        String token = service.sign("123456", LocalDateTime.now().plusSeconds(10), LocalDateTime.now().plusSeconds(20), groups);
        List<Group> groupsList = JwtService.getGroups(token);

        Assert.assertEquals(groupsList.size(), 3);
        Assert.assertEquals(groupsList.get(0).getName(), "group 1");
        Assert.assertEquals(groupsList.get(1).getName(), "group 2");
        Assert.assertEquals(groupsList.get(2).getName(), "group 3");
    }
}
