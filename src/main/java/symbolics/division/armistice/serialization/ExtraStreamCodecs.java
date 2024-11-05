package symbolics.division.armistice.serialization;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import org.joml.Vector3f;

public final class ExtraStreamCodecs {
	public static final StreamCodec<FriendlyByteBuf, HitResult> HIT_RESULT = StreamCodec.of(
		(buffer, value) -> {
			buffer.writeVec3(value.getLocation());
			buffer.writeEnum(value.getType());

			switch (value) {
				case BlockHitResult block -> {
					buffer.writeEnum(block.getDirection());
					buffer.writeBlockPos(block.getBlockPos());
					buffer.writeBoolean(block.isInside());
				}
				case EntityHitResult entity -> {
					buffer.writeInt(entity.getEntity().getId());
				}
				default -> {
				}
			}
		},
		buffer -> {
			Vec3 location = buffer.readVec3();
			HitResult.Type type = buffer.readEnum(HitResult.Type.class);

			return switch (type) {
				case BLOCK -> new BlockHitResult(
					location,
					buffer.readEnum(Direction.class),
					buffer.readBlockPos(),
					buffer.readBoolean()
				);
				case ENTITY -> new PartialEntityHitResult(
					buffer.readInt(),
					location
				);
				default -> throw new IllegalArgumentException();
			};
		}
	);

	public static final StreamCodec<ByteBuf, Vector2f> VECTOR2F = new StreamCodec<>() {
		@NotNull
		public Vector2f decode(@NotNull ByteBuf buf) {
			return new Vector2f(buf.readFloat(), buf.readFloat());
		}

		public void encode(@NotNull ByteBuf buf, @NotNull Vector2f vec) {
			buf.writeFloat(vec.x);
			buf.writeFloat(vec.y);
		}
	};

	public static final StreamCodec<ByteBuf, Vec3> VEC3 = ByteBufCodecs.VECTOR3F.map(
		Vec3::new,
		vec -> new Vector3f((float) vec.x(), (float) vec.y(), (float) vec.z())
	);

	public static final StreamCodec<ByteBuf, java.util.UUID> UUID = ByteBufCodecs.STRING_UTF8.map(
		java.util.UUID::fromString,
		java.util.UUID::toString
	);

	private ExtraStreamCodecs() {
	}
}
