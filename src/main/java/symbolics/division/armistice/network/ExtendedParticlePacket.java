package symbolics.division.armistice.network;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;
import symbolics.division.armistice.Armistice;
import symbolics.division.armistice.serialization.ExtraStreamCodecs;

// Thank you Tomate0613
public record ExtendedParticlePacket(
	Vec3 origin,
	Vec3 posVariation,
	Vec3 velocity,
	Vec3 velocityVariation,
	int count,
	ParticleOptions particleType
) implements CustomPacketPayload {
	public static final StreamCodec<RegistryFriendlyByteBuf, ExtendedParticlePacket> STREAM_CODEC = StreamCodec.composite(
		ExtraStreamCodecs.VEC3,
		ExtendedParticlePacket::origin,
		ExtraStreamCodecs.VEC3,
		ExtendedParticlePacket::posVariation,
		ExtraStreamCodecs.VEC3,
		ExtendedParticlePacket::velocity,
		ExtraStreamCodecs.VEC3,
		ExtendedParticlePacket::velocityVariation,
		ByteBufCodecs.INT,
		ExtendedParticlePacket::count,
		ParticleTypes.STREAM_CODEC,
		ExtendedParticlePacket::particleType,
		ExtendedParticlePacket::new
	);

	public static final Type<ExtendedParticlePacket> TYPE = new Type<>(Armistice.id("extended_particle"));

	@NotNull
	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	private static final RandomSource RANDOM = RandomSource.create();

	public static void receive(ExtendedParticlePacket packet, IPayloadContext context) {
		for (int i = 0; i < packet.count(); i++) {
			context.player().level().addParticle(
				packet.particleType(),
				false,
				packet.origin().x + RANDOM.nextGaussian() * packet.posVariation().x,
				packet.origin().y + RANDOM.nextGaussian() * packet.posVariation().y,
				packet.origin().z + RANDOM.nextGaussian() * packet.posVariation().z,
				packet.velocity().x + RANDOM.nextGaussian() * packet.velocityVariation().x,
				packet.velocity().y + RANDOM.nextGaussian() * packet.velocityVariation().y,
				packet.velocity().z + RANDOM.nextGaussian() * packet.velocityVariation().z
			);
		}
	}
}
