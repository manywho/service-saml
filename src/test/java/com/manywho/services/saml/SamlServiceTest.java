package com.manywho.services.saml;

import com.manywho.services.saml.services.JwtService;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;

public class SamlServiceTest {
    @Test
    public void testSignVerify() throws Exception {
        JwtService service = new JwtService("test-secret");
        String token = service.sign("123456", DateTime.now().minusSeconds(10), DateTime.now().plusSeconds(10));

        Assert.assertTrue(service.isValid(token));
    }

    @Test
    public void testSignVerifyExpiredToken() throws Exception {
        JwtService service = new JwtService("test-secret");
        String token = service.sign("123456", DateTime.now().minusSeconds(20), DateTime.now().minusSeconds(10));

        Assert.assertFalse(service.isValid(token));
    }

    @Test
    public void testSignVerifyTokenInFuture() throws Exception {
        JwtService service = new JwtService("test-secret");
        // the not before date is set in the future so the verification fails
        String token = service.sign("123456", DateTime.now().plusSeconds(10), DateTime.now().plusSeconds(20));

        Assert.assertFalse(service.isValid(token));
    }
}
