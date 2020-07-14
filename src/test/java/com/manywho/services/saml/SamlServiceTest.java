package com.manywho.services.saml;

import com.manywho.services.saml.services.JwtService;
import org.junit.Assert;
import org.junit.Test;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class SamlServiceTest {
    @Test
    public void testSignVerify() {
        JwtService service = new JwtService("test-secret");
        String token = service.sign("123456", null, null, LocalDateTime.now().minusSeconds(10), LocalDateTime.now().plusSeconds(10));

        Assert.assertTrue(service.isValid(token));
    }

    @Test
    public void testSignVerifyExpiredToken() {
        JwtService service = new JwtService("test-secret");
        String token = service.sign("123456", null, null, LocalDateTime.now().minusSeconds(20), LocalDateTime.now(ZoneOffset.UTC).minusSeconds(10));

        Assert.assertFalse(service.isValid(token));
    }

    @Test
    public void testSignVerifyTokenInFuture() {
        JwtService service = new JwtService("test-secret");
        // the not before date is set in the future so the verification fails
        String token = service.sign("123456", null, null, LocalDateTime.now().plusSeconds(10), LocalDateTime.now(ZoneOffset.UTC).plusSeconds(20));

        Assert.assertFalse(service.isValid(token));
    }

    @Test
    public void testPrimaryGroupInToken() {
        JwtService service = new JwtService("test-secret");
        String token = service.sign("123456", "group test id 1", "group test name 1", LocalDateTime.now().minusSeconds(10), LocalDateTime.now().plusSeconds(10));

        Assert.assertTrue(service.isValid(token));

        Assert.assertEquals("group test id 1", service.decode(token).getClaim("pgi").asString());
        Assert.assertEquals("group test name 1", service.decode(token).getClaim("pgn").asString());
    }
}
