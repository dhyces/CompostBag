package dhyces.compostbag;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class Registry {
    public static final DeferredRegister<Item> REGISTER = DeferredRegister.create(ForgeRegistries.Keys.ITEMS, Constants.MOD_ID);

    public static void register(IEventBus bus) {
        REGISTER.register(bus);
    }
}
