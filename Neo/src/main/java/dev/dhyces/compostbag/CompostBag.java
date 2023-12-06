package dev.dhyces.compostbag;

import dev.dhyces.compostbag.item.CompostBagItem;
import net.minecraft.world.level.block.DispenserBlock;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.common.NeoForge;

@Mod(Constants.MOD_ID)
public class CompostBag {
    
    public CompostBag() {
        Common.init();
        var modBus = FMLJavaModLoadingContext.get().getModEventBus();
        Registry.register(modBus);

        modBus.addListener(this::commonSetup);

        if (FMLLoader.getDist().isClient()) {
            ClientEvents.init(modBus, NeoForge.EVENT_BUS);
        }

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.serverSpec);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            DispenserBlock.registerBehavior(Common.COMPOST_BAG_ITEM.get(), CompostBagItem.DISPENSE_BEHAVIOR);
        });
    }
}