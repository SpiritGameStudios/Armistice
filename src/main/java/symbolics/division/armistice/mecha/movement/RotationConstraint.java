package symbolics.division.armistice.mecha.movement;

import org.joml.Matrix4f;
import org.joml.Matrix4fc;

// values in radians
public record RotationConstraint(float minYaw, float maxYaw, float minPitch, float maxPitch, Matrix4fc frustum) {
	public static RotationConstraint of(float minYaw, float maxYaw, float minPitch, float maxPitch, float length) {
		return new RotationConstraint(
			minYaw,
			maxYaw,
			minPitch,
			maxPitch,
			new Matrix4f().setPerspective(
				minYaw + maxYaw,
				(minPitch + maxPitch) / (minYaw + maxYaw),
				length,
				9999
			)
//			new Matrix4f().perspectiveOffCenterFov(
//				minYaw,
//				maxYaw,
//				minPitch,
//				maxPitch,
//				length,
//				999
//			)
		);
	}

	public static RotationConstraint of(float yaw, float pitch, float length) {
		return of(yaw, yaw, pitch, pitch, length);
	}

	public static RotationConstraint of(float radians, float length) {
		return of(radians, radians, length);
	}
}
