package symbolics.division.armistice.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import symbolics.division.armistice.Armistice;
import symbolics.division.armistice.mecha.MechaEntity;
import symbolics.division.armistice.mecha.ordnance.HitscanGunOrdnance;
import symbolics.division.armistice.serialization.ExtraStreamCodecs;
import symbolics.division.armistice.serialization.PartialEntityHitResult;

public record OrdnanceHitscanS2CPayload(int mechaId, int ordnance,
										HitResult target, Vector3f source) implements CustomPacketPayload {
	public static final StreamCodec<FriendlyByteBuf, OrdnanceHitscanS2CPayload> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.VAR_INT,
		OrdnanceHitscanS2CPayload::mechaId,
		ByteBufCodecs.VAR_INT,
		OrdnanceHitscanS2CPayload::ordnance,
		ExtraStreamCodecs.HIT_RESULT,
		OrdnanceHitscanS2CPayload::target,
		ByteBufCodecs.VECTOR3F,
		OrdnanceHitscanS2CPayload::source,
		OrdnanceHitscanS2CPayload::new
	);

	public static final Type<OrdnanceHitscanS2CPayload> TYPE = new Type<>(Armistice.id("ordnance_hitscan"));

	@NotNull
	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void receive(OrdnanceHitscanS2CPayload payload, IPayloadContext context) {
		Entity entity = context.player().level().getEntity(payload.mechaId);
		if (!(entity instanceof MechaEntity mecha) || !(mecha.core().ordnance().get(payload.ordnance) instanceof HitscanGunOrdnance))
			return;
		HitResult result = payload.target;
		if (result instanceof PartialEntityHitResult partial) result = partial.finish(context.player().level());

//		HitscanBullet.create(context.player().level(), new Vec3(payload.source()), payload.target.getLocation(), 20);
	}
}
