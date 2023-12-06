package dev.dhyces.compostbag.platform.services;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
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

    Supplier<Item> registerItem(Registry<Item> registry, String id, Supplier<Item> obj);

    public ItemStack copyWithSize(ItemStack stack, int size);

    public boolean bonemeal(ItemStack stack, Level level, BlockPos blockPos, Player player);

    public Supplier<Integer> maxBonemeal();
}
