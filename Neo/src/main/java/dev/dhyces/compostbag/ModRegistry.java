package dev.dhyces.compostbag;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModRegistry {
	public static final DeferredRegister<Item> REGISTER = DeferredRegister.create(BuiltInRegistries.ITEM, Constants.MOD_ID);

	public static void register(IEventBus bus) {
		REGISTER.register(bus);
	}
}
