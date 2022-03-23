package dhyces.compostbag;

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
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dhyces.compostbag.item.CompostBagItem;
import dhyces.compostbag.tooltip.ClientCompostBagTooltip;
import dhyces.compostbag.tooltip.CompostBagTooltip;

@Mod(CompostBag.MODID)
public class CompostBag {
	
	public static final String MODID = "compostbag";
	
	public static final Logger LOGGER = LogManager.getLogger(CompostBag.class);

    public CompostBag() {
    	FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
    	FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
    }
    
    private void commonSetup(final FMLCommonSetupEvent event) {
    	event.enqueueWork(() -> {
    		DispenserBlock.registerBehavior(RegistryEvents.COMPOST_BAG, RegistryEvents.COMPOST_BAG.DISPENSE_BEHAVIOR);
    	});
    }

    private void clientSetup(final FMLClientSetupEvent event) {
    	event.enqueueWork(() -> 
    		ItemProperties.register(RegistryEvents.COMPOST_BAG, new ResourceLocation(MODID, "filled"), (stack, level, living, id) -> {
	         return CompostBagItem.getFullnessDisplay(stack);
	      }));
    	MinecraftForgeClient.registerTooltipComponentFactory(CompostBagTooltip.class, ClientCompostBagTooltip::new);
    }
    
    @Mod.EventBusSubscriber(modid = MODID, bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
    	
    	public static CompostBagItem COMPOST_BAG;
    	
        @SubscribeEvent
        static void onItemsRegistry(final RegistryEvent.Register<Item> event) {
        	COMPOST_BAG = new CompostBagItem();
            event.getRegistry().register(COMPOST_BAG);
        }
    }
}
