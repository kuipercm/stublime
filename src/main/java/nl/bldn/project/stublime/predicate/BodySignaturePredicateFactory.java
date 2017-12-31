package nl.bldn.project.stublime.predicate;

import static java.util.Arrays.asList;

import java.util.List;
import java.util.stream.Collectors;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import com.jayway.jsonpath.JsonPath;

public class BodySignaturePredicateFactory {
    public static final String BODY_TYPE_JSON = "JSON";
    public static final String BODY_TYPE_XML = "XML";
    public static final List<String> SUPPORTED_BODY_TYPES = asList(BODY_TYPE_JSON, BODY_TYPE_XML);

    private static final XPathFactory xPathFactory = XPathFactory.newInstance();

    private BodySignaturePredicateFactory() {
        //don't instantiate
    }

    public static BodySignaturePredicate<?> createBodySignaturePredicate(String bodyType, String expectedBodySignature, String joiner, List<String> bodySignatureExpressions) {
        if (BODY_TYPE_JSON.equalsIgnoreCase(bodyType)) {
            List<JsonPath> transformed = bodySignatureExpressions.stream()
                    .map(JsonPath::compile)
                    .collect(Collectors.toList());

            return new JsonBodySignaturePredicate(expectedBodySignature, joiner, transformed);
        } else if (BODY_TYPE_XML.equalsIgnoreCase(bodyType)) {
            XPath xPath = xPathFactory.newXPath();
            List<XPathExpression> transformed = bodySignatureExpressions.stream()
                    .map(expression -> {
                        try {
                            return xPath.compile(expression);
                        } catch (XPathExpressionException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .collect(Collectors.toList());

            return new XmlBodySignaturePredicate(expectedBodySignature, joiner, transformed);
        }

        throw new IllegalArgumentException("Unknown body type " + bodyType + ". Supported body types: " + SUPPORTED_BODY_TYPES);
    }

}
