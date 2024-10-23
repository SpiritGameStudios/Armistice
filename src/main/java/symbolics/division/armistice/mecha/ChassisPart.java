package symbolics.division.armistice.mecha;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import symbolics.division.armistice.mecha.movement.DirectionState;
import symbolics.division.armistice.mecha.movement.GeometryUtil;
import symbolics.division.armistice.mecha.movement.Leggy;
import symbolics.division.armistice.mecha.schematic.ChassisSchematic;

import java.util.ArrayList;
import java.util.List;

/**
 * The chassis controls the pathing and manages legs. It is the root of the model,
 * so all other parts have position and rotation defined with this at the base.
 * It also has a separate health pool and hitbox that controls whether it is immobilized.
 */
public class ChassisPart extends AbstractMechaPart {
	public final List<Vec3> debugStepTargets = new ArrayList<>();
	protected final DirectionState direction = new DirectionState(Math.PI);
	protected final List<Leggy> legs = new ArrayList<>();
	protected final ChassisSchematic schematic;
	protected final double followTolerance = 4; // temp: related to model diameter
	protected final double moveSpeed = 1.0;
	protected final int numLegs = 6;
	protected final double stepTolerance = 2;
	protected Vec3 movement = Vec3.ZERO;
	protected Vec3 pathingTarget = Vec3.ZERO;

	// todo notes: chassis schematic/skeleton needs to tell us
	// - number of legs
	// - number of segments for each individual leg
	// - leg root pos relative to chassis
	// - (AI) follow distance
	// - move speed
	// - step tolerance (?) -/- also at-rest tolerance

	public ChassisPart(ChassisSchematic schematic) {
		this.schematic = schematic;
		for (int i = 0; i < numLegs; i++) {
			// temp: Define leg segments and orientation from bone data
			legs.add(new Leggy(5));
			legs.get(i).setRootDir(new Vec3(0, 0, 1));
			debugStepTargets.add(Vec3.ZERO);
		}
	}

	@Override
	public void init(MechaCore core) {
		super.init(core);
		core.hull.init(core);
		for (Leggy l : legs) {
			// temp: leg pos from skeleton etc etc
			l.setRootPosAll(core.position());
		}
	}

	@Override
	public void tick() {
		super.tick();
		// temp: need non horizontal-only movement
		if (pathingTarget != null) {
			var targetXZ = pathingTarget.with(Direction.Axis.Y, 0);
			var coreXZ = core.position().with(Direction.Axis.Y, 0);
			if (targetXZ.distanceTo(coreXZ) > followTolerance) {
				var hdir = targetXZ.subtract(coreXZ);
				this.direction.setTarget(hdir);
				if (!stepping()) {
					this.movement = hdir.normalize().scale(moveSpeed);
				}
			} else {
				this.movement = Vec3.ZERO;
			}
		}

//		if (!stepping()) {
		this.direction.tick();
//		}

		for (int i = 0; i < legs.size(); i++) {
			// select locations around this for leg tips
			Vec3 stepTarget = relStepPos(core, i);
			debugStepTargets.set(i, stepTarget);
			Leggy leg = legs.get(i);
			// temp: move this logic inside leg controller, only update target pos at each tick
			if (!GeometryUtil.inRange(leg.getStepTarget(), stepTarget, stepTolerance)) {
				int l = i - 1 < 0 ? legs.size() - 1 : i - 1;
				int r = i + 1 >= legs.size() ? 0 : i + 1;
				boolean lneighborStepping = legs.get(l).stepping();
				boolean rneighborStepping = legs.get(r).stepping();
				if (!(lneighborStepping || rneighborStepping)) {
					leg.setStepTarget(stepTarget);
				}
			}
			// temp: set based on skeleton
			leg.setRootPosAll(core.position().add(0, 1, 0));
//			leg.setRootDir(stepTarget.subtract(core.position().add(0, 1, 0)).with(Direction.Axis.Y, 0).normalize());
//			leg.setRootDir(new Vec3(0, 0, 1));//stepTarget.subtract(leg.getRootPos().with(Direction.Axis.Y, 0)).normalize());
			leg.tick();
		}
	}

	@Override
	public void serverTick() {
		tick();
		core.hull.serverTick();
	}

