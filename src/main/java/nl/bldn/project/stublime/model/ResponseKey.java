package nl.bldn.project.stublime.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import nl.bldn.project.stublime.predicate.BodySignaturePredicate;

import org.springframework.http.HttpMethod;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
@Builder
@JsonDeserialize(builder = ResponseKey.ResponseKeyBuilder.class)
public final class ResponseKey {
    public static final String DEFAULT_EMPTY_STRING_JOINER = "";
    public static final String DEFAULT_DASH_STRING_JOINER = "-";

    private final String resource;
    private final HttpMethod httpMethod;

    private final String expectedBodySignature;
    private final List<String> bodySignatureExpressions;
    private final String signatureElementsJoiner;
    private final String bodyType; //JSON or XML

    @Setter
    private Pattern compiledResource;
    @Setter
    private UUID id;
    @Setter @JsonIgnore
    BodySignaturePredicate<?> bodySignaturePredicate;

    @JsonPOJOBuilder(withPrefix = "")
    public static class ResponseKeyBuilder {
        private String signatureElementsJoiner = DEFAULT_EMPTY_STRING_JOINER;
        private List<String> bodySignatureExpressions = new ArrayList<>();
    }
}
