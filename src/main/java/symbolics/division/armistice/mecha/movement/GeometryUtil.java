package symbolics.division.armistice.mecha.movement;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public final class GeometryUtil {
	private GeometryUtil() {
		throw new UnsupportedOperationException("Cannot instantiate utility class");
	}

	public static Vector3f rotatePitch(Vector3f v, float rad) {
		var axis = new Vector3f(v.x, 0, v.z).cross(v).normalize();
		return new Vector3f(v).rotateAxis(rad, axis.x, axis.y, axis.z);
	}




	public static Vec2 dir2Rad(Vec3 dir) {
		// yaw (x: positive z, y: positive x), pitch (x: horizontal length, y: positive y)
		return new Vec2((float) Math.atan(dir.x / dir.z), (float) Math.atan(dir.y / Math.sqrt(dir.x * dir.x + dir.z * dir.z)));
	}

	public static double chord(double radians) {
		return 2 * Math.sin(radians / 2);
	}

	public static boolean inRange(Vec3 a, Vec3 b, double tolerance) {
		return a.distanceToSqr(b) <= tolerance * tolerance;
	}

	/**
	 * Gives a point on an up-down curve with a given flatness at the top.
	 *
	 * @param x a value from 0 to 1.
	 * @return a value from 0 to 1 on the curve evaluated at x.
	 */
	public static float easedCurve(float x) {
		return easedCurve(x, 3.5f);
	}

	public static float easedCurve(float x, float flatness) {
		return (float) (1.0 - Math.pow(Math.abs((x + 1) / 2), 2.5));
	}
}
