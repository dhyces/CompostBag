package dhyces.compostbag.platform;

import dhyces.compostbag.Constants;
import dhyces.compostbag.platform.services.IPlatformHelper;

import java.util.ServiceLoader;

public class Services {

    public static final IPlatformHelper PLATFORM = load(IPlatformHelper.class);
    public static final IPlatformHelper.ItemHelper ITEM_HELPER = load(IPlatformHelper.ItemHelper.class);
    public static final IPlatformHelper.IConfig CONFIG = load(IPlatformHelper.IConfig.class);

    public static <T> T load(Class<T> clazz) {

        final T loadedService = ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        return loadedService;
    }
}
