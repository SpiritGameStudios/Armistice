package symbolics.division.armistice.mecha.schematic;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.Range;
import symbolics.division.armistice.mecha.ChassisPart;

public record ChassisSchematic(
	@Range(from = 1, to = 3) int tier,
	int minArmorLevel,
	int maxArmorLevel
) implements Schematic<ChassisSchematic, ChassisPart> {
	public static final Codec<ChassisSchematic> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.intRange(1, 3).fieldOf("tier").forGetter(ChassisSchematic::tier),
		Codec.INT.fieldOf("minArmorLevel").forGetter(ChassisSchematic::minArmorLevel),
		Codec.INT.fieldOf("maxArmorLevel").forGetter(ChassisSchematic::maxArmorLevel)
	).apply(instance, ChassisSchematic::new));

	@Override
	public ChassisPart make() {
		return null;
	}

	@Override
	public Codec<ChassisSchematic> codec() {
		return CODEC;
	}
}
