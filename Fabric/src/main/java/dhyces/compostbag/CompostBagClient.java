package dhyces.compostbag;

import dhyces.compostbag.item.CompostBagItem;
import dhyces.compostbag.tooltip.ClientCompostBagTooltip;
import dhyces.compostbag.tooltip.CompostBagTooltip;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;

public class CompostBagClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ItemProperties.register(Common.COMPOST_BAG_ITEM.get(), Common.modLoc("filled"), CompostBagItem::getFullnessDisplay);
        ClientPlayNetworking.registerGlobalReceiver(Common.modLoc("config_sync"), (client, handler, buf, responseSender) -> {
            var maxBonemeal = buf.readInt();

            client.execute(() -> CompostBag.MAX_BONEMEAL = maxBonemeal);
        });
        ClientPlayConnectionEvents.DISCONNECT.register(Event.DEFAULT_PHASE, (handler, client) -> {
            client.execute(CompostBag::reloadConfig);
        });
        TooltipComponentCallback.EVENT.register(data -> {
            if (data instanceof CompostBagTooltip tooltipComponent) {
                return new ClientCompostBagTooltip(tooltipComponent);
            }
            return null;
        });
    }
}
