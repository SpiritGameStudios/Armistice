package symbolics.division.armistice.mecha.movement;

import au.edu.federation.caliko.FabrikBone3D;
import au.edu.federation.caliko.FabrikChain3D;
import au.edu.federation.caliko.FabrikJoint3D;
import au.edu.federation.utils.Vec3f;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import symbolics.division.armistice.mecha.ChassisPart;
import symbolics.division.armistice.model.MechaModelData;

public class ChassisLeg {

	protected final int legIndex;
	protected ChassisPart chassis;
	protected FabrikChain3D chain = IKUtil.defaultChain();

	protected Vec3 tickTarget = Vec3.ZERO;
	protected Vec3 prevStepTarget = Vec3.ZERO;
	protected Vec3 nextStepTarget = Vec3.ZERO;
	protected Vec3 finalStepTarget = Vec3.ZERO;

	/*
	Idea:

	starfish-shape: absolute root has a vertical bone, and all legs
	extend from this such that the angle is maintained as chassis rotates.

	n legs, n chains + 1 tiny bone that tries to solve for leg centroid + standing height at each step.
	leg chains have embedded targets for step positions.
	chassis should rotate to allow for leg rotations.
	absolute root is unfixed and points horizontally, like a tail.
	central tiny bone points directly vertical
	 */

	public ChassisLeg(MechaModelData.LegInfo info, ChassisPart chassis, int index) {
		this.chassis = chassis;
		this.legIndex = index;
		// model format:
		// leg1 -> seg1: yaw bone. get default yaw and limits
		// seg1 -> seg2: get x-axis rotation and limits

		// bone from center of model to base of leg. must always be a fixed yaw from "nose" direction.
		Vec3f base = new Vec3f();
		Vec3f end = base.plus(IKUtil.Z_AXIS);
		FabrikBone3D baseBone = new FabrikBone3D(base, end);
		chain.addBone(baseBone);
		chain.setRotorBaseboneConstraint(
			FabrikChain3D.BaseboneConstraintType3D.LOCAL_ROTOR,
			IKUtil.mc2fab(info.rootOffset().with(Direction.Axis.Y, 0).normalize()),
			0
		);
//		chain.setLocalHingedBasebone(
//			new Vec3f(IKUtil.Y_AXIS), 0, 0, IKUtil.mc2fab(info.rootOffset().with(Direction.Axis.Y, 0))
//		);
		chain.setEmbeddedTargetMode(true);

		// yaw bone: base of leg in the actual model. Only bone allowed to rotate around y.
		MechaModelData.SegmentInfo yawBone = info.segments().getFirst();
		chain.addConsecutiveHingedBone(
			IKUtil.Z_AXIS, (float) yawBone.length(),
			FabrikJoint3D.JointType.LOCAL_HINGE, IKUtil.Y_AXIS,
			(float) yawBone.minAngleDeg(), (float) yawBone.maxAngleDeg(),
			Vec3f.rotateAboutAxisDegs(IKUtil.Z_AXIS, (float) yawBone.baseAngleDeg(), IKUtil.Y_AXIS)
		);

		for (var segment : info.segments().subList(1, info.segments().size())) {
			chain.addConsecutiveHingedBone(
				IKUtil.Z_AXIS, (float) segment.length(),
				FabrikJoint3D.JointType.LOCAL_HINGE, IKUtil.X_AXIS,
				(float) segment.minAngleDeg(), (float) segment.maxAngleDeg(),
				Vec3f.rotateAboutAxisDegs(IKUtil.Z_AXIS, (float) segment.baseAngleDeg(), IKUtil.X_AXIS)
			);
		}

		String legName = "leg" + legIndex;
		chain.setName(legName);
		baseBone.setName(legName + "_base");
		for (int i = 1; i < chain.getChain().size(); i++) {
			chain.getBone(i).setName(legName + "_seg" + i);
		}

	}

	public FabrikChain3D getChain() {
		return chain;
	}

	public void tick() {
		// there are 4 types of targets:
		/*
			tick target: position that the chain should be targeting in during this tick.
			previous step target: previous location this leg is stepping from
			next step target: new location this leg is stepping to
			final step target: ultimate location this leg will try to step to until in range.
		 */
		double tempBlocksPerTick = 0.05f;
		tickTarget = chassis.legMap().legTarget(legIndex);
		chain.updateEmbeddedTarget(IKUtil.mc2fab(tickTarget));
		chain.solveForEmbeddedTarget();
	}

	private double stepTolerance() {
		// current step tolerance based on chassis state
		// when chassis doesn't want to move, should be small
		// temp: should be fed in from chassis model data, requested from chassis
		return 1.0;
	}

	public boolean stepping() {
		return false;
	}

}
