package dev.dhyces.compostbag;

import dev.dhyces.compostbag.item.CompostBagItem;
import dev.dhyces.compostbag.networking.Networking;
import dev.dhyces.compostbag.platform.Services;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;

public class CompostBag implements ModInitializer {

    public static final String CONFIG_DEFAULT_TEXT = """
                #Server
                #Max bonemeal in compost bag
                #in range [0,INT_MAX)
                max-bonemeal=128
                """;

    public static SimpleConfig CONFIG = makeConfig();
    public static int MAX_BONEMEAL;

    static SimpleConfig makeConfig() {
        return SimpleConfig.of(Constants.MOD_ID).provider(c -> CONFIG_DEFAULT_TEXT).request();
    }

    static int getConfigMaxBonemeal() {
        return CONFIG.getOrDefault("max-bonemeal", 128);
    }

    static void reloadConfig() {
        CompostBag.CONFIG = makeConfig();
        MAX_BONEMEAL = getConfigMaxBonemeal();
    }

    @Override
    public void onInitialize() {
        Common.init();
        Networking.init();

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(entries -> entries.accept(new ItemStack(Common.COMPOST_BAG_ITEM.get())));
        DispenserBlock.registerBehavior(Common.COMPOST_BAG_ITEM.get(), CompostBagItem.DISPENSE_BEHAVIOR);

        MAX_BONEMEAL = getConfigMaxBonemeal();

        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public ResourceLocation getFabricId() {
                return new ResourceLocation(Constants.MOD_ID, "config");
            }

            @Override
            public void onResourceManagerReload(ResourceManager resourceManager) {
                reloadConfig();

                Common.LOGGER.info("Config reloaded! max-bonemeal=" + Services.PLATFORM.maxBonemeal().get());
            }
        });
    }
}
