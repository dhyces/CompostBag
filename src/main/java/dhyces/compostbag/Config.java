package dhyces.compostbag;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;

public class Config {


	public static class Common {

		public final IntValue MAX_BONEMEAL;

		public Common(ForgeConfigSpec.Builder builder) {
			builder.push("common");

			MAX_BONEMEAL = builder.comment("Max bonemeal in compost bag")
					.defineInRange("maxBonemeal", () -> 128, 0, Integer.MAX_VALUE);
			builder.pop();
		}
	}

	static final ForgeConfigSpec commonSpec;
    public static final Common COMMON;
    static {
        final Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
        commonSpec = specPair.getRight();
        COMMON = specPair.getLeft();
    }
}
