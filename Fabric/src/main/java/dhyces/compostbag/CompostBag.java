package dhyces.compostbag;

import dhyces.compostbag.util.Ticker;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;

public class CompostBag implements ModInitializer, ClientModInitializer {
    
    @Override
    public void onInitialize() {
        Common.init();
    }

    @Environment(EnvType.CLIENT)
    public static final Ticker TICKER = new Ticker(10, 5);

    @Override
    public void onInitializeClient() {

    }
}
