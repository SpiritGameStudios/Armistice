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
import symbolics.division.armistice.mecha.movement.ChassisLeg;
import symbolics.division.armistice.mecha.movement.DirectionState;
import symbolics.division.armistice.mecha.movement.LegMap;
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
	protected final ChassisSchematic schematic;
	protected final double followTolerance = 4; // temp: related to model diameter
	protected final double moveSpeed = 1.0;
	protected List<ChassisLeg> legs;
	protected Vec3 movement = Vec3.ZERO;
	protected Vec3 pathingTarget = Vec3.ZERO;
	protected LegMap legMap;

	// todo notes: chassis schematic/skeleton needs to tell us
	// - number of legs
	// - number of segments for each individual leg
	// - leg root pos relative to chassis
	// - (AI) follow distance
	// - move speed
	// - step tolerance (?) -/- also at-rest tolerance

	public ChassisPart(ChassisSchematic schematic) {
		this.schematic = schematic;
	}

	@Override
	public void init(MechaCore core) {
		super.init(core);
		core.hull.init(core);
		this.legMap = new LegMap(core.model(), this);
		this.legs = core.model.legInfo().stream().map(info -> new ChassisLeg(info, this)).toList();
	}

	@Override
	public void tick() {
		super.tick();
		// temp: need non horizontal-only movement
		// update desired movement
		if (pathingTarget != null) {
			// if we're not facing the target, try to rotate towards it.
			Vec3 targetHorizontalDir = pathingTarget.subtract(core.position()).with(Direction.Axis.Y, 0).normalize();
			if (direction().dot(targetHorizontalDir) < 0.9) {
				direction.setTarget(targetHorizontalDir);
				if (!stepping()) {
					this.direction.tick();
				}
			} else {
				// otherwise we are facing it, so we ask our legs to move us towards it.
				float stepOffsetDistance = 1f;
				legMap.setMapOffset(new Vec3(0, 0, stepOffsetDistance));
			}
		} else {
			// don't need to go anywhere, give our legs a rest
			legMap.setMapOffset(Vec3.ZERO);
		}

		for (int i = 0; i < legs.size(); i++) {
//			// select locations around this for leg tips
//			Vec3 stepTarget = relStepPos(core, i);
//			debugStepTargets.set(i, stepTarget);
//			Leggy leg = legs.get(i);
//			// temp: move this logic inside leg controller, only update target pos at each tick
//			if (!GeometryUtil.inRange(leg.getStepTarget(), stepTarget, stepTolerance)) {
//				int l = i - 1 < 0 ? legs.size() - 1 : i - 1;
//				int r = i + 1 >= legs.size() ? 0 : i + 1;
//				boolean lneighborStepping = legs.get(l).stepping();
//				boolean rneighborStepping = legs.get(r).stepping();
//				if (!(lneighborStepping || rneighborStepping)) {
//					leg.setStepTarget(stepTarget);
////					leg.setTarget(stepTarget);
//				}
//			}
//			// temp: set based on skeleton
//			leg.setRootPos(core.position().add(0, 1, 0));
//
//			float r = baseLegAngle(i);
//			var d = this.direction();
//			var adj = d.yRot(r);
//			leg.setRootDir(adj);
////			leg.setRootDir(stepTarget.subtract(core.position().add(0, 1, 0)).with(Direction.Axis.Y, 0).normalize());
////			leg.setRootDir(new Vec3(0, 0, 1));//stepTarget.subtract(leg.getRootPos().with(Direction.Axis.Y, 0)).normalize());
//			leg.tick();
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

	public LegMap legMap() {
		return legMap;
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
		for (ChassisLeg l : legs) s |= l.stepping();
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

	@Override
	public void renderDebug(MultiBufferSource bufferSource, PoseStack poseStack) {
		if (true) return;
		for (int i = 0; i < legs.size(); i++) {
			ChassisLeg leg = legs.get(i);

//			VertexConsumer lineStrip2 = bufferSource.getBuffer(RenderType.debugLineStrip(2.0));
//			for (Vec3 joint : leg.jointPositions())
//				lineStrip2.addVertex(poseStack.last(), joint.toVector3f()).setColor(1.0f, 1.0f, 1.0f, 1.0f);

			VertexConsumer quad = bufferSource.getBuffer(RenderType.debugQuads());
			Vec3 target = debugStepTargets.get(i);

			quad.addVertex(poseStack.last(), target.add(-1, 0, -1).toVector3f()).setColor(1.0f, 0.0f, 0.0f, 1.0f);
			quad.addVertex(poseStack.last(), target.add(1, 0, -1).toVector3f()).setColor(1.0f, 0.0f, 0.0f, 1.0f);
			quad.addVertex(poseStack.last(), target.add(1, 0, 1).toVector3f()).setColor(1.0f, 0.0f, 0.0f, 1.0f);
			quad.addVertex(poseStack.last(), target.add(-1, 0, 1).toVector3f()).setColor(1.0f, 0.0f, 0.0f, 1.0f);

//			for (Vec3 joint : leg.jointPositions()) {
//				VertexConsumer rotationNormal = bufferSource.getBuffer(RenderType.debugLineStrip(4.0));
//				rotationNormal.addVertex(poseStack.last(), joint.toVector3f()).setColor(0.0f, 1.0f, 1.0f, 1.0f);
//				rotationNormal.addVertex(poseStack.last(), joint.add(leg.rot_normal).toVector3f()).setColor(0.0f, 1.0f, 1.0f, 1.0f);
//			}

//			drawLoc(leg.getStepTarget().toVector3f(), 0, 0, 1, poseStack, bufferSource);
//			drawLoc(leg.getTarget().toVector3f(), 1, 1, 0, poseStack, bufferSource);
//			drawLoc(leg.getTrueTarget(), 0, 1, 0, poseStack, bufferSource);

		}

		VertexConsumer targetLine = bufferSource.getBuffer(RenderType.debugLineStrip(4.0));
		targetLine.addVertex(poseStack.last(), core.position().add(0, 1, 0).add(core.direction()).toVector3f())
			.setColor(0.0f, 1.0f, 0.0f, 1.0f);
		targetLine.addVertex(poseStack.last(), pathingTarget.toVector3f())
			.setColor(0.0f, 1.0f, 0.0f, 1.0f);

		core.hull.renderDebug(bufferSource, poseStack);
	}

	private static void drawLoc(Vector3f p, float r, float g, float b, PoseStack poseStack, MultiBufferSource bf) {
		VertexConsumer vc = bf.getBuffer(RenderType.debugLineStrip(4.0));
		vc.addVertex(poseStack.last(), p).setColor(r, g, b, 1.0f);
		vc.addVertex(poseStack.last(), p.add(0, 1, 0, new Vector3f()))
			.setColor(r, g, b, 1.0f);
	}
}
