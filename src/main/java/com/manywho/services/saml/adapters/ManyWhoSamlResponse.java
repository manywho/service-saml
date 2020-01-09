package com.manywho.services.saml.adapters;

import com.onelogin.saml2.authn.SamlResponse;
import com.onelogin.saml2.exception.SettingsException;
import com.onelogin.saml2.exception.ValidationError;
import com.onelogin.saml2.settings.Saml2Settings;
import com.onelogin.saml2.util.Util;
import org.joda.time.DateTime;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;

public class ManyWhoSamlResponse extends SamlResponse {

    private DateTime notBefore;
    private DateTime notAfter;

    public ManyWhoSamlResponse(Saml2Settings settings, String samlResponse, String currentUrl) throws ValidationError, SAXException, XPathExpressionException, SettingsException, ParserConfigurationException, IOException {
        super(settings, null);
        this.setDestinationUrl(currentUrl);
        this.loadXmlFromBase64(samlResponse);
        setNotBeforeAndNotAfterFromConditions();
    }

    public DateTime getNotBefore() {
        return notBefore;
    }

    public DateTime getNotAfter() {
        return notAfter;
    }

    void setNotBeforeAndNotAfterFromConditions() {
        NodeList timestampNodes = super.getSAMLResponseDocument().getElementsByTagNameNS("*", "Conditions");
        if (timestampNodes.getLength() != 0) {
            for(int i = 0; i < timestampNodes.getLength(); ++i) {
                NamedNodeMap attrName = timestampNodes.item(i).getAttributes();
                Node notBeforeAttribute = attrName.getNamedItem("NotBefore");
                Node notAfterAttribute = attrName.getNamedItem("NotOnOrAfter");

                if (notAfterAttribute != null) {
                    notAfter = moreRestrictiveAfterDate(notAfter, dateTimeFromString(notAfterAttribute.getNodeValue()));
                }

                if (notBeforeAttribute != null) {
                    notBefore = moreRestrictiveBeforeDate(notBefore, dateTimeFromString(notBeforeAttribute.getNodeValue()));
                }
            }
        }
    }

    private DateTime dateTimeFromString(String value) {
        return Util.parseDateTime(value);
    }

    private DateTime moreRestrictiveBeforeDate(DateTime current, DateTime newTime){
        if (newTime == null) {
            return current;
        }

        if (current == null) {
            return newTime;
        }

        if (newTime.getMillis() > current.getMillis()) {
            return newTime;
        }

        return current;
    }
    
    private DateTime moreRestrictiveAfterDate(DateTime current, DateTime newTime){
        if (newTime == null) {
            return current;
        }

        if (current == null) {
            return newTime;
        }

        if (newTime.getMillis() < current.getMillis()) {
            return newTime;
        }

        return current;
    }
}
