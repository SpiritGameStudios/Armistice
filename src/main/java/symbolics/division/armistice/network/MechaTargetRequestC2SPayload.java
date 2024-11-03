package symbolics.division.armistice.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;
import symbolics.division.armistice.Armistice;
import symbolics.division.armistice.mecha.MechaEntity;
import symbolics.division.armistice.mecha.OrdnancePart;
import symbolics.division.armistice.util.CodecHelper;
import symbolics.division.armistice.util.PartialEntityHitResult;

public record MechaTargetRequestC2SPayload(HitResult target, int ordnance) implements CustomPacketPayload {
	public static final Type<MechaTargetRequestC2SPayload> TYPE = new Type<>(Armistice.id("mecha_target"));

	public static final StreamCodec<FriendlyByteBuf, MechaTargetRequestC2SPayload> STREAM_CODEC = StreamCodec.composite(
		CodecHelper.HIT_RESULT,
		MechaTargetRequestC2SPayload::target,
		ByteBufCodecs.INT,
		MechaTargetRequestC2SPayload::ordnance,
		MechaTargetRequestC2SPayload::new
	);

	@NotNull
	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void receive(MechaTargetRequestC2SPayload payload, IPayloadContext context) {
		if (!(context.player().getVehicle() instanceof MechaEntity mecha)) return;
		HitResult result = payload.target;
		if (result instanceof PartialEntityHitResult partial) result = partial.finish(context.player().level());

		OrdnancePart part = mecha.core().ordnance().get(payload.ordnance());
		if (!part.startTargeting(result)) {

		}
	}
}
