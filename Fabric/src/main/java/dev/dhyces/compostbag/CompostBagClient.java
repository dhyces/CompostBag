package dev.dhyces.compostbag;

import dev.dhyces.compostbag.tooltip.ClientCompostBagTooltip;
import dev.dhyces.compostbag.tooltip.CompostBagTooltip;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import net.minecraft.client.renderer.item.ItemProperties;

public class CompostBagClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ItemProperties.register(Common.COMPOST_BAG_ITEM.value(), Common.id("filled"), CommonClient::bonemealFullness);
        TooltipComponentCallback.EVENT.register(data -> {
            if (data instanceof CompostBagTooltip tooltipComponent) {
                return new ClientCompostBagTooltip(tooltipComponent);
            }
            return null;
        });
    }
}
