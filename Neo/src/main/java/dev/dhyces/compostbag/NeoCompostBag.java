package dev.dhyces.compostbag;

import dev.dhyces.compostbag.item.CompostBagItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.block.DispenserBlock;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

@Mod(CompostBag.MODID)
public class NeoCompostBag {
    
    public NeoCompostBag(IEventBus modBus, Dist dist) {
        CompostBag.init();
        ModRegistry.register(modBus);

        modBus.addListener(this::commonSetup);
        modBus.addListener(this::onAddToTabs);

        if (dist.isClient()) {
            NeoCompostBagClient.init(modBus);
        }
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() ->
            DispenserBlock.registerBehavior(CompostBag.COMPOST_BAG_ITEM.value(), CompostBagItem.DISPENSE_BEHAVIOR)
        );
    }

    private void onAddToTabs(final BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(CompostBag.COMPOST_BAG_ITEM.value());
        }
    }
}