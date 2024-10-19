package symbolics.division.armistice.mecha.movement;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.List;


public class KinematicsSolver {
	final static double IK_TOLERANCE = 0.3;

	// FABRIK: A fast, iterative solver for the Inverse Kinematics problem
	// Aristidou and Lasenby, 2011
	// http://www.andreasaristidou.com/publications/papers/FABRIK.pdf
	// temp: HEAVILY WIP IF YOU COMMENT ON THIS SECTION YOU WAIVE YOUR RIGHT
	//       TO NOT BEING ANNIHILATED BY HAMA INDUSTRIES VOID ACTUATOR (C)
	public static void solve(Vec3 target, List<IKSegment> segments, double maxDist, Leggy leg) {
		final int maxIterations = 50;

		IKSegment[] seg = segments.toArray(IKSegment[]::new);
		// segment i has base at joint i and tip at joint i+1.
		Vec3[] joints = Leggy.jointsOf(segments).toArray(Vec3[]::new);
		if (joints.length < 1) return;
		final IKSegment root = seg[1]; // root segment that is allowed to move

		// define rotation plane normal unit vec
		Vec3 dirToTargetFromRoot = target.subtract(root.position()).normalize();
		Vec3 planeNormal = dirToTargetFromRoot.with(Direction.Axis.Y, 0).cross(new Vec3(0, 1, 0));
		// temp: debug render normal
		leg.rot_normal = planeNormal.scale(0.2);
		// if behind leg base, flip normal to correct it
		if (seg[0].direction().dot(dirToTargetFromRoot) < 0) planeNormal = planeNormal.scale(-1).normalize();
		// project all joints onto the plane
		for (int i = 0; i < segments.size(); i++) {
			joints[i] = planarProject(joints[i], target, planeNormal);
		}

		// distance between nodes[i] and nodes[i+1];
		double dist = target.distanceTo(root.position());

		// check if in reach
		if (dist > maxDist) { // unreachable target
			joints[1] = seg[0].endPosition();
			for (int i = 2; i <= joints.length - 2; i++) {
				// from the root, point each segment towards target as far as it goes.
				Vec3 relativeJointPos = joints[i].subtract(joints[i - 1]);
				Vec3 constrained = clampPlanarAngle(relativeJointPos, joints[i - 1].subtract(joints[i - 2]), planeNormal, -Math.PI / 4, Math.PI / 4);
				joints[i] = adjustRelative(joints[i - 1], constrained.add(joints[i - 1]), seg[i - 1].length());
			}
		} else { // reachable target
			double dif = joints[joints.length - 1].distanceTo(target);
			int iterations = maxIterations;
			Vec3 rootPos = joints[1];
			while (dif > IK_TOLERANCE && iterations > 0) {
				iterations--;
				// FORWARD PHASE
				// set end segment to target
				joints[joints.length - 1] = target;
				joints[joints.length - 2] = adjustRelative(joints[joints.length - 2], target, seg[joints.length - 2].length());

				// from the second to last segment, adjust the previous segment to the next backwards
				for (int i = joints.length - 3; i > 0; i--) {
					Vec3 relativeJointPos = joints[i].subtract(joints[i + 1]);
					Vec3 constrained = clampPlanarAngle(relativeJointPos, joints[i + 1].subtract(joints[i + 2]), planeNormal, -Math.PI / 4, Math.PI / 4);
					joints[i] = adjustRelative(joints[i + 1], constrained.add(joints[i + 1]), seg[i].length());
				}

				// BACKWARDS PHASE
				// reset root of segment 1 to touch end of segment 0
				joints[1] = rootPos;
				// for each joint after, restrict it to respect constraints of the tip it represents
				for (int i = 2; i <= joints.length - 1; i++) {
					Vec3 relativeJointPos = joints[i].subtract(joints[i - 1]);
					Vec3 constrained = clampPlanarAngle(relativeJointPos, joints[i - 1].subtract(joints[i - 2]), planeNormal, -Math.PI / 4, Math.PI / 4);
					joints[i] = adjustRelative(joints[i - 1], constrained.add(joints[i - 1]), seg[i - 1].length());
				}


			}
		}

		for (int i = 1; i <= joints.length - 2; i++) {
			seg[i].setPosition(joints[i]);
			seg[i].setDirection(joints[i + 1].subtract(joints[i]));
		}
	}

	// returns a point on the line between <a> and <b> that is exactly <offset> distance from <a>
	public static Vec3 adjustRelative(Vec3 a, Vec3 b, double offset) {
		double li = offset / a.distanceTo(b);
		double li1 = 1 - li;
		return a.scale(li1).add(b.scale(li));
	}

	// projects point onto the plane intersecting anchor with unit vector normal
	private static Vec3 planarProject(Vec3 point, Vec3 anchor, Vec3 unitNormal) {
		return point.subtract(unitNormal.scale(point.subtract(anchor).dot(unitNormal)));
	}

	/**
	 * @param r        any vector
	 * @param dir      another vector coplanar to r
	 * @param norm     UNIT vector normal to the plane r and dir are on
	 * @param minAngle minimum angle in radians
	 * @param maxAngle maximum angle in radians
	 * @return a vector with angle from dir towards r around norm clamped between minAngle and maxAngle
	 */
	public static Vec3 clampPlanarAngle(Vec3 r, Vec3 dir, Vec3 norm, double minAngle, double maxAngle) {
		// give a vector r, a coplanar vector dir, and a normal vector,
		// constrain r so that its within min and max angle (radians)
		double dp = dir.dot(r);
		Vec3 dxr = dir.cross(r);

		// projection of p onto dir
		Vec3 jx = dir.scale(dp / dir.lengthSqr());
		Vec3 jy = r.subtract(jx);

		// calc and clamp theta if needed
		double yh = Math.signum(norm.dot(dxr)) * jy.length();
		double xh = Math.signum(dp) * jx.length();
		double theta = Math.atan2(yh, xh);
		double theta2 = Math.clamp(theta, minAngle, maxAngle);
		if (theta == theta2) return r;

		// otherwise, produce new orthogonal downscaled appropriately.
		double length = r.length();
		var fixed = new Vector3f(dir.toVector3f()).rotateAxis((float) theta2, (float) norm.x, (float) norm.y, (float) norm.z).normalize((float) length);
		return new Vec3(fixed);
	}
}
