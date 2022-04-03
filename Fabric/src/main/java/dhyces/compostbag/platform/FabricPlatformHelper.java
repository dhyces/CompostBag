package dhyces.compostbag.platform;

import dhyces.compostbag.Constants;
import dhyces.compostbag.platform.services.IPlatformHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
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
    public Supplier<Item> registerItem(Registry<Item> registry, String id, Supplier<Item> obj) {
        var o = obj.get();
        Registry.register(registry, new ResourceLocation(Constants.MOD_ID, "compost_bag"), o);
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
            return () -> 128;
        }
}
