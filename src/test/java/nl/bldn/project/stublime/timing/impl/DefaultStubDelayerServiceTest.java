package nl.bldn.project.stublime.timing.impl;

import static org.assertj.core.api.Assertions.assertThat;

import nl.bldn.project.stublime.model.ResponseTiming;
import nl.bldn.project.stublime.timing.StubDelayService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.util.StopWatch;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.generator.InRange;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;

@RunWith(JUnitQuickcheck.class)
public class DefaultStubDelayerServiceTest {
    private static final int OVERHEAD_IN_MILLIS = 50;
    private final StubDelayService sut = new DefaultStubDelayerService(new DefaultDelayCalculator());

    @Test
    public void when_input_timing_is_null_then_do_nothing() {
        executeTimedTest(null, 0, 0, OVERHEAD_IN_MILLIS);
    }

    @Property
    public void when_timing_and_time_spent_then_actually_pause_the_execution(@InRange(minInt = 0, maxInt = 100) int minDelay, @InRange(minInt = 0, maxInt = 70) int timeSpentAlready) {
        executeTimedTest(ResponseTiming.builder().minimumDelay(minDelay).build(), timeSpentAlready, minDelay - timeSpentAlready, minDelay + OVERHEAD_IN_MILLIS);
    }

    private void executeTimedTest(ResponseTiming timing, int alreadySpent, long expectedMinimumDuration, long expectedMaximumDuration) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        sut.delayResponse(timing, alreadySpent);
        stopWatch.stop();

        long executionTime = stopWatch.getLastTaskTimeMillis();
        assertThat(executionTime).isGreaterThanOrEqualTo(expectedMinimumDuration);
        assertThat(executionTime).isLessThan(expectedMaximumDuration);
    }
}