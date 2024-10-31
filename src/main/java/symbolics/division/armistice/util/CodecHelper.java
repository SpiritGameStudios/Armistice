package symbolics.division.armistice.util;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.Util;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import org.joml.Vector4d;

import java.util.List;
import java.util.UUID;

public final class CodecHelper {
	public static final Codec<UUID> UUID = Codec.stringResolver(java.util.UUID::toString, java.util.UUID::fromString);

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

