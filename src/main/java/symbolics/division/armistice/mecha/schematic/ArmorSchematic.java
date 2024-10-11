package symbolics.division.armistice.mecha.schematic;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.Range;
import symbolics.division.armistice.mecha.ArmorPart;

public record ArmorSchematic(
	@Range(from = 1, to = 9) int size
) implements Schematic<ArmorSchematic, ArmorPart> {
	public static final Codec<ArmorSchematic> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.intRange(1, 9).fieldOf("size").forGetter(ArmorSchematic::size)
	).apply(instance, ArmorSchematic::new));

	@Override
	public ArmorPart make() {
		return null;
	}

	@Override
	public Codec<ArmorSchematic> codec() {
		return CODEC;
	}
}
