package dev.dhyces.compostbag;

import dev.dhyces.compostbag.util.Ticker;
import dev.dhyces.compostbag.platform.Services;

public class CommonClient {
    static final Ticker TICKER = new Ticker(10, 3);

    public static Ticker getTickerInstance() {
        if (Services.PLATFORM.isSideClient())
            return TICKER;
        return null;
    }
}
