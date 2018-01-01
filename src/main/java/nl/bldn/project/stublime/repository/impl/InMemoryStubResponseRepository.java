package nl.bldn.project.stublime.repository.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;

import nl.bldn.project.stublime.model.ResponseKey;
import nl.bldn.project.stublime.model.StubResponse;
import nl.bldn.project.stublime.repository.StubResponseRepository;

import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Repository;

import lombok.Value;

@Repository
public class InMemoryStubResponseRepository implements StubResponseRepository {
    private final Map<UUID, StubResponse> allResponses = new LinkedHashMap<>();

    @Override
    public List<StubResponse> getAllStubResponses() {
        return new ArrayList<>(allResponses.values());
    }

    @Override
    public StubResponse saveStubResponse(StubResponse stubResponse) {
        allResponses.put(stubResponse.getKey().getId(), stubResponse);
        return stubResponse;
    }

    @Override
    public void deleteResponseById(UUID responseId) {
        allResponses.remove(responseId);
    }

    @Override
    public StubResponse getStubResponse(String resource, HttpMethod method, String requestBody) {

        MatcherStubResponse response = allResponses.values().stream()
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