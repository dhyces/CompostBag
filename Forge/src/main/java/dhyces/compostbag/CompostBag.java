package dhyces.compostbag;

import dhyces.compostbag.item.CompostBagItem;
import dhyces.compostbag.tooltip.ClientCompostBagTooltip;
import dhyces.compostbag.tooltip.CompostBagTooltip;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;

@Mod(Constants.MOD_ID)
public class CompostBag {
    
    public CompostBag() {
        Common.init();
        var modBus = FMLJavaModLoadingContext.get().getModEventBus();

        modBus.addListener(this::commonSetup);

        if (FMLLoader.getDist().isClient()) {
            modBus.addListener(this::clientSetup);
            modBus.addListener(this::registerTooltipComponents);
            modBus.addListener(this::onAddToTabs);
        }

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.serverSpec);

        Registry.register(modBus);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            DispenserBlock.registerBehavior(Common.COMPOST_BAG_ITEM.get(), CompostBagItem.DISPENSE_BEHAVIOR);
        });
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemProperties.register(Common.COMPOST_BAG_ITEM.get(), Common.modLoc("filled"), CompostBagItem::getFullnessDisplay);
        });
    }

    private void onAddToTabs(CreativeModeTabEvent.BuildContents event) {
        event.registerSimple(CreativeModeTabs.TOOLS_AND_UTILITIES, Common.COMPOST_BAG_ITEM.get());
    }

    private void registerTooltipComponents(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(CompostBagTooltip.class, ClientCompostBagTooltip::new);
    }
}