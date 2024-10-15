package symbolics.division.armistice.mecha.movement;

import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.List;


public class KinematicsSolver {
	final static double IK_TOLERANCE = 0.3;


	// FABRIK: A fast, iterative solver for the Inverse Kinematics problem
	// Aristidou and Lasenby, 2011
	// http://www.andreasaristidou.com/publications/papers/FABRIK.pdf
	// does NOT modify rotation/direction of segment 0.
	public static void solve(Vec3 target, List<IKSegment> segments, double maxDist, Vec3 refDir) {
		final int maxIterations = 50;
		Vector3f ref = refDir.toVector3f();

		Vec3 tilt = new Vec3(0, 10, 0).add(target.subtract(segments.getFirst().position()).normalize().scale(1)).normalize();
		for (int i = 1; i < segments.size(); i++) {
			segments.get(i).setDirection(tilt);
			segments.get(i).setPosition(segments.get(i - 1).endPosition());
		}

		IKSegment[] seg = segments.toArray(IKSegment[]::new);
		// segment i has base at joint i and tip at joint i+1.
		Vec3[] joints = Leggy.jointsOf(segments).toArray(Vec3[]::new);
		if (joints.length < 1) return;
		final IKSegment root = seg[1]; // root segment that is allowed to move

//		// define vector for rotation plane
//		// treat root as hinge joint around y axis
//		Vec3 planeNormal = target.subtract(root.getPos()).with(Direction.Axis.Y, 0).cross(new Vec3(0, 1, 0));
//		planeNormal = planeNormal.length() == 0 ? new Vec3(1, 0, 0) : planeNormal.normalize();

		// distance between nodes[i] and nodes[i+1];
		double dist = target.distanceTo(root.position());

		// check if in reach
		if (dist > maxDist) { // unreachable target
			joints[1] = seg[0].endPosition();
			for (int i = 1; i <= joints.length - 2; i++) {
				// from the root, point each segment towards target as far as it goes.
				// point end joint of segment i at target
//				joints[i + 1] = adjustRelative(joints[i], target, seg[i].length());
				// apply constraints: clamp to rotation range of segment i
//				joints[i + 1] = GeometryUtil.clampToFrustum(seg[i].constraint(), joints[i + 1], joints[i - 1], joints[i], ref);
				// re-scale to correct length
				joints[i + 1] = adjustRelative(joints[i], joints[i + 1], seg[i].length());
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
					// constrain joint i to be in correct rotation relative to i+2 pointing towards i+1.
					// technically incorrect, needs to be mirrored. todo.
//					Vec3 constrained = GeometryUtil.clampToFrustum(seg[i + 1].constraint(), joints[i], joints[i + 2], joints[i + 1], ref);
					// fix segment length
//					joints[i] = adjustRelative(joints[i + 1], constrained, seg[i].length());
					joints[i] = adjustRelative(joints[i + 1], joints[i], seg[i].length());
				}

				// BACKWARDS PHASE
				// reset root of segment 1 to touch end of segment 0
				joints[1] = rootPos;
				// for each joint after, restrict it to respect constraints of the tip it represents
				for (int i = 1; i <= joints.length - 2; i++) {
					// constrain ideal value for valid rotation
//					Vec3 constrained = GeometryUtil.clampToFrustum(seg[i - 1].constraint(), joints[i], joints[i - 2], joints[i - 1], ref);
					// rescale appropriately
//					joints[i] = adjustRelative(joints[i - 1], constrained, seg[i - 1].length());
					joints[i + 1] = adjustRelative(joints[i], joints[i + 1], seg[i].length());
				}
				dif = joints[joints.length - 1].distanceTo(target);
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
		return a.multiply(li1, li1, li1).add(b.multiply(li, li, li));
	}

	// projects point onto the plane intersecting anchor with unit vector normal
	private static Vec3 planarProject(Vec3 point, Vec3 anchor, Vec3 normal) {
		return point.subtract(normal.scale(point.subtract(anchor).dot(normal)));
	}
}
