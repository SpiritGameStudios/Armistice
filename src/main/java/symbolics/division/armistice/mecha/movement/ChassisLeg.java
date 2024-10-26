package symbolics.division.armistice.mecha.movement;

import au.edu.federation.caliko.*;
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

	public ChassisLeg(MechaModelData.LegInfo info, ChassisPart chassis, int index, FabrikStructure3D skeleton) {
		this.chassis = chassis;
		this.legIndex = index;
		// model format:
		// leg1 -> seg1: yaw bone. get default yaw and limits
		// seg1 -> seg2: get x-axis rotation and limits

		// bone from center of model to base of leg (base to leg# in model).
		// must always be a fixed yaw from "nose" direction.
		Vec3f base = new Vec3f();
		Vec3f end = base.plus(IKUtil.Z_AXIS.times((float) info.rootOffset().length()));
		FabrikBone3D baseBone = new FabrikBone3D(base, end);
		chain.addBone(baseBone);
		chain.setRotorBaseboneConstraint(
			FabrikChain3D.BaseboneConstraintType3D.LOCAL_ROTOR,
			IKUtil.mc2fab(info.rootOffset().with(Direction.Axis.Y, 0)),
			0
		);
//		chain.setLocalHingedBasebone(
//			new Vec3f(IKUtil.Y_AXIS), 0, 0, IKUtil.mc2fab(info.rootOffset().with(Direction.Axis.Y, 0))
//		);
		chain.setEmbeddedTargetMode(true);

		// yaw bone: base of leg in the actual model. Only bone allowed to rotate around y.
		// we have to add the artificial yaw of the parent to get the true yaw relative to it.
		// PROBLEM IS HERE: THIS BONES UV IS NOT FULLY HORIZONTAL BUT THE AXIS IS VERTICAL
		// IS IT:
		// because of 0 or 1 rotation constraints?
		// small minimum length (0.1) floating point errors?
		// note that the problem is in the relative UV in the function not being planar to 0, 1, 0 which is world space.
		float baseYaw = new Vec3f(IKUtil.mc2fab(info.rootOffset())).getGlobalYawDegs();
		MechaModelData.SegmentInfo yawBone = info.segments().getFirst();
		chain.addConsecutiveHingedBone(
			IKUtil.Z_AXIS,
			(float) yawBone.length(),
			FabrikJoint3D.JointType.LOCAL_HINGE,
			IKUtil.Y_AXIS,
			(float) yawBone.minAngleDeg(), (float) yawBone.maxAngleDeg(),
			Vec3f.rotateAboutAxisDegs(IKUtil.Z_AXIS, (float) yawBone.baseAngleDeg() + baseYaw, IKUtil.Y_AXIS)
		);

		for (var segment : info.segments().subList(1, info.segments().size())) {
			chain.addConsecutiveHingedBone(
				IKUtil.Z_AXIS,
				(float) segment.length(),
				FabrikJoint3D.JointType.LOCAL_HINGE,
				IKUtil.X_AXIS,
				(float) segment.minAngleDeg(), (float) segment.maxAngleDeg(),
				Vec3f.rotateAboutAxisDegs(IKUtil.Z_AXIS, (float) segment.baseAngleDeg(), IKUtil.X_AXIS)
			);
		}

		String legName = "leg" + legIndex;
		chain.setName(legName);
		baseBone.setName(legName + "_base");
		chain.getBone(1).setName(legName + "_yaw");
		// skip first 2 in enumeration (root and yaw bones
		for (int i = 2; i < chain.getChain().size(); i++) {
			chain.getBone(i).setName(legName + "_seg" + (i - 1));
		}

		// skeleton makes a copy, so we need to snag it back after skeleton gets it.
		// this is fine as long as we don't modify the chain, only read from it and update embedded targets.
		skeleton.connectChain(chain, 0, 0, BoneConnectionPoint.END);
		chain = skeleton.getChain(skeleton.getNumChains() - 1);
	}

	public FabrikChain3D getChain() {
		return chain;
	}


	private boolean firstTick = true;

	public void tick() {
		// there are 4 types of targets:
		/*
			tick target: position that the chain should be targeting in during this tick.
			previous step target: previous location this leg is stepping from
			next step target: new location this leg is stepping to
			final step target: ultimate location this leg will try to step to until in range.
		 */
		double tempBlocksPerTick = 0.05f;
		Vec3 maptarget = chassis.legMap().legTarget(legIndex);
		if (tickTarget.distanceTo(maptarget) > chassis.legMap().stepTolerance()) {
			tickTarget = chassis.legMap().legTarget(legIndex);
		}
		chain.updateEmbeddedTarget(IKUtil.mc2fab(tickTarget));
		try {
//			chain.solveForEmbeddedTarget();
		} catch (IllegalArgumentException e) {
			int a = 1;
			throw e;
		}
		firstTick = false;
	}

	public boolean stepping() {
		return false;
	}

}
