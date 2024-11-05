package symbolics.division.armistice.mecha.schematic;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Range;
import symbolics.division.armistice.mecha.HullPart;
import symbolics.division.armistice.registry.ArmisticeRegistries;

import java.util.List;
import java.util.function.Function;

public record HullSchematic(
	@Range(from = 1, to = 3) int tier,
	List<Integer> slots,
	HeatData heat,
	ResourceLocation id
) implements Schematic<HullSchematic, HullPart> {
	public static final Codec<HullSchematic> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.INT.fieldOf("tier").forGetter(HullSchematic::tier),
		Codec.INT.listOf().fieldOf("slots").forGetter(HullSchematic::slots),
		HeatData.CODEC.fieldOf("heat").forGetter(HullSchematic::heat),
		ResourceLocation.CODEC.fieldOf("id").forGetter(HullSchematic::id)
	).apply(instance, HullSchematic::new));

	public static final Function<RegistryAccess, Codec<HullSchematic>> REGISTRY_CODEC = access -> access.registryOrThrow(ArmisticeRegistries.HULL_KEY).byNameCodec();

	public static final StreamCodec<RegistryFriendlyByteBuf, HullSchematic> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.VAR_INT,
		HullSchematic::tier,
		ByteBufCodecs.VAR_INT.apply(ByteBufCodecs.list()),
		HullSchematic::slots,
		HeatData.STREAM_CODEC,
		HullSchematic::heat,
		ResourceLocation.STREAM_CODEC,
		HullSchematic::id,
		HullSchematic::new
	);

	@Override
	public HullPart make() {
		return new HullPart(this);
	}

	@Override
	public Codec<HullSchematic> registryCodec(RegistryAccess access) {
		return REGISTRY_CODEC.apply(access);
	}

	@Override
	public StreamCodec<? extends ByteBuf, HullSchematic> streamCodec() {
		return STREAM_CODEC;
	}
}
