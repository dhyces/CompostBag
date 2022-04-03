package dhyces.compostbag.platform;

import dhyces.compostbag.Config;
import dhyces.compostbag.platform.services.IPlatformHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.function.Supplier;

public class ForgePlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "Forge";
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
    public Supplier<Item> registerItem(Registry<Item> registry, String id, Supplier<Item> obj) {
        return dhyces.compostbag.Registry.REGISTER.register(id, obj);
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
        return () -> Config.COMMON.MAX_BONEMEAL.get();
    }
}
