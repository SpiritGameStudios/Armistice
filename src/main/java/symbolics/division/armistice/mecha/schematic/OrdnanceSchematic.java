package symbolics.division.armistice.mecha.schematic;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import symbolics.division.armistice.mecha.OrdnancePart;
import symbolics.division.armistice.registry.ArmisticeRegistries;

import java.util.function.Supplier;

public record OrdnanceSchematic(
	int size,
	Supplier<OrdnancePart> supplier
) implements Schematic<OrdnanceSchematic, OrdnancePart> {
	public static final Codec<OrdnanceSchematic> REGISTRY_CODEC = ArmisticeRegistries.ORDNANCE.byNameCodec();
	public static final StreamCodec<RegistryFriendlyByteBuf, OrdnanceSchematic> REGISTRY_STREAM_CODEC = ByteBufCodecs.registry(ArmisticeRegistries.ORDNANCE_KEY);

	@Override
	public OrdnancePart make() {
		OrdnancePart part = supplier.get();
		part.setId(id());

		return part;
	}

	@Override
	public Codec<OrdnanceSchematic> registryCodec(RegistryAccess access) {
		return REGISTRY_CODEC;
	}

	@Override
	public StreamCodec<? extends ByteBuf, OrdnanceSchematic> streamCodec() {
		return REGISTRY_STREAM_CODEC;
	}

	public ResourceLocation id() {
		return ArmisticeRegistries.ORDNANCE.getKey(this);
	}
}