	@Override
	public void clientTick(float tickDelta) {
		tick();
		core.hull.clientTick(tickDelta);
	}

	@Override
	public Part parent() {
		return core;
	}

	@Override
	public Quaternionf relRot() {
		return new Quaternionf().rotateTo(new Vector3f(0, 0, 1), this.direction().toVector3f());
	}

	Vector3fc relHullPos() {
		return core.model().relativeHullPosition();
	}

	public Vec3 getPathingTarget() {
		return this.pathingTarget;
	}

	/**
	 * @param target the location in the level to path to.
	 */
	public void setPathingTarget(Vec3 target) {
		this.pathingTarget = target;
	}

	protected boolean stepping() {
		boolean s = false;
		for (Leggy l : legs) s |= l.stepping();
		return s;
	}

	/**
	 * @return Desired acceleration absent external forces
	 */
	public Vec3 movement() {
		return movement;
	}

	// temp: see where we can replace this with relRot etc
	public Vec3 direction() {
		return direction.curDir();
	}

	/**
	 * Assuming bilateral symmetry with two rows of legs
	 *
	 * @return Expected central target position for leg tip relative to chassis center and direction
	 */
	protected Vec3 relStepPos(MechaCore core, int leg) {
		// temp: may need to define each by max leg distance.
		// 		 definitely need to define by skeleton offset and rotation
		// left legs are even, right are odd
		float side = -(leg % 2 * 2 - 1);// +1 for even (left), -1 for odd (right)
		float rotation = side * ((((float) leg / numLegs) + 0.1f) * (float) Math.PI);
		var base = this.direction().with(Direction.Axis.Y, 0).yRot(rotation).normalize();
		return core.position().add(base.scale(legs.get(leg).getMaxLength() / 2).add(0, -1, 0));
	}

	@Override
	public void renderDebug(MultiBufferSource bufferSource, PoseStack poseStack) {
		for (int i = 0; i < legs.size(); i++) {
			Leggy leg = legs.get(i);

			VertexConsumer lineStrip2 = bufferSource.getBuffer(RenderType.debugLineStrip(2.0));
			for (Vec3 joint : leg.jointPositions())
				lineStrip2.addVertex(poseStack.last(), joint.toVector3f()).setColor(1.0f, 1.0f, 1.0f, 1.0f);

			VertexConsumer quad = bufferSource.getBuffer(RenderType.debugQuads());
			Vec3 target = debugStepTargets.get(i);

			quad.addVertex(poseStack.last(), target.add(-1, 0, -1).toVector3f()).setColor(1.0f, 0.0f, 0.0f, 1.0f);
			quad.addVertex(poseStack.last(), target.add(1, 0, -1).toVector3f()).setColor(1.0f, 0.0f, 0.0f, 1.0f);
			quad.addVertex(poseStack.last(), target.add(1, 0, 1).toVector3f()).setColor(1.0f, 0.0f, 0.0f, 1.0f);
			quad.addVertex(poseStack.last(), target.add(-1, 0, 1).toVector3f()).setColor(1.0f, 0.0f, 0.0f, 1.0f);

			for (Vec3 joint : leg.jointPositions()) {
				VertexConsumer rotationNormal = bufferSource.getBuffer(RenderType.debugLineStrip(4.0));
				rotationNormal.addVertex(poseStack.last(), joint.toVector3f()).setColor(0.0f, 1.0f, 1.0f, 1.0f);
				rotationNormal.addVertex(poseStack.last(), joint.add(leg.rot_normal).toVector3f()).setColor(0.0f, 1.0f, 1.0f, 1.0f);
			}
		}

		VertexConsumer targetLine = bufferSource.getBuffer(RenderType.debugLineStrip(4.0));
		targetLine.addVertex(poseStack.last(), core.position().add(0, 1, 0).add(core.direction()).toVector3f())
			.setColor(0.0f, 1.0f, 0.0f, 1.0f);
		targetLine.addVertex(poseStack.last(), pathingTarget.toVector3f())
			.setColor(0.0f, 1.0f, 0.0f, 1.0f);

		core.hull.renderDebug(bufferSource, poseStack);
	}
}
