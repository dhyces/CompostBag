package dev.dhyces.compostbag.platform;

import dev.dhyces.compostbag.ModRegistry;
import dev.dhyces.compostbag.platform.services.IPlatformHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ComposterBlock;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;

import java.util.function.Supplier;

public class NeoPlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "Neo";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return !FMLLoader.isProduction();
    }

    @Override
    public boolean isSideClient() {
        return FMLLoader.getDist().isClient();
    }

    @Override
    public float getCompostChance(ItemStack stack) {
        return ComposterBlock.getValue(stack);
    }

    @Override
    public <T> Holder<T> register(Registry<T> registry, String id, Supplier<T> obj) {
        return ModRegistry.getOrCreateDeferredRegistry(registry).register(id, obj);
    }

    @Override
    public boolean bonemeal(ItemStack stack, Level level, BlockPos blockPos, Player player) {
        return BoneMealItem.applyBonemeal(stack, level, blockPos, player);
    }
}
