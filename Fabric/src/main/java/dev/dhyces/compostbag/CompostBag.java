package dev.dhyces.compostbag;

import dev.dhyces.compostbag.item.CompostBagItem;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;

public class CompostBag implements ModInitializer {

    @Override
    public void onInitialize() {
        Common.init();

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(entries -> entries.accept(new ItemStack(Common.COMPOST_BAG_ITEM.value())));
        DispenserBlock.registerBehavior(Common.COMPOST_BAG_ITEM.value(), CompostBagItem.DISPENSE_BEHAVIOR);
    }
}
