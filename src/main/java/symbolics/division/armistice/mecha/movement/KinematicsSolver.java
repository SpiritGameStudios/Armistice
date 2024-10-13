package symbolics.division.armistice.mecha.movement;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;


public class KinematicsSolver {

	public static void solve(Vec3 target, JointNode effector) {
		solve(target, null, effector);
	}

	// FABRIK: A fast, iterative solver for the Inverse Kinematics problem
	// Aristidou and Lasenby, 2011
	// http://www.andreasaristidou.com/publications/papers/FABRIK.pdf
	public static void solve(Vec3 target, @Nullable JointNode root, JointNode effector) {
		final double tolerance = 0.1;
		final int maxIterations = 50;

		JointNode[] nodes = effector.getChain(root);
		if (nodes.length < 1) return;
		if (root == null) root = nodes[0];
		final int n = nodes.length;

		// define vector for rotation plane
		// treat root as hinge joint around y axis
		Vec3 planeNormal = target.subtract(root.getPos()).with(Direction.Axis.Y, 0).cross(new Vec3(0, 1, 0));
		planeNormal = planeNormal.length() == 0 ? new Vec3(1, 0, 0) : planeNormal.normalize();

		// distance between nodes[i] and nodes[i+1];
		double[] offsets = new double[n - 1]; // d_i
		double sum_offsets = 0;
		for (int i = 0; i < offsets.length - 1; i++) {
			offsets[i] = nodes[i].getOffset();
			sum_offsets += offsets[i];
		}
		double dist = target.distanceTo(root.getPos());
		Vec3 rootPos = nodes[0].getPos();

		// check if in reach
		if (dist > sum_offsets) { // unreachable target
			for (int i = 0; i < n - 1; i++) {
				nodes[i + 1].setPos(
					adjustRelative(constrain(nodes[i].getPos(), rootPos, planeNormal), target, offsets[i])
				);
			}
		} else { // reachable target
			double dif = nodes[n - 1].getPos().distanceTo(target);
			int iterations = maxIterations;
			while (dif > tolerance && iterations > 0) {
				iterations--;
				// FORWARD PHASE
				// set effector to target
				nodes[n - 1].setPos(target);
				// move joint i to closest valid position to joint i+1
				for (int i = n - 2; i > 0; i--
				) {
					// constrain joint i to be in correct rotation
					var constrained = constrain(nodes[i].getPos(), rootPos, planeNormal);

					// it is now in correct plane but wrong rotation
					// wrt pitch, it is either below the min angle or above the max angle.
					// constraining: constrain, then adjust.

					if (i <= n - 3) {
						Vec3 i2i3 = nodes[i + 2].getPos().subtract(nodes[i + 1].getPos());
//						Vec3 i1i2 =  nodes[i]
					}

					// ensure offset from i+1 is satisfied
					nodes[i].setPos(adjustRelative(nodes[i + 1].getPos(), constrained, offsets[i]));
				}

				// BACKWARDS PHASE
				nodes[0].setPos(rootPos);
				// adjust tail of segments to be guaranteed attached to root
				for (int i = 0; i < n - 2; i++) {
					// constrain ideal value for valid rotation
//					var constrained = constrain(nodes[i+1].getPos(), rootPos, planeNormal);
					// rescale appropriately
					nodes[i + 1].setPos(adjustRelative(nodes[i].getPos(), nodes[i + 1].getPos(), offsets[i]));
				}
				dif = nodes[n - 1].getPos().distanceTo(target);
			}
		}
	}

	private static Vec3 constrain(Vec3 pos, Vec3 rootPos, Vec3 planeNormal) {
		return planarProject(pos, rootPos, planeNormal);
	}

	// returns a point on the line between <a> and <b> that is exactly <offset> distance from <a>
	private static Vec3 adjustRelative(Vec3 a, Vec3 b, double offset) {
		double li = offset / a.distanceTo(b);
		double li1 = 1 - li;
		return a.multiply(li1, li1, li1).add(b.multiply(li, li, li));
	}

	// projects point onto the plane intersecting anchor with unit vector normal
	private static Vec3 planarProject(Vec3 point, Vec3 anchor, Vec3 normal) {
		return point.subtract(normal.scale(point.subtract(anchor).dot(normal)));
	}
}
