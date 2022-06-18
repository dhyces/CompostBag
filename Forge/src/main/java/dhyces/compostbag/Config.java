package dhyces.compostbag;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import org.apache.commons.lang3.tuple.Pair;

public class Config {

    public static final Server SERVER;
    static final ForgeConfigSpec serverSpec;

    static {
        final Pair<Server, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Server::new);
        serverSpec = specPair.getRight();
        SERVER = specPair.getLeft();
    }

    public static class Server {

        public final IntValue MAX_BONEMEAL;

        public Server(ForgeConfigSpec.Builder builder) {
            builder.push("server");

            MAX_BONEMEAL = builder.comment("Max bonemeal in compost bag")
                    .defineInRange("maxBonemeal", () -> 128, 0, Integer.MAX_VALUE);
            builder.pop();
        }
    }
}
