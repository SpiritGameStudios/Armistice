package symbolics.division.armistice.util;

import com.mojang.serialization.Codec;

public final class CodecHelper {
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

