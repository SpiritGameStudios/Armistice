package symbolics.division.armistice.mecha.movement;

import au.edu.federation.caliko.FabrikBone3D;
import au.edu.federation.caliko.FabrikChain3D;
import au.edu.federation.caliko.FabrikJoint3D;
import au.edu.federation.utils.Vec3f;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import symbolics.division.armistice.debug.ArmisticeDebugValues;
import symbolics.division.armistice.math.GeometryUtil;
import symbolics.division.armistice.mecha.ChassisPart;
import symbolics.division.armistice.mecha.MechaCore;
import symbolics.division.armistice.model.MechaModelData;

import java.util.ArrayList;
import java.util.List;

import static symbolics.division.armistice.mecha.movement.IKUtil.f2m;
import static symbolics.division.armistice.mecha.movement.IKUtil.m2f;

public class ChassisLeg {

	protected final int legIndex;
	protected ChassisPart chassis;
	protected FabrikChain3D chain = new FabrikChain3D();

	protected Vec3 tickTarget = Vec3.ZERO;
	protected Vec3 prevStepTarget = Vec3.ZERO;
	protected Vec3 nextStepTarget = Vec3.ZERO;
	protected Vec3 finalStepTarget = Vec3.ZERO;

	public boolean priority = false;

	private final float tempInitialY = 0;

	public ChassisLeg(MechaModelData.LegInfo info, ChassisPart chassis, int index) {
		// WARNING: CALIKO ROTATION SYSTEM IS LEFT-HANDED
		// THIS MEANS X AXIS IS FLIPPED OVER Z COMPARED TO MINECRAFT

		this.chassis = chassis;
		this.legIndex = index;
		// model format:
		// leg1 -> seg1: yaw bone. get default yaw and limits
		// seg1 -> seg2: get x-axis rotation and limits

		// bone from center of model to base of leg (base to leg# in model).
		// must always be a fixed yaw from "nose" direction.
//		Vec3f base = new Vec3f();
//		Vec3f end = base.plus(IKUtil.Z_AXIS.times((float) info.rootOffset().length()));
//		FabrikBone3D baseBone = new FabrikBone3D(base, end);
//		chain.addBone(baseBone);
//		chain.setHingeBaseboneConstraint(
//			FabrikChain3D.BaseboneConstraintType3D.LOCAL_HINGE,
//			IKUtil.Y_AXIS,
//			1,
//			1,
//			IKUtil.mc2fab(info.rootOffset().with(Direction.Axis.Y, 0))
//		);

		// yaw bone: base of leg in the actual model. Only bone allowed to rotate around y.
		// we have to add the artificial yaw of the parent to get the true yaw relative to it.
		float baseYawDeg = (float) GeometryUtil.yaw(info.rootOffset()) * Mth.RAD_TO_DEG;
		MechaModelData.SegmentInfo yawBoneInfo = info.segments().getFirst();
		FabrikBone3D yawBone = new FabrikBone3D(new Vec3f(), new Vec3f(0, 0, (float) info.segments().getFirst().length()));
		float relativeYaw = (float) yawBoneInfo.baseAngleDeg() - baseYawDeg;
		chain.addBone(yawBone);
		chain.setBaseLocation(m2f(info.rootOffset()));
		chain.setGlobalHingedBasebone(
			IKUtil.Y_AXIS,
			180,//(float) yawBoneInfo.minAngleDeg(),
			180, //(float) yawBoneInfo.maxAngleDeg(),
			Vec3f.rotateAboutAxisDegs(
				IKUtil.Z_AXIS,
				relativeYaw,
				IKUtil.Y_AXIS
			)
		);

		for (int i = 1; i < info.segments().size(); i++) {
			var segment = info.segments().get(i);
			float segBasePitch = (float) segment.baseAngleDeg();
			chain.addConsecutiveHingedBone(
				IKUtil.Z_AXIS,
				(float) segment.length(),
				FabrikJoint3D.JointType.LOCAL_HINGE,
				IKUtil.X_AXIS.negated(),
				180,//(float) segment.minAngleDeg(),
				180,//(float) segment.maxAngleDeg(),
				Vec3f.rotateAboutAxisDegs(IKUtil.Z_AXIS, segBasePitch, IKUtil.X_AXIS)
			);
		}

//		for (int i = 1; i < info.segments().size(); i++) {
//			var segment = info.segments().get(i);
//			float deg = (float) segment.baseAngleDeg();
//			deg = Mth.abs(deg); // might need to change for some models, but apparently fixes rotation in test model?
//			var r = Vec3f.rotateAboutAxisDegs(IKUtil.Z_AXIS, deg, IKUtil.X_AXIS);
//			r.set(Mth.abs(r.x) < 0.001f ? 0.0f : r.x, Mth.abs(r.y) < 0.001f ? 0.0f : r.y, Mth.abs(r.z) < 0.001f ? 0.0f : r.z);
//
//			chain.addConsecutiveHingedBone(
//				IKUtil.Z_AXIS,
//				(float) segment.length(),
//				FabrikJoint3D.JointType.LOCAL_HINGE,
//				IKUtil.X_AXIS,
//				(float) segment.minAngleDeg(), (float) segment.maxAngleDeg(),
//				r
//			);
//		}

		String legName = "leg" + legIndex;
		chain.setName(legName);
//		baseBone.setName(legName + "_base");
		chain.getBone(0).setName(legName + "_yaw");
		// skip first 2 in enumeration (root and yaw bones)
		for (int i = 1; i < chain.getChain().size(); i++) {
			chain.getBone(i).setName(legName + "_seg" + i);
		}

		// skeleton makes a copy, so we need to snag it back after skeleton gets it.
		// this is fine as long as we don't modify the chain, only read from it and update embedded targets.
//		skeleton.connectChain(chain, 0, 0, BoneConnectionPoint.END);
//		chain = skeleton.getChain(skeleton.getNumChains() - 1);
		IKUtil.configureDefaultChainSettings(chain);
	}

