package symbolics.division.armistice.mecha.movement;

import com.mojang.datafixers.util.Function3;
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
		final Function3<Vec3, Vec3, Double, Vec3> adjustRelative = (a, b, offset) -> {
			double ri = a.distanceTo(b);
			double li = offset/ri;
			double li1 = 1-li;
			return a.multiply(li1, li1, li1).add(b.multiply(li, li, li));
		};

		JointNode[] nodes = effector.getChain(root);
		if (nodes.length < 1) return;
		if (root == null) root = nodes[0];
		final int n = nodes.length;

		// distance between nodes[i] and nodes[i+1];
		double[] offsets = new double[n-1]; // d_i
		double sum_offsets = 0;
		for (int i = 0; i < offsets.length-1; i++) {
			offsets[i] = nodes[i].getOffset();
			sum_offsets += offsets[i];
		}
		double dist = target.distanceTo(root.getPos());

		// check if in reach
		if (dist > sum_offsets) { // unreachable target
			// handle unreachable
			for (int i=0; i < n-1; i++) {
//				// ri = |target - p_i|
//				double jointDist = target.distanceTo(nodes[i].getPos());
//				// l_i = d_i / r_i
//				double pct = offsets[i]/jointDist;
//				// p_{i+1} = (1 - l_i) + l_i * target
				nodes[i+1].setPos(
						adjustRelative.apply(nodes[i].getPos(), target, offsets[i])
				);
			}
		} else { // reachable target
			Vec3 rootPos = nodes[0].getPos();
			double dif = nodes[n-1].getPos().distanceTo(target);
			while (dif > tolerance) {
				// FORWARD PHASE
				// set effector to target
				nodes[n-1].setPos(target);
				// iterate from node before effector
				for (int i=n-2; i>0; i--) {
					// find dist between new joint pos i+1 and current pos i
//					double r = nodes[i+1].getPos().distanceTo(nodes[i].getPos());
//					double pct = offsets[i]/r;
					// find new pos for pi
					nodes[i].setPos(
							adjustRelative.apply(nodes[i+1].getPos(), nodes[i].getPos(), offsets[i])
					);
				}

				// BACKWARDS PHASE
				nodes[0].setPos(rootPos);
				for (int i=0; i<n-2; i++) {
					nodes[i+1].setPos(
							adjustRelative.apply(nodes[i].getPos(), nodes[i+1].getPos(), offsets[i])
					);
				}
				dif = nodes[n-1].getPos().distanceTo(target);
			}
		}
	}
}
