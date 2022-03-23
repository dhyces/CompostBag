package dhyces.compostbag;

import dhyces.compostbag.item.CompostBagItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class Registry {
	static final DeferredRegister<Item> REGISTER = DeferredRegister.create(ForgeRegistries.ITEMS, CompostBag.MODID);
	
	public static void register(IEventBus bus) {REGISTER.register(bus);}
	
	public static final RegistryObject<CompostBagItem> COMPOST_BAG;
	
	static {
		COMPOST_BAG = REGISTER.register("compost_bag", CompostBagItem::new);
	}
}
