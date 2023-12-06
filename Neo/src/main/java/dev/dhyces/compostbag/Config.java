package dev.dhyces.compostbag;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class Config {

	public static class Server {

		public final ModConfigSpec.IntValue MAX_BONEMEAL;

		public Server(ModConfigSpec.Builder builder) {
			builder.push("server");

			MAX_BONEMEAL = builder.comment("Max bonemeal in compost bag")
					.defineInRange("maxBonemeal", () -> 128, 0, Integer.MAX_VALUE);
			builder.pop();
		}
	}

	static final ModConfigSpec serverSpec;
    public static final Server SERVER;
    static {
        final Pair<Server, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(Server::new);
		serverSpec = specPair.getRight();
		SERVER = specPair.getLeft();
    }
}
