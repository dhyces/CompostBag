package dhyces.compostbag.platform.services;

import net.minecraft.world.item.ItemStack;

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

    public interface ItemHelper {
        public ItemStack copyWithSize(ItemStack stack, int size);
    }

    public interface IConfig {
        public Supplier<Integer> maxBonemeal();
    }
}
