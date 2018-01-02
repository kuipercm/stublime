package nl.bldn.project.stublime.timing.impl;

import static java.lang.Math.max;
import static java.lang.Math.min;

import java.util.Random;

import nl.bldn.project.stublime.model.ResponseTiming;
import nl.bldn.project.stublime.timing.DelayCalculator;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "stublime.delay.type", havingValue = "gaussian")
public class GaussianDelayCalculator implements DelayCalculator {
    private final Random random = new Random();

    @Override
    public long calculateDelayInMillis(ResponseTiming timing, long timeSpentAlready) {
        double gaussianRandomValue = getBoundGaussian();
        double mean = timing.getMinimumDelay() + ((timing.getMaximumDelay() - timing.getMinimumDelay()) / 2d);
        double halfTheCurve = timing.getMaximumDelay() - mean;
        double calculatedRandomValue = gaussianRandomValue * halfTheCurve;

        double calculatedDelay = mean + calculatedRandomValue;
        double boundedDelay = min(max(calculatedDelay, timing.getMinimumDelay()), timing.getMaximumDelay());
        double actualDelay = max(boundedDelay - timeSpentAlready, 0);

        return (long) actualDelay;
    }

    private double getBoundGaussian() {
        double gaussianRandomValue = random.nextGaussian();
        while(gaussianRandomValue < -1.0 || gaussianRandomValue > 1.0) {
            gaussianRandomValue = random.nextGaussian();
        }
        return gaussianRandomValue;
    }
}
