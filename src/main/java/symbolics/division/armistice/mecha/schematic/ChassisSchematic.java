package symbolics.division.armistice.mecha.schematic;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Range;
import symbolics.division.armistice.mecha.ChassisPart;
import symbolics.division.armistice.registry.ArmisticeRegistries;

public record ChassisSchematic(
	@Range(from = 1, to = 3) int tier,
	int minArmorLevel,
	int maxArmorLevel
) implements Schematic<ChassisSchematic, ChassisPart> {
	public static final Codec<ChassisSchematic> REGISTRY_CODEC = ArmisticeRegistries.CHASSIS.byNameCodec();

	@Override
	public ChassisPart make() {
		return null;
	}

	@Override
	public Codec<ChassisSchematic> codec() {
		return ArmisticeRegistries.CHASSIS.byNameCodec();
	}

	public ResourceLocation id() {
		return ArmisticeRegistries.CHASSIS.getKey(this);
	}
}
