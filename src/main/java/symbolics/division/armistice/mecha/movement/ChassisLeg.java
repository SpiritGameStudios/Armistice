package symbolics.division.armistice.mecha.movement;

import au.edu.federation.caliko.FabrikChain3D;
import au.edu.federation.utils.Vec3f;
import net.minecraft.world.phys.Vec3;
import symbolics.division.armistice.mecha.ChassisPart;
import symbolics.division.armistice.model.MechaModelData;

public class ChassisLeg {

	protected final double stepTolerance = 2; // temp: get from leginfo
	protected ChassisPart chassis;
	protected FabrikChain3D chain;

	private static final Vec3f X_AXIS = new Vec3f(1, 0, 0);
	private static final Vec3f Y_AXIS = new Vec3f(0, 1, 0);
	private static final Vec3f Z_AXIS = new Vec3f(0, 0, 1);

	public ChassisLeg(MechaModelData.LegInfo info, ChassisPart chassis) {
		this.chassis = chassis;
		// model format:
		// leg1 -> seg1: yaw bone. get default yaw and limits
		// seg1 -> seg2: get x-axis rotation and limits


		// legs only calculate positions etc relative to chassis
	}

	/*
	public Leggy(int nSegments) {
		if (nSegments < 1) throw new RuntimeException("leg must have at least one segment");
		chain = new FabrikChain3D();

		Vec3f root = new Vec3f();
		Vec3f end = root.plus(Z_AXIS);
		FabrikBone3D baseBone = new FabrikBone3D(root, end);
		chain.addBone(baseBone);
		chain.setBaseLocation(baseLocation);
		chain.setGlobalHingedBasebone(
			new Vec3f(Y_AXIS), 180, 180, new Vec3f(Z_AXIS)
		);
		chain.setMinIterationChange(0.1f);
		chain.setSolveDistanceThreshold(0.1f);
		chain.setMaxIterationAttempts(500);

		for (int i = 0; i<3; i++) {
			float cw = i % 2 == 0 ? 45 : 45;
			float ccw = i % 2 == 0 ? 45 : 45;
			chain.addConsecutiveHingedBone(
				Z_AXIS, 1f, FabrikJoint3D.JointType.LOCAL_HINGE, X_AXIS, 160, 160, Z_AXIS
			);
		}

		chain.addConsecutiveHingedBone(
			Z_AXIS, 1f, FabrikJoint3D.JointType.LOCAL_HINGE, X_AXIS, 0, 160, Z_AXIS
		);


		this.controller = new LegController(this);
	}
	 */

	public void setStepTarget(Vec3 location) {
		// this is a world location that we need to transform into model space
	}

	public void tick() {

	}

	public boolean stepping() {
		return false;
	}


}
