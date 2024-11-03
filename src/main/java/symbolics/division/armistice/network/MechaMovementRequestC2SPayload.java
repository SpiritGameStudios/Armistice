package symbolics.division.armistice.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import symbolics.division.armistice.Armistice;
import symbolics.division.armistice.mecha.MechaEntity;

public record MechaMovementRequestC2SPayload(Vector3f pos) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<MechaMovementRequestC2SPayload> TYPE = new CustomPacketPayload.Type<>(Armistice.id("movement_request"));
	public static final StreamCodec<ByteBuf, MechaMovementRequestC2SPayload> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.VECTOR3F,
		payload -> payload.pos,
		MechaMovementRequestC2SPayload::new
	);

	public static void receive(MechaMovementRequestC2SPayload payload, IPayloadContext context) {
		if (context.player().getVehicle() instanceof MechaEntity mecha) {
			mecha.core().setPathingTarget(payload.pos);
		}
	}

	@Override
	@NotNull
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
