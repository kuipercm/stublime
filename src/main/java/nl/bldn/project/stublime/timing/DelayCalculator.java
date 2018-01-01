package nl.bldn.project.stublime.timing;

import nl.bldn.project.stublime.model.ResponseTiming;

public interface DelayCalculator {
    long calculateDelayInMillis(ResponseTiming timing, long timeSpentAlready);
}
