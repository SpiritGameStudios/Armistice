package symbolics.division.armistice.mecha.schematic;

import com.mojang.serialization.Codec;
import org.jetbrains.annotations.Range;
import symbolics.division.armistice.mecha.HullPart;
import symbolics.division.armistice.registry.ArmisticeRegistries;

import java.util.List;

public record HullSchematic(
	@Range(from = 1, to = 3) int tier,
	List<Integer> slots
) implements Schematic<HullSchematic, HullPart> {
	public static final Codec<HullSchematic> REGISTRY_CODEC = ArmisticeRegistries.HULL.byNameCodec();

	@Override
	public HullPart make() {
		return null;
	}

	@Override
	public Codec<HullSchematic> codec() {
		return ArmisticeRegistries.HULL.byNameCodec();
	}
}
