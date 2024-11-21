package symbolics.division.armistice.client;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public record ArmisticeClientConfig(ModConfigSpec.DoubleValue flickerAmount) {
	public static final ArmisticeClientConfig INSTANCE;
	public static final ModConfigSpec SPEC;

	public static ArmisticeClientConfig create(ModConfigSpec.Builder builder) {
		return new ArmisticeClientConfig(
			builder.defineInRange("flicker_amount", 1.0, 0.0, 1.0)
		);
	}

	static {
		Pair<ArmisticeClientConfig, ModConfigSpec> pair =
			new ModConfigSpec.Builder().configure(ArmisticeClientConfig::create);

		INSTANCE = pair.getLeft();
		SPEC = pair.getRight();
	}
}
