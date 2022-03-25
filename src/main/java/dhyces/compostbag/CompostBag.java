package dhyces.compostbag;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.blaze3d.platform.InputConstants;

import dhyces.compostbag.item.CompostBagItem;
import dhyces.compostbag.tooltip.ClientCompostBagTooltip;
import dhyces.compostbag.tooltip.CompostBagTooltip;

// TODO: remove me, im temporary. Put a breakpoint on com.electronwill.nightconfig.core.utils.IntDeque#addLast and change the config in npp.
// it should result in a "config incorrect. Correcting".
@Mod(CompostBag.MODID)
public class CompostBag {
	
	public static final String MODID = "compostbag";
	
	public static final Logger LOGGER = LogManager.getLogger(CompostBag.class);
	
	public static final KeyMapping SHOW_TOOLTIP = new KeyMapping("key.compostbag.showTooltip", InputConstants.KEY_LCONTROL, "key.categories.compostbag");

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
    		ClientRegistry.registerKeyBinding(SHOW_TOOLTIP);
    	});
    }
}
