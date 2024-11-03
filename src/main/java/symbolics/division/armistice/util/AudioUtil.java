package symbolics.division.armistice.util;

import net.minecraft.util.RandomSource;


public final class AudioUtil {
	public static float randomizedPitch(RandomSource random, float base, float maxDelta) {
		return base + (random.nextFloat() * 2 - 1) * maxDelta;
	}
}
