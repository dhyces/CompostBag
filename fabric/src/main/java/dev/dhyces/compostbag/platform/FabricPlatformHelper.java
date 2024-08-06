package dev.dhyces.compostbag.platform;

import dev.dhyces.compostbag.CompostBag;
import dev.dhyces.compostbag.platform.services.IPlatformHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ComposterBlock;

import java.util.function.Supplier;

public class FabricPlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public boolean isSideClient() {
        return FabricLoader.getInstance().getEnvironmentType().equals(EnvType.CLIENT);
    }

    @Override
    public float getCompostChance(ItemStack stack) {
        return ComposterBlock.COMPOSTABLES.getFloat(stack.getItem());
    }

    @Override
    public <T> Holder<T> register(Registry<T> registry, String id, Supplier<T> obj) {
        return Registry.registerForHolder(registry, CompostBag.id(id), obj.get());
    }

    @Override
    public boolean bonemeal(ItemStack stack, Level level, BlockPos blockPos, Player player) {
        return BoneMealItem.growCrop(stack, level, blockPos);
    }
}
