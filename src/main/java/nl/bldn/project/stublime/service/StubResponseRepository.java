package nl.bldn.project.stublime.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nl.bldn.project.stublime.model.ResponseKey;
import nl.bldn.project.stublime.model.StubResponse;

import org.springframework.http.HttpMethod;

import lombok.Value;

public class StubResponseRepository {
    private final Map<Pattern, StubResponse> allResponses = new LinkedHashMap<>();

    public List<StubResponse> getAllStubResponses() {
        return new ArrayList<>(allResponses.values());
    }

    public void saveStubResponse(StubResponse stubResponse) {
        allResponses.put(stubResponse.getKey().getCompiledResource(), stubResponse);
    }

    public StubResponse getStubResponse(String resource, HttpMethod method, String requestBody) {

        MatcherStubResponse response = allResponses.entrySet().stream()
                .map(e -> new MatcherStubResponse(e.getKey().matcher(resource), e.getValue()))
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