package dev.dhyces.compostbag.platform;

import dev.dhyces.compostbag.CompostBag;
import dev.dhyces.compostbag.Constants;
import dev.dhyces.compostbag.platform.services.IPlatformHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

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
    public Supplier<Item> registerItem(Registry<Item> registry, String id, Supplier<Item> obj) {
        var o = obj.get();
        Registry.register(registry, new ResourceLocation(Constants.MOD_ID, id), o);
        return () -> o;
    }

    @Override
    public ItemStack copyWithSize(ItemStack stack, int size) {
        var copy = stack.copy();
        copy.setCount(size);
        return copy;
    }

    @Override
    public boolean bonemeal(ItemStack stack, Level level, BlockPos blockPos, Player player) {
        return BoneMealItem.growCrop(stack, level, blockPos);
    }

    @Override
    public Supplier<Integer> maxBonemeal() {
            return () -> CompostBag.MAX_BONEMEAL;
        }
}
