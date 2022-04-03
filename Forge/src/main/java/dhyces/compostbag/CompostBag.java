package dhyces.compostbag;

import dhyces.compostbag.item.CompostBagItem;
import dhyces.compostbag.platform.Config;
import dhyces.compostbag.tooltip.ClientCompostBagTooltip;
import dhyces.compostbag.tooltip.CompostBagTooltip;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Constants.MOD_ID)
public class CompostBag {
    
    public CompostBag() {
        Common.init();
        var modBus = FMLJavaModLoadingContext.get().getModEventBus();

        modBus.addListener(this::commonSetup);
        modBus.addListener(this::clientSetup);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.commonSpec);

        Registry.register(modBus);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            DispenserBlock.registerBehavior(Registry.COMPOST_BAG.get(), Registry.COMPOST_BAG.get().DISPENSE_BEHAVIOR);
        });
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemProperties.register(Registry.COMPOST_BAG.get(), new ResourceLocation(Constants.MOD_ID, "filled"), (stack, level, living, id) -> {
                return CompostBagItem.getFullnessDisplay(stack);
            });
            MinecraftForgeClient.registerTooltipComponentFactory(CompostBagTooltip.class, ClientCompostBagTooltip::new);
        });
    }
    private void onItemTooltip(ItemTooltipEvent event) {
        
        Common.onItemTooltip(event.getItemStack(), event.getFlags(), event.getToolTip());
    }
}