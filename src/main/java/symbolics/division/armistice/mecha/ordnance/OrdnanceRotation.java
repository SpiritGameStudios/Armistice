package symbolics.division.armistice.mecha.ordnance;

import au.edu.federation.caliko.FabrikBone3D;
import au.edu.federation.caliko.FabrikChain3D;
import au.edu.federation.caliko.FabrikJoint3D;
import au.edu.federation.utils.Vec3f;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.*;
import symbolics.division.armistice.mecha.MechaCore;
import symbolics.division.armistice.mecha.OrdnancePart;
import symbolics.division.armistice.mecha.movement.IKUtil;

import java.util.Map;

import static symbolics.division.armistice.mecha.MechaEntity.BARREL_ROTATIONS;

public class OrdnanceRotation {
	protected OrdnancePart ord;
	protected final MechaCore core;
	protected FabrikChain3D chain;

	protected final float yawSpeed;
	protected final float pitchSpeed;

	public OrdnanceRotation(OrdnancePart ord,
							float yOffset,
							float minYaw, float maxYaw, MechaCore core, float yawDegTickSpeed,
							float minPitch, float maxPitch, float pitchDegTickSpeed) {
		this.core = core;
		this.yawSpeed = yawDegTickSpeed;
		this.pitchSpeed = pitchDegTickSpeed;
		this.ord = ord;
		FabrikBone3D baseBone = new FabrikBone3D(new Vec3f(), new Vec3f(0, 1, 0));
		chain = new FabrikChain3D();
		chain.addBone(baseBone);
		chain.setGlobalHingedBasebone(
			IKUtil.Y_AXIS,
			minYaw,
			maxYaw,
			IKUtil.Z_AXIS
		);
		chain.addConsecutiveHingedBone(
			IKUtil.Z_AXIS,
			yOffset,
			FabrikJoint3D.JointType.LOCAL_HINGE,
			IKUtil.X_AXIS.negated(),
			maxPitch,
			minPitch,
			IKUtil.Z_AXIS
		);
	}

	public void setTarget(Vec3 target) {
		// transform to model space
		Matrix4f w2m = new Matrix4f();
		w2m.rotate(ord.baseRotation().conjugate());
		var ab = ord.absPos();
		var t = target.toVector3f().sub(ab);

		Vector3f modelspaceTarget = w2m.transformPosition(t);
		chain.solveForTarget(new Vec3f(modelspaceTarget.x, modelspaceTarget.y, modelspaceTarget.z));
	}

	public Vector2fc relYawPitchRad() {
		return core.ordnanceBarrelRotation(core.ordnanceIndex(ord));
	}

	public Vec3 currentDirection() {
		Vector2fc rotation = relYawPitchRad();

		Matrix4f m2w = new Matrix4f().rotate(ord.baseRotation());
		m2w.rotate(new Quaternionf().rotateZYX(0, rotation.x() * Mth.DEG_TO_RAD, -rotation.y() * Mth.DEG_TO_RAD));
		return new Vec3(m2w.transformDirection(0, 0, 1, new Vector3f()));
	}

	public void tick() {
		var d = IKUtil.f2m(chain.getBone(1).getDirectionUV());
		double xz = Mth.length(d.x, d.z);
		float yaw = (float) Mth.atan2(d.x, d.z) * Mth.RAD_TO_DEG;
		float pitch = (float) Mth.atan2(d.y, xz) * Mth.RAD_TO_DEG;

		Vector2fc rotation = relYawPitchRad();

		Map<Integer, Vector2f> rotations = core.entity().getEntityData().get(BARREL_ROTATIONS);

		rotations.put(
			core.ordnanceIndex(ord),
			new Vector2f(
				Mth.wrapDegrees(Mth.approachDegrees(rotation.x(), yaw, yawSpeed)),
				Mth.wrapDegrees(Mth.approachDegrees(rotation.y(), pitch, pitchSpeed))
			)
		);

		core.entity().getEntityData().set(BARREL_ROTATIONS, rotations);
	}
}
