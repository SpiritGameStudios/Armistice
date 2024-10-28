package symbolics.division.armistice.model;

import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import symbolics.division.armistice.math.GeometryUtil;

/**
 * Position, xyz rotation, and 3d direction unit vector
 */
public record Bone(Vec3 pos, Vec3 rot, Vec3 direction, Quaternionfc quat) {
	public static final Bone ZERO = new Bone(Vec3.ZERO, Vec3.ZERO, Vec3.ZERO, new Quaternionf(0, 0, 0, 0));

	/**
	 * Only works for top-level nodes, ie those directly under the root.
	 */
	public static Bone of(OutlinerNode node) {
		return new Bone(
			node.origin().scale(BBModelData.BASE_SCALE_FACTOR),
			node.rotation(),
			GeometryUtil.bbRot2Direction(node.rotation()),
			GeometryUtil.bbRot2Quaternion(node.rotation())
		);
	}
}
