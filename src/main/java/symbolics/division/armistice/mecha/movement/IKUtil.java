package symbolics.division.armistice.mecha.movement;

import au.edu.federation.caliko.FabrikChain3D;
import au.edu.federation.utils.Vec3f;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class IKUtil {
	public static final Vec3f X_AXIS = new Vec3f(1, 0, 0);
	public static final Vec3f Y_AXIS = new Vec3f(0, 1, 0);
	public static final Vec3f Z_AXIS = new Vec3f(0, 0, 1);

	public static void configureDefaultChainSettings(FabrikChain3D chain) {
		// Caliko Structures don't preserve all chain settings when added
		chain.setMinIterationChange(0.1f);
		chain.setSolveDistanceThreshold(0.1f);
		chain.setMaxIterationAttempts(100);
	}

	// flipped x version for left handed system conversion (confusing!)
	public static Vec3f mc2fab(Vec3 mcVec) {
		return new Vec3f(-(float) mcVec.x, (float) mcVec.y, (float) mcVec.z);
	}

	public static Vec3 fab2mc(Vec3f fabVec) {
		return new Vec3(-fabVec.x, fabVec.y, fabVec.z);
	}

	public static Vec3f m2f(Vec3 mcVec) {
		return new Vec3f((float) mcVec.x, (float) mcVec.y, (float) mcVec.z);
	}

	public static Vec3 f2m(Vec3f fabVec) {
		return new Vec3(fabVec.x, fabVec.y, fabVec.z);
	}

	public static Vec3f rotateFabYawDeg(Vec3f vec, float deg) {
		// calico is left-handed, be careful
		return mc2fab(
			fab2mc(vec).yRot(deg * Mth.DEG_TO_RAD)
		);
	}
}
