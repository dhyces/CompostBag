package dev.dhyces.compostbag;

import com.mojang.serialization.Codec;
import dev.dhyces.compostbag.item.CompostBagItem;
import dev.dhyces.compostbag.platform.Services;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.UnaryOperator;

public class Common {
    public static final String MODID = "compostbag";
    public static ResourceLocation id(String id) {
        return new ResourceLocation(MODID, id);
    }
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    public static final Holder<DataComponentType<Integer>> MAX_BONEMEAL_COUNT = registerComponentType("max_bonemeal_count", integerBuilder -> integerBuilder.persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(ByteBufCodecs.VAR_INT));
    public static final Holder<DataComponentType<Integer>> BONEMEAL_COUNT = registerComponentType("bonemeal_count", integerBuilder -> integerBuilder.persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(ByteBufCodecs.VAR_INT));
    public static final Holder<DataComponentType<Integer>> MAX_COMPOST_LEVEL = registerComponentType("max_compost_level", integerBuilder -> integerBuilder.persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(ByteBufCodecs.VAR_INT));
    public static final Holder<DataComponentType<Integer>> COMPOST_LEVEL = registerComponentType("compost_level", integerBuilder -> integerBuilder.persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(ByteBufCodecs.VAR_INT));

    public static final Holder<Item> COMPOST_BAG_ITEM = Services.PLATFORM.registerItem("compost_bag", () -> new CompostBagItem(new Item.Properties().stacksTo(1).component(MAX_BONEMEAL_COUNT.value(), 128).component(MAX_COMPOST_LEVEL.value(), 7)));

    private static <T> Holder<DataComponentType<T>> registerComponentType(String id, UnaryOperator<DataComponentType.Builder<T>> builder) {
        return cast(Services.PLATFORM.register(BuiltInRegistries.DATA_COMPONENT_TYPE, id, () -> builder.apply(DataComponentType.builder()).build()));
    }

    private static <T> T cast(Object o) {
        return (T) o;
    }

    public static void init() {
    }
}