package dhyces.compostbag;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;

public class CompostBag implements ModInitializer {
    
    @Override
    public void onInitialize() {
        Common.init();
    }
}
