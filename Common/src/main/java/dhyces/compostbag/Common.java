package dhyces.compostbag;

import dhyces.compostbag.item.CompostBagItem;
import dhyces.compostbag.platform.Services;
import net.minecraft.core.Registry;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

public class Common {

    public static final Supplier<Item> COMPOST_BAG_ITEM;

    public static void init() {
    }

    static {
        COMPOST_BAG_ITEM = Services.PLATFORM.registerItem(Registry.ITEM, "compost_bag", () -> new CompostBagItem());
    }
}