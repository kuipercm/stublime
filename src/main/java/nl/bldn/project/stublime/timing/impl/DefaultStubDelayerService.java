package nl.bldn.project.stublime.timing.impl;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

import nl.bldn.project.stublime.model.ResponseTiming;
import nl.bldn.project.stublime.timing.DelayCalculator;
import nl.bldn.project.stublime.timing.StubDelayService;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class DefaultStubDelayerService implements StubDelayService {
    private final DelayCalculator delayCalculator;
    private final Timer timer = new Timer("delayTimer", true);

    @Override
    public void delayResponse(ResponseTiming timing, long timeSpentAlready) {
        if (timing == null) {
            log.debug("No timing for response, doing nothing");
            return;
        }

        long toDelay = delayCalculator.calculateDelayInMillis(timing, timeSpentAlready);
        log.trace("Delaying response with {} millis", toDelay);

        // Using a semaphore here because Thread.sleep is too unreliable at (very) short delays.
        final Semaphore semaphore = new Semaphore(0);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                semaphore.release();
            }
        }, toDelay);

        try {
            semaphore.acquire();
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
