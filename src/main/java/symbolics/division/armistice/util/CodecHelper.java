package symbolics.division.armistice.util;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.Util;
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
import org.joml.Vector4d;

import java.util.List;
import java.util.UUID;

public final class CodecHelper {
	public static final Codec<UUID> UUID = Codec.stringResolver(java.util.UUID::toString, java.util.UUID::fromString);

	public static final StreamCodec<ByteBuf, UUID> UUID_STREAM = ByteBufCodecs.STRING_UTF8.map(
		java.util.UUID::fromString,
		java.util.UUID::toString
	);

	public static final StreamCodec<ByteBuf, Vec3> VEC3 = ByteBufCodecs.VECTOR3F.map(
		Vec3::new,
		vec -> new Vector3f((float) vec.x(), (float) vec.y(), (float) vec.z())
	);

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

	public static final Codec<Vector4d> VECTOR4D = Codec.DOUBLE
		.listOf()
		.comapFlatMap(
			array -> Util.fixedSize(array, 4)
				.map(vector -> new Vector4d(vector.getFirst(), vector.get(1), vector.get(2), vector.get(3))),
			vector -> List.of(vector.x(), vector.y(), vector.z(), vector.w())
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

	private CodecHelper() {
		throw new UnsupportedOperationException("Cannot instantiate utility class");
	}

	public static <T extends Enum<T>> Codec<T> enumCodec(Class<T> clazz) {
		return Codec.stringResolver(Enum::name, string -> Enum.valueOf(clazz, string));
	}

	public static Codec<Integer> clampedRange(int min, int max) {
		return Codec.INT.xmap(
			value -> Math.clamp(value, min, max),
			value -> Math.clamp(value, min, max)
		);
	}

	public static Codec<Float> clampedRange(float min, float max) {
		return Codec.FLOAT.xmap(
			value -> Math.clamp(value, min, max),
			value -> Math.clamp(value, min, max)
		);
	}

	public static Codec<Double> clampedRange(double min, double max) {
		return Codec.DOUBLE.xmap(
			value -> Math.clamp(value, min, max),
			value -> Math.clamp(value, min, max)
		);
	}
}