	public FabrikChain3D getChain() {
		return chain;
	}

	protected float totalTicksToStep = 1;
	protected float ticksToStep = 0;

	public void tick() {
		// there are 4 types of targets:
		/*
			tick target: position that the chain should be targeting in during this tick.
			previous step target: previous location this leg is stepping from
			next step target: new location this leg is stepping to
			final step target: ultimate location this leg will try to step to until in range.
		 */
		if (!ArmisticeDebugValues.ikSolving) return;

		if (((MechaCore) chassis.parent()).entity().level().isClientSide) {
			trySolve(tickTarget);
			return;
		}

		float ticksPerBlock = 15;
		Vec3 mapTarget = chassis.legMap().legTarget(legIndex);
//		Vec3 tip = tipPos();
//		float mapDelta = (float) mapTarget.distanceTo(tip);
//		boolean inRange = mapDelta <= chassis.legMap().stepTolerance();


//		// check if we need to do a new step
//		if (!stepping() && !inRange && !chassis.neighborsStepping(legIndex)) {
//			if (prevStepTarget != finalStepTarget) {
//				prevStepTarget = tip;
//			}
//			finalStepTarget = getNearestValidStepTarget(mapTarget);
//			totalTicksToStep = mapDelta * ticksPerBlock;
//			ticksToStep = totalTicksToStep;
//			priority = false;
//		}
//
//		// update current step
//		if (stepping()) {
//			// not final tick, snap if too far
//			priority = false;
//			if (ticksToStep > 1f && tickTarget.closerThan(finalStepTarget, 10)) {
//				ticksToStep--;
//				float stepPercent = (totalTicksToStep - ticksToStep) / totalTicksToStep;
//				tickTarget = prevStepTarget.add(finalStepTarget.subtract(prevStepTarget).scale(stepPercent))
//					.add(0, GeometryUtil.easedCurve(stepPercent), 0);
//			} else { //final tick
//				ticksToStep = 0;
//				tickTarget = finalStepTarget;
//				prevStepTarget = finalStepTarget;
//			}
//		}

		if (legIndex == 0) {
			tickTarget = mapTarget;
			float ticks = ((MechaCore) chassis.parent()).entity().tickCount;
			tickTarget = tickTarget.add(1, 2, 1 + Mth.sin(ticks / 60));

			if (ticks % 20 == 0) {
//				System.out.println("maptarget: (server)" + mapTarget);
//				System.out.println(tickTarget);
			}
//			trySolve(tickTarget);
			chain.solveForTarget(m2f(mapTarget));
		}

	}

	public void trySolve(Vec3 worldSpaceTarget) {
//		if (chassis.legsReady()) {
		var m = m2f(w2m(worldSpaceTarget));
		chain.solveForTarget(m);
//			Vec3f prev = chain.getBone(0).getStartLocation();
		// you have to jiggle the bone a bit to motivate it to move
//			chain.getBone(0).getStartLocation().set(prev.x, prev.y + 0.002f, prev.z);
//			chain.solveForEmbeddedTarget();
//		}
	}

//	public void getLocalRotations() {
//		for (FabrikBone3D : )
//	}

//	protected Vec3 nearestValidStepPosition(Vec3 ideal) {
//		//  temp
//		return ideal;
//	}

	public Vec3 tipPos() {
		return m2w(f2m(chain.getEffectorLocation()));
	}

	public boolean stepping() {
		return ticksToStep > 0;
	}

	public Vec3 getTickTarget() {
		return tickTarget;
	}

	public void setTickTarget(Vec3 tickTarget) {
		this.tickTarget = tickTarget;
	}

	public Vec3 w2m(Vec3 vec) {
		Matrix4f transform = new Matrix4f();

		return new Vec3(
			chassis.relRot().conjugate().transform(vec.toVector3f().sub(chassis.absPos()))
		);
	}

	public Vec3 m2w(Vec3 vec) {
		Matrix4f transform = new Matrix4f();
		return new Vec3(
			transform.rotate(chassis.relRot()).transformPosition(vec.toVector3f()).add(chassis.absPos())
		);
	}


	private Vec3 getNearestValidStepTarget(Vec3 goal) {
		return goal;
//		if (tempInitialY == 0) tempInitialY = chassis.absPos().y;
//		return goal.with(Direction.Axis.Y, tempInitialY);
	}

	public List<Vec3> jointPositions() {
		List<Vec3> joints = new ArrayList<>();
		chain.getChain().stream().map(bone -> m2w(f2m(bone.getStartLocation()))).forEach(joints::add);
		joints.add(m2w(f2m(chain.getChain().getLast().getEndLocation())));
		return joints;
	}
}
