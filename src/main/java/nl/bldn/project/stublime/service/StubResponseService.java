package nl.bldn.project.stublime.service;

import static nl.bldn.project.stublime.predicate.BodySignaturePredicateFactory.createBodySignaturePredicate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nl.bldn.project.stublime.model.ResponseKey;
import nl.bldn.project.stublime.model.StubResponse;
import nl.bldn.project.stublime.repository.impl.InMemoryStubResponseRepository;
import nl.bldn.project.stublime.validation.StubResponseValidator;

import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.Value;

@Service
@AllArgsConstructor
public class StubResponseService {
    private final InMemoryStubResponseRepository repository;
    private final StubResponseValidator validator;

    public List<StubResponse> getAllResponses() {
        return repository.getAllStubResponses();
    }

    public StubResponse setStubResponse(StubResponse stubResponse) {
        validator.validateStubResponse(stubResponse);

        ResponseKey key = stubResponse.getKey();
        String resource = key.getResource();
        key.setCompiledResource(Pattern.compile(resource));

        if (key.getBodyType() != null) {
            key.setBodySignaturePredicate(createBodySignaturePredicate(key.getBodyType(), key.getExpectedBodySignature(), key.getSignatureElementsJoiner(), key.getBodySignatureExpressions()));
        }

        return repository.saveStubResponse(stubResponse);
    }

    public void deleteStubResponse(UUID responseId) {
        repository.deleteResponseById(responseId);
    }

    public StubResponse getStubResponse(String resource, HttpMethod method, String requestBody) {
        MatcherStubResponse response = repository.getAllStubResponses().stream()
                .map(stubResponse -> new MatcherStubResponse(stubResponse.getKey().getCompiledResource().matcher(resource), stubResponse))
                .filter(msr -> msr.matcher.find())
                .filter(msr -> {
                    ResponseKey key = msr.stubResponse.getKey();
                    return key.getBodySignaturePredicate() == null || key.getBodySignaturePredicate().test(requestBody);
                })
                .filter(msr -> msr.stubResponse.getKey().getHttpMethod() == null || msr.stubResponse.getKey().getHttpMethod() == method)
                .findFirst()
                .orElse(null);

        if (response == null) {
            return null;
        }

        return postProcessResponse(response, requestBody);
    }

    private StubResponse postProcessResponse(MatcherStubResponse response, String requestBody) {
        Map<Integer, String> matchedGroupsInUrl = findMatchedGroupsInUrl(response.matcher);

        // use matched elements from resource (url) to fill response
        // use additional expressions (jsonpath/xpath) to extract body elements and place them in the response

        return response.stubResponse;
    }

    private Map<Integer, String> findMatchedGroupsInUrl(Matcher matcher) {
        Map<Integer, String> result = new HashMap<>();
        for (int i = 1; i <= matcher.groupCount(); i++) {
            result.put(i, matcher.group(i));
        }
        return result;
    }


    @Value
    private static class MatcherStubResponse {
        private final Matcher matcher;
        private final StubResponse stubResponse;
    }

}
