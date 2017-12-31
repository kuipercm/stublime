package nl.bldn.project.stublime.predicate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class XmlBodySignaturePredicate implements BodySignaturePredicate<XPathExpression> {
    private final String expectedBodySignature;
    private final String joiner;
    @Getter
    private final List<XPathExpression> bodySignatureExpressions;
    private final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

    public XmlBodySignaturePredicate(String expectedBodySignature, @NonNull String joiner, @NonNull List<XPathExpression> bodySignatureExpressions) {
        this.expectedBodySignature = expectedBodySignature;
        this.joiner = joiner;
        this.bodySignatureExpressions = bodySignatureExpressions;
    }

    @Override
    public Class<XPathExpression> getSignatureExpressionKlazz() {
        return XPathExpression.class;
    }

    @Override
    public boolean test(String requestBody) {
        if (expectedBodySignature == null) {
            return true;
        }
        if (requestBody == null) {
            return false;
        }
        if (expectedBodySignature.length() == 0 && bodySignatureExpressions.isEmpty()) {
            return true;
        }

        final Document bodyDocument = requestBodyAsXmlDocument(requestBody);
        if (bodyDocument == null) {
            return false;
        }

        String bodySignature = bodySignatureExpressions.stream()
                .map(pathExpression -> readXmlToken(pathExpression, bodyDocument))
                .collect(Collectors.joining(joiner));

        return expectedBodySignature.equals(bodySignature);
    }

    private Document requestBodyAsXmlDocument(String requestBody) {
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(new ByteArrayInputStream(requestBody.getBytes(StandardCharsets.UTF_8)));
        } catch (ParserConfigurationException | IOException | SAXException e) {
            log.warn("Error parsing requestbody as XML document: ", e);
        }
        return null;
    }

    private static String readXmlToken(XPathExpression pathExpression, Document bodyDocument) {
        try {
            return pathExpression.evaluate(bodyDocument);
        } catch (XPathExpressionException e) {
            log.warn("Could not evaluate XPathExpression: ", e);
            return "";
        }
    }
}
