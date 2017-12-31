package nl.bldn.project.stublime.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
@Builder
@JsonDeserialize(builder = StubResponse.StubResponseBuilder.class)
public final class StubResponse {
    private final ResponseKey key;
    private final ResponseDefinition response;
    private final ResponseTiming responseTiming;

    @JsonPOJOBuilder(withPrefix = "")
    public static class StubResponseBuilder {
        //for serialization annotation
    }
}
