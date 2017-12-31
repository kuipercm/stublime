package nl.bldn.project.stublime.service;

import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import nl.bldn.project.stublime.model.ResponseKey;
import nl.bldn.project.stublime.model.StubResponse;
import nl.bldn.project.stublime.predicate.JsonBodySignaturePredicate;
import nl.bldn.project.stublime.predicate.XmlBodySignaturePredicate;

import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import com.jayway.jsonpath.JsonPath;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class StubResponseService {
    private final StubResponseRepository repository;
    private final StubResponseValidator validator;
    private final XPathFactory xPathFactory = XPathFactory.newInstance();

    public List<StubResponse> getAllResponses() {
        return repository.getAllStubResponses();
    }

    public StubResponse getStubResponse(String resource, HttpMethod method, String requestBody) {
        return repository.getStubResponse(resource, method, requestBody);
    }

    public void setStubResponse(StubResponse stubResponse) {
        validator.validateStubResponse(stubResponse);

        ResponseKey key = stubResponse.getKey();
        if (key.getId() == null) {
            key.setId(UUID.randomUUID());
        }

        String resource = key.getResource();
        key.setCompiledResource(Pattern.compile(resource));

        if (key.getBodyType() != null && key.getBodyType().equals("JSON")) {
            List<JsonPath> transformed = key.getBodySignatureExpressions().stream()
                    .map(JsonPath::compile)
                    .collect(Collectors.toList());
            key.setBodySignaturePredicate(new JsonBodySignaturePredicate(key.getExpectedBodySignature(), key.getSignatureElementsJoiner(), transformed));
        } else if (key.getBodyType() != null && key.getBodyType().equals("XML")) {
            XPath xPath = xPathFactory.newXPath();
            List<XPathExpression> transformed = key.getBodySignatureExpressions().stream()
                    .map(expression -> {
                        try {
                            return xPath.compile(expression);
                        } catch (XPathExpressionException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .collect(Collectors.toList());
            key.setBodySignaturePredicate(new XmlBodySignaturePredicate(key.getExpectedBodySignature(), key.getSignatureElementsJoiner(), transformed));
        }

        repository.saveStubResponse(stubResponse);
    }


}
