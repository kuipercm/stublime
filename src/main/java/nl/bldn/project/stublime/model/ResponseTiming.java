package nl.bldn.project.stublime.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

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
@JsonDeserialize(builder = ResponseTiming.ResponseTimingBuilder.class)
public final class ResponseTiming {
    private final int minimumDelay;
    private final int maximumDelay;
    private final int variance;
}
