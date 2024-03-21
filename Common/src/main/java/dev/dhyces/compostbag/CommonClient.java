package dev.dhyces.compostbag;

import dev.dhyces.compostbag.item.CompostBagItem;
import dev.dhyces.compostbag.util.Ticker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class CommonClient {
    static final Ticker TICKER = new Ticker(10, 3);

    public static Ticker getTickerInstance() {
        return TICKER;
    }

    public static float bonemealFullness(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed) {
        return CompostBagItem.getBonemealCount(stack) / (float)stack.getOrDefault(Common.MAX_BONEMEAL_COUNT.value(), 1);
    }
}
