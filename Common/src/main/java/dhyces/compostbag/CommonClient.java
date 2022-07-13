package dhyces.compostbag;

import dhyces.compostbag.platform.Services;
import dhyces.compostbag.util.Ticker;

public class CommonClient {
    static final Ticker TICKER = new Ticker(10, 3);

    public static Ticker getTickerInstance() {
        if (Services.PLATFORM.isSideClient())
            return TICKER;
        return null;
    }
}
