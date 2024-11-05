package symbolics.division.armistice.mecha.schematic;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Range;
import symbolics.division.armistice.mecha.ChassisPart;
import symbolics.division.armistice.registry.ArmisticeRegistries;

import java.util.function.Function;

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

	public static final Function<RegistryAccess, Codec<ChassisSchematic>> REGISTRY_CODEC = access -> access.registryOrThrow(ArmisticeRegistries.CHASSIS_KEY).byNameCodec();

	public static final StreamCodec<ByteBuf, ChassisSchematic> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.VAR_INT,
		ChassisSchematic::tier,
		ByteBufCodecs.VAR_INT,
		ChassisSchematic::minArmorLevel,
		ByteBufCodecs.VAR_INT,
		ChassisSchematic::maxArmorLevel,
		ByteBufCodecs.DOUBLE,
		ChassisSchematic::moveSpeed,
		ResourceLocation.STREAM_CODEC,
		ChassisSchematic::id,
		ChassisSchematic::new
	);

	@Override
	public ChassisPart make() {
		return new ChassisPart(this);
	}

	@Override
	public Codec<ChassisSchematic> registryCodec(RegistryAccess access) {
		return REGISTRY_CODEC.apply(access);
	}

	@Override
	public StreamCodec<? extends ByteBuf, ChassisSchematic> streamCodec() {
		return STREAM_CODEC;
	}
}
