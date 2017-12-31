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
@JsonDeserialize(builder = ResponseDefinition.ResponseDefinitionBuilder.class)
public final class ResponseDefinition {
    private final String responseContent;
    private final int responseStatusCode;

    @JsonPOJOBuilder(withPrefix = "")
    public static class ResponseDefinitionBuilder {
        private int responseStatusCode = 200;
    }
}
