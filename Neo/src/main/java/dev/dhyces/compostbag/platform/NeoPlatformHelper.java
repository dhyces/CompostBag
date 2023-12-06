package dev.dhyces.compostbag.platform;

import dev.dhyces.compostbag.Config;
import dev.dhyces.compostbag.ModRegistry;
import dev.dhyces.compostbag.platform.services.IPlatformHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.items.ItemHandlerHelper;

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
    public Supplier<Item> registerItem(String id, Supplier<Item> obj) {
        return ModRegistry.REGISTER.register(id, obj);
    }


    @Override
    public ItemStack copyWithSize(ItemStack stack, int size) {
        return ItemHandlerHelper.copyStackWithSize(stack, size);
    }

    @Override
    public boolean bonemeal(ItemStack stack, Level level, BlockPos blockPos, Player player) {
        return BoneMealItem.applyBonemeal(stack, level, blockPos, player);
    }

    @Override
    public Supplier<Integer> maxBonemeal() {
        return Config.SERVER.MAX_BONEMEAL;
    }
}
