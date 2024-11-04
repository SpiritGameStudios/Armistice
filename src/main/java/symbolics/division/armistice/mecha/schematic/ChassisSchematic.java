package symbolics.division.armistice.mecha.schematic;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Range;
import symbolics.division.armistice.mecha.ChassisPart;
import symbolics.division.armistice.registry.ArmisticeRegistries;

public record ChassisSchematic(
	@Range(from = 1, to = 3) int tier,
	int minArmorLevel,
	int maxArmorLevel,
	double moveSpeed,
	ResourceLocation id
) implements Schematic<ChassisSchematic, ChassisPart> {
	public static final Codec<ChassisSchematic> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.INT.fieldOf("tier").forGetter(ChassisSchematic::tier),
		Codec.INT.fieldOf("min_armor").forGetter(ChassisSchematic::minArmorLevel),
		Codec.INT.fieldOf("max_armor").forGetter(ChassisSchematic::maxArmorLevel),
		Codec.DOUBLE.fieldOf("move_speed").forGetter(ChassisSchematic::moveSpeed),
		ResourceLocation.CODEC.fieldOf("id").forGetter(ChassisSchematic::id)
	).apply(instance, ChassisSchematic::new));

	@Override
	public ChassisPart make() {
		return new ChassisPart(this);
	}

	@Override
	public Codec<ChassisSchematic> registryCodec(RegistryAccess access) {
		return getCodec(access);
	}

	public static Codec<ChassisSchematic> getCodec(RegistryAccess access) {
		return access.registryOrThrow(ArmisticeRegistries.CHASSIS_KEY).byNameCodec();
	}
}
