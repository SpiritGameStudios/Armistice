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

//	public static void getLocalPitch(FabrikChain3D chain, int index) {
//		FabrikBone3D bone = chain.getBone(index);
////		bone.getGlobalPitchDegs()
////		// we want local pitch, global yaw
////		chain.getBaseboneRelativeReferenceConstraintUV();
////		chain.getBaseboneRelativeConstraintUV()
//
//
//		// modified from Fabrik Chain 3d solution for local hinges (see license)
//
//		// Not a basebone? Then construct a rotation matrix based on the previous bones inner-to-to-inner direction...
//		Mat3f m;
//		Vec3f relativeHingeRotationAxis;
//		if (index > 0) {
//			m = Mat3f.createRotationMatrix(chain.getBone(index - 1).getDirectionUV());
//			relativeHingeRotationAxis = m.times(bone.getJoint().getHingeRotationAxis()).normalise();
//		} else // ...basebone? Need to construct matrix from the relative constraint UV.
//		{
//			relativeHingeRotationAxis = chain.getBaseboneRelativeConstraintUV();
//		}
//
//		// ...and transform the hinge rotation axis into the previous bones frame of reference. ^^^ up there
//
//		// now we know how to transform our global pitch
//
//
//		// Project this bone's outer-to-inner direction onto the plane described by the relative hinge rotation axis
//		// Note: The returned vector is normalised.
//		Vec3f outerToInnerUV = bone.getDirectionUV().negated().projectOntoPlane(relativeHingeRotationAxis);
//	}
}
