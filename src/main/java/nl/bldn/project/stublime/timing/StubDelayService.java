package nl.bldn.project.stublime.timing;

import nl.bldn.project.stublime.model.ResponseTiming;

public interface StubDelayService {
    void delayResponse(ResponseTiming timing, long alreadyTaken);
}
