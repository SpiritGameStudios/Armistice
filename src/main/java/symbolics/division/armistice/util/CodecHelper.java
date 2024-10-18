package symbolics.division.armistice.util;

import com.mojang.serialization.Codec;
import net.minecraft.Util;
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

