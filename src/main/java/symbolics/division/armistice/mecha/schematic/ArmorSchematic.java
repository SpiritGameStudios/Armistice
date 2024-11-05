package symbolics.division.armistice.mecha.schematic;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Range;
import symbolics.division.armistice.mecha.ArmorPart;
import symbolics.division.armistice.registry.ArmisticeRegistries;

import java.util.function.Function;

public record ArmorSchematic(
	@Range(from = 1, to = 9) int size,
	double plating,
	ResourceLocation id
) implements Schematic<ArmorSchematic, ArmorPart> {
	public static final Codec<ArmorSchematic> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.INT.fieldOf("size").forGetter(ArmorSchematic::size),
		Codec.DOUBLE.fieldOf("plating").forGetter(ArmorSchematic::plating),
		ResourceLocation.CODEC.fieldOf("id").forGetter(ArmorSchematic::id)
	).apply(instance, ArmorSchematic::new));

	public static final Function<RegistryAccess, Codec<ArmorSchematic>> REGISTRY_CODEC = access -> access.registryOrThrow(ArmisticeRegistries.ARMOR_KEY).byNameCodec();

	public static final StreamCodec<ByteBuf, ArmorSchematic> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.VAR_INT,
		ArmorSchematic::size,
		ByteBufCodecs.DOUBLE,
		ArmorSchematic::plating,
		ResourceLocation.STREAM_CODEC,
		ArmorSchematic::id,
		ArmorSchematic::new
	);

	@Override
	public ArmorPart make() {
		return new ArmorPart(this);
	}

	@Override
	public Codec<ArmorSchematic> registryCodec(RegistryAccess access) {
		return REGISTRY_CODEC.apply(access);
	}

	@Override
	public StreamCodec<ByteBuf, ArmorSchematic> streamCodec() {
		return STREAM_CODEC;
	}
}
