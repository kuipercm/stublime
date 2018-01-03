package nl.bldn.project.stublime.predicate;

import java.util.List;
import java.util.stream.Collectors;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.JsonPathException;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsonBodySignaturePredicate implements BodySignaturePredicate<JsonPath> {
    private final String expectedBodySignature;
    private final String joiner;
    @Getter
    private final List<JsonPath> bodySignatureExpressions;

    public JsonBodySignaturePredicate(String expectedBodySignature, @NonNull String joiner, @NonNull List<JsonPath> bodySignatureExpressions) {
        this.expectedBodySignature = expectedBodySignature;
        this.joiner = joiner;
        this.bodySignatureExpressions = bodySignatureExpressions;
    }

    @Override
    public Class<JsonPath> getSignatureExpressionKlazz() {
        return JsonPath.class;
    }

    @Override
    public boolean test(final String requestBody) {
        if (expectedBodySignature == null) {
            return true;
        }
        if (requestBody == null || requestBody.isEmpty()) {
            return false;
        }

        String bodySignature = bodySignatureExpressions.stream()
                .map(pathExpression -> readJsonToken(pathExpression, requestBody))
                .collect(Collectors.joining(joiner));

        return expectedBodySignature.equals(bodySignature);
    }

    private static String readJsonToken(JsonPath pathExpression, String requestBody) {
        try {
            return pathExpression.read(requestBody).toString();
        } catch (JsonPathException e) {
            log.debug("Error while reading json body through expressions: ", e);
            return "";
        }
    }
}
