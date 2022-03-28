package dhyces.compostbag;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.blaze3d.platform.InputConstants;

import dhyces.compostbag.item.CompostBagItem;
import dhyces.compostbag.tooltip.ClientCompostBagTooltip;
import dhyces.compostbag.tooltip.CompostBagTooltip;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(CompostBag.MODID)
public class CompostBag {

	public static final String MODID = "compostbag";

    public CompostBag() {
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
    		ItemProperties.register(Registry.COMPOST_BAG.get(), new ResourceLocation(MODID, "filled"), (stack, level, living, id) -> {
    				return CompostBagItem.getFullnessDisplay(stack);
    			});
    		MinecraftForgeClient.registerTooltipComponentFactory(CompostBagTooltip.class, ClientCompostBagTooltip::new);
    	});
    }
}
