package nl.bldn.project.stublime.timing.impl;

import static org.assertj.core.api.Assertions.assertThat;

import nl.bldn.project.stublime.model.ResponseTiming;
import nl.bldn.project.stublime.timing.DelayCalculator;

import org.junit.runner.RunWith;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.generator.InRange;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;

@RunWith(JUnitQuickcheck.class)
public class DefaultDelayCalculatorTest {
    private final DelayCalculator sut = new DefaultDelayCalculator();

    @Property
    public void when_delay_is_calculated_it_is_always_zero_or_greater_than_zero_with_time_spent(@InRange(minInt = 0, maxInt = 500) int minDelay, @InRange(minInt = 0, maxInt = 600) int timeSpentAlready) {
        long toDelay = sut.calculateDelayInMillis(ResponseTiming.builder().minimumDelay(minDelay).build(), timeSpentAlready);
        assertThat(toDelay).isGreaterThanOrEqualTo(0);
    }

    @Property
    public void when_delay_is_calculated_it_is_always_greater_than_zero_when_timespent_is_smaller_than_minimum_delay(@InRange(minInt = 200, maxInt = 500) int minDelay, @InRange(minInt = 0, maxInt = 200) int timeSpentAlready) {
        long toDelay = sut.calculateDelayInMillis(ResponseTiming.builder().minimumDelay(minDelay).build(), timeSpentAlready);
        assertThat(toDelay).isGreaterThan(0);
        assertThat(toDelay).isEqualTo(minDelay - timeSpentAlready);
    }

}