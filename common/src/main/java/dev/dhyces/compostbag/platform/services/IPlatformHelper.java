package dev.dhyces.compostbag.platform.services;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.function.Supplier;

public interface IPlatformHelper {

    /**
     * Gets the name of the current platform
     *
     * @return The name of the current platform.
     */
    String getPlatformName();

    /**
     * Checks if a mod with the given id is loaded.
     *
     * @param modId The mod to check if it is loaded.
     * @return True if the mod is loaded, false otherwise.
     */
    boolean isModLoaded(String modId);

    /**
     * Check if the game is currently in a development environment.
     *
     * @return True if in a development environment, false otherwise.
     */
    boolean isDevelopmentEnvironment();

    boolean isSideClient();

    float getCompostChance(ItemStack stack);

    default Holder<Item> registerItem(String id, Supplier<Item> obj) {
        return register(BuiltInRegistries.ITEM, id, obj);
    }

    <T> Holder<T> register(Registry<T> registry, String id, Supplier<T> obj);

    boolean bonemeal(ItemStack stack, Level level, BlockPos blockPos, Player player);
}
