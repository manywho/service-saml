package com.manywho.services.saml.adapters;

import com.onelogin.saml2.authn.SamlResponse;
import com.onelogin.saml2.exception.SettingsException;
import com.onelogin.saml2.exception.ValidationError;
import com.onelogin.saml2.settings.Saml2Settings;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;

public class ManyWhoSamlResponse extends SamlResponse {
    public ManyWhoSamlResponse(Saml2Settings settings, String samlResponse, String currentUrl) throws ValidationError, SAXException, XPathExpressionException, SettingsException, ParserConfigurationException, IOException {
        super(settings, null);
        this.setDestinationUrl(currentUrl);
        this.loadXmlFromBase64(samlResponse);
    }
}
