package symbolics.division.armistice.mecha.schematic;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.Range;
import symbolics.division.armistice.mecha.HullPart;

public record HullSchematic(
	@Range(from = 1, to = 3) int tier
) implements Schematic<HullSchematic, HullPart> {
	public static final Codec<HullSchematic> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.intRange(1, 3).fieldOf("tier").forGetter(HullSchematic::tier)
	).apply(instance, HullSchematic::new));

	@Override
	public HullPart make() {
		return null;
	}

	@Override
	public Codec<HullSchematic> codec() {
		return CODEC;
	}
}
