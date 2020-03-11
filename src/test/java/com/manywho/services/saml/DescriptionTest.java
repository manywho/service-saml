package com.manywho.services.saml;

import com.manywho.services.saml.test.SamlServiceFunctionalTest;
import org.apache.commons.io.IOUtils;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import org.json.JSONException;
import org.junit.Assert;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import javax.ws.rs.core.MediaType;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

public class DescriptionTest extends SamlServiceFunctionalTest {
    @Test
    public void testDescription() throws URISyntaxException, IOException, JSONException {
        MockHttpRequest request = MockHttpRequest.post("/metadata")
                .content(new ByteArrayInputStream("{}".getBytes()))
                .contentType(MediaType.APPLICATION_JSON);

        MockHttpResponse response = new MockHttpResponse();

        dispatcher.invoke(request, response);

        String value = response.getContentAsString();

        JSONAssert.assertEquals(getFileContent("description.json"), value, false);
     }

    private String getFileContent(String path) throws IOException {
        return IOUtils.toString(getClass().getClassLoader().getResourceAsStream(path), StandardCharsets.UTF_8);
    }
}
