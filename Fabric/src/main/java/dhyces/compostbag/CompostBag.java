package dhyces.compostbag;

import dhyces.compostbag.item.CompostBagItem;
import dhyces.compostbag.platform.Services;
import dhyces.compostbag.util.Ticker;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.block.DispenserBlock;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class CompostBag implements ModInitializer {

    public static SimpleConfig CONFIG = makeConfig();
    public static int MAX_BONEMEAL;

    static SimpleConfig makeConfig() {
        return SimpleConfig.of(Constants.MOD_ID).provider(c -> "#Server\n#Max bonemeal in compost bag\n#in range [0,INT_MAX)\nmax-bonemeal=128\n").request();
    }

    @Override
    public void onInitialize() {
        Common.init();
        DispenserBlock.registerBehavior(Common.COMPOST_BAG_ITEM.get(), CompostBagItem.DISPENSE_BEHAVIOR);
        MAX_BONEMEAL = CONFIG.getOrDefault("max-bonemeal", 128);
        //TODO: finish config and syncing
        ServerPlayConnectionEvents.JOIN.register(Event.DEFAULT_PHASE, (handler, sender, server) -> {
        });
        ServerPlayConnectionEvents.DISCONNECT.register(Event.DEFAULT_PHASE, (handler, server) -> {

        });
        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public ResourceLocation getFabricId() {
                return new ResourceLocation(Constants.MOD_ID, "config");
            }

            @Override
            public void onResourceManagerReload(ResourceManager resourceManager) {
                CompostBag.CONFIG = makeConfig();
                Common.LOGGER.info("Config reloaded! max-bonemeal=" + Services.PLATFORM.maxBonemeal().get());
            }
        });
    }


}
