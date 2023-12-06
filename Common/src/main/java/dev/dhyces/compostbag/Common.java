package dev.dhyces.compostbag;

import dev.dhyces.compostbag.item.CompostBagItem;
import dev.dhyces.compostbag.platform.Services;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

public class Common {

    public static final Supplier<Item> COMPOST_BAG_ITEM = Services.PLATFORM.registerItem(BuiltInRegistries.ITEM, "compost_bag", CompostBagItem::new);

    public static final Logger LOGGER = LogManager.getLogger(Constants.MOD_ID);

    public static void init() {
    }

    public static ResourceLocation modLoc(String id) {
        return new ResourceLocation(Constants.MOD_ID, id);
    }
}