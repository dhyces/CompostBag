package dhyces.compostbag;

import dhyces.compostbag.item.CompostBagItem;
import dhyces.compostbag.platform.Services;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

public class Common {

    public static final Supplier<Item> COMPOST_BAG_ITEM;

    public static final Logger LOGGER = LogManager.getLogger(Constants.MOD_ID);

    static {
        COMPOST_BAG_ITEM = Services.PLATFORM.registerItem(Registry.ITEM, "compost_bag", () -> new CompostBagItem());
    }

    public static void init() {
    }

    public static ResourceLocation modLoc(String id) {
        return new ResourceLocation(Constants.MOD_ID, id);
    }
}