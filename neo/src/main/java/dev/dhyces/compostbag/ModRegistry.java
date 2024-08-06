package dev.dhyces.compostbag;

import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.core.Registry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Map;

public class ModRegistry {
	private static final Map<Registry<?>, DeferredRegister<?>> REGISTRIES = new Reference2ObjectOpenHashMap<>();

	@SuppressWarnings("unchecked")
	public static <T> DeferredRegister<T> getOrCreateDeferredRegistry(Registry<T> registry) {
		return (DeferredRegister<T>) REGISTRIES.computeIfAbsent(registry, objects -> DeferredRegister.create(registry, CompostBag.MODID));
	}

	public static void register(IEventBus bus) {
		REGISTRIES.values().forEach(deferredRegister -> deferredRegister.register(bus));
	}
}
