package nl.bldn.project.stublime.timing.impl;

import nl.bldn.project.stublime.model.ResponseTiming;
import nl.bldn.project.stublime.timing.DelayCalculator;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "stublime.delay.type", havingValue = "default", matchIfMissing = true)
public class DefaultDelayCalculator implements DelayCalculator {
    @Override
    public long calculateDelayInMillis(ResponseTiming timing, long timeSpentAlready) {
        return Math.max(timing.getMinimumDelay() - timeSpentAlready, 0);
    }
}
