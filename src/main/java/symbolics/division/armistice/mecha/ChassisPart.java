package symbolics.division.armistice.mecha;

import au.edu.federation.caliko.FabrikBone3D;
import au.edu.federation.caliko.FabrikChain3D;
import au.edu.federation.caliko.FabrikStructure3D;
import au.edu.federation.utils.Vec3f;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4f;
import symbolics.division.armistice.client.render.MechaEntityRenderer;
import symbolics.division.armistice.client.render.hud.DrawHelper;
import symbolics.division.armistice.client.render.hud.MechaHudRenderer;
import symbolics.division.armistice.debug.ArmisticeDebugValues;
import symbolics.division.armistice.mecha.movement.ChassisLeg;
import symbolics.division.armistice.mecha.movement.DirectionState;
import symbolics.division.armistice.mecha.movement.IKUtil;
import symbolics.division.armistice.mecha.movement.LegMap;
import symbolics.division.armistice.mecha.schematic.ChassisSchematic;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static symbolics.division.armistice.mecha.MechaEntity.*;

/**
 * The chassis controls the pathing and manages legs. It is the root of the model,
 * so all other parts have position and rotation defined with this at the base.
 * It also has a separate health pool and hitbox that controls whether it is immobilized.
 */
public class ChassisPart extends AbstractMechaPart {
	// temp: Should be defined by sum of part sizes
	private static final double GRAVITY = 0.1;

	public final List<Vec3> debugStepTargets = new ArrayList<>();
	protected final DirectionState direction = new DirectionState(Math.PI);
	protected final ChassisSchematic schematic;
	protected final double followTolerance = 30; // temp: related to model diameter
	protected final double moveSpeed;
	protected List<ChassisLeg> legs;
	protected Vec3 movement = Vec3.ZERO;
	@Nullable
	protected Vec3 pathingTarget = Vec3.ZERO;
	protected LegMap legMap;
	protected FabrikStructure3D skeleton;

	protected boolean legsReady = false;
	protected Vec3 prevPos = Vec3.ZERO;
	private boolean firstTick = true;
	private boolean atRest = true;

	// todo notes: chassis schematic/skeleton needs to tell us
	// - number of legs
	// - number of segments for each individual leg
	// - leg root pos relative to chassis
	// - (AI) follow distance
	// - move speed
	// - step tolerance (?) -/- also at-rest tolerance

	public ChassisPart(ChassisSchematic schematic) {
		this.schematic = schematic;
		moveSpeed = schematic.moveSpeed();
	}

	private void setLegTickTargets(List<Vector3f> tickTargets) {
		this.core.entity().getEntityData().set(LEG_TICK_TARGETS, tickTargets);
	}

	private void setClientPos(Vector3f clientPos) {
		this.core.entity().getEntityData().set(CLIENT_POS, clientPos);
	}

	private void setClientDir(Vector3f clientDir) {
		this.core.entity().getEntityData().set(CLIENT_DIR, clientDir);
	}

	private List<Vector3f> getLegTickTargets() {
		return this.core.entity().getEntityData().get(LEG_TICK_TARGETS);
	}

	private Vector3f getClientPos() {
		return this.core.entity().getEntityData().get(CLIENT_POS);
	}

	private Vector3f getClientDir() {
		return this.core.entity().getEntityData().get(CLIENT_DIR);
	}

//	Vec3 prevDir = new Vec3(0, 0, 1);

	@Override
	public void init(MechaCore core) {
		super.init(core);
		core.hull.init(core);
		this.legMap = new LegMap(core.model(), this);


		// the skeleton appears
		this.skeleton = new FabrikStructure3D();
		var p = absPos();
		FabrikBone3D rootBone = new FabrikBone3D(new Vec3f(p.x, p.y, p.z), new Vec3f(p.x, p.y, p.z + 1));
		rootBone.setName("root");
		FabrikChain3D rootChain = new FabrikChain3D();
		rootChain.addBone(rootBone);
		rootChain.setGlobalHingedBasebone(
			IKUtil.Y_AXIS,
			180, 180, IKUtil.Z_AXIS
		);

		this.skeleton.addChain(rootChain);

		IKUtil.configureDefaultChainSettings(this.skeleton.getChain(0));
		this.skeleton.getChain(0).setSolveDistanceThreshold(0.3f);
		this.skeleton.getChain(0).setFixedBaseMode(false);

		// root bone is like the "nose" of the skeleton, guides the center towards desired position.
		// each leg determines its own fixed rotation relative to the nose, and adds its chain to the skeleton.
		this.legs = new ArrayList<>();
		for (int i = 0; i < core.model().legInfo().size(); i++) {
			legs.add(new ChassisLeg(core.model().legInfo().get(i), this, i, skeleton));
		}
	}

	public boolean legsReady() {
		return legsReady;
	}

	@Override
	public void tick() {
		super.tick();
	}

	@Override
	public void serverTick() {
		super.serverTick();

		// update desired movement
		if (pathingTarget != null && !pathingTarget.closerThan(new Vec3(absPos()).with(Direction.Axis.Y, pathingTarget.y), followTolerance)) {
			atRest = false;
			// if we're not facing the target, try to rotate towards it.
			Vec3 targetHorizontalDir = pathingTarget.subtract(core.position()).with(Direction.Axis.Y, 0).normalize();
			if (direction().dot(targetHorizontalDir) < 0.95) {
				// kindly ask our legs to rotate us
				float sign = (direction().yRot(Mth.PI / 2).dot(targetHorizontalDir) < 0) ? -1 : 1;
				legMap.setMapRotation(sign * Mth.PI / 4);
			} else {
				// otherwise we are facing it, so we ask our legs to move us towards it.
				// temp: set from model data
				float stepOffsetDistance = 10f;
				legMap.setMapOffset(new Vec3(0, 0, stepOffsetDistance));
				legMap.setMapRotation(0);
			}
		} else {// don't need to go anywhere, give our legs a rest
			atRest = true;
			legMap.setMapOffset(Vec3.ZERO);
			legMap.setMapRotation(0);
			pathingTarget = null;
		}

		List<ChassisLeg> lowp = new ArrayList<>();
		for (ChassisLeg leg : legs) {
			leg.tick();
//			if (leg.priority) {
//				leg.tick();
//			} else {
//				lowp.add(leg);
//				leg.priority = true;
//			}
		}
		for (ChassisLeg leg : lowp) {
//			leg.tick();
		}

		if (firstTick) {
			firstTick = false;
			prevPos = core.position();
		} else {
			// legmap is currently rotated or forwards depending on where we want our legs.
			// legs will attempt to move into position, then we want to move to be between our legs.

			Vec3 desiredDir = legMap.targetDir(effectors());
			Vec3 desiredPos = legMap.targetCentroid(desiredDir, effectors());

			// move to centroid
			if (prevPos == Vec3.ZERO) prevPos = core.position();
			var curPos = IKUtil.f2m(skeleton.getChain(0).getBone(0).getEndLocation());
			float ticksPerBlock = 4;
			var delta = desiredPos.subtract(curPos);
			Vec3f tgt = IKUtil.m2f(curPos.add(delta.normalize().scale(1 / ticksPerBlock)));

			// rotate base
			var baseDir = IKUtil.f2m(skeleton.getChain(0).getBone(0).getDirectionUV());
			var angleDelta = Math.acos(baseDir.dot(desiredDir));
			if (Math.abs(angleDelta) > 0.1) {
				var oldBase = IKUtil.f2m(skeleton.getChain(0).getBaseLocation());
				var tip = IKUtil.f2m(skeleton.getChain(0).getBone(0).getEndLocation());
				var newBase = tip.subtract(desiredDir);
				var baseDelta = newBase.subtract(oldBase);
				var interpolated = IKUtil.m2f(oldBase.add(baseDelta.normalize().scale(0.01)));

				skeleton.getChain(0).setBaseLocation(interpolated);
				skeleton.getChain(0).getBone(0).setStartLocation(interpolated);
			}

			// bug in caliko: targeting an effector's base is undefined
			var baseBone = skeleton.getChain(0).getBone(0);
			Vec3f baseStart = baseBone.getStartLocation();
			if (baseStart.approximatelyEquals(tgt, 0.01f)) { // apply perturbation
				baseBone.setStartLocation(new Vec3f(baseStart.x + 0.01f, baseStart.y, baseStart.z));
			}

			if (ArmisticeDebugValues.ikSolving) {
				if (!atRest) {
					skeleton.solveForTarget(tgt);
				}
				legsReady = true;
			}
			core.entity().setPos(IKUtil.f2m(skeleton.getChain(0).getBone(0).getEndLocation()));

			for (var leg : legs) {
				// live-update chain (caliko bug: fixedbase doesn't update on time)
				leg.setStartLocation(baseBone.getEndLocation());
			}

			// update client
			setClientPos(absPos());
			var ddd = direction().toVector3f();
			setClientDir(ddd);
			setLegTickTargets(effectors().stream().map(Vec3::toVector3f).toList());
		}

		core.hull.serverTick();

		if (!ArmisticeDebugValues.chassisGravity) return;
		movement = !core.entity().onGround() ?
			movement.subtract(0, GRAVITY, 0) :
			new Vec3(movement.x, 0, movement.z);
	}

	public boolean atRest() {
		return atRest;
	}

	@Override
	public void clientTick(float tickDelta) {
		super.clientTick(tickDelta);

		core.hull.clientTick(tickDelta);

		if (!ArmisticeDebugValues.ikSolving) return;
		if (firstTick || getLegTickTargets().isEmpty()) {
			firstTick = false;
			return;
		}

		Vec3 clientPos = new Vec3(getClientPos());
		Vec3 clientDir = new Vec3(getClientDir());
		List<Vec3> tickTargets = getLegTickTargets().stream().map(Vec3::new).toList();
		Vec3f target = IKUtil.m2f(clientPos);

		var baseChain = skeleton.getChain(0);
		var baseBone = baseChain.getBone(0);
		Vec3f baseLoc = IKUtil.m2f(clientPos.subtract(clientDir));
		fixBaseLocation(baseChain, baseLoc);
		baseBone.setEndLocation(target);

		for (int i = 0; i < tickTargets.size(); i++) {
			legs.get(i).updateEmbeddedTarget(tickTargets.get(i));

			// for debug render
			legs.get(i).setTickTarget(tickTargets.get(i));
		}

		skeleton.solveForTarget(target);

		for (var leg : legs) {
			leg.setStartLocation(baseBone.getEndLocation());
		}
	}

	private static void fixBaseLocation(FabrikChain3D chain, Vec3f loc) {
		chain.getBone(0).setStartLocation(loc);
		chain.setBaseLocation(loc);
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

	@Nullable
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
		if (core.entity().level().isClientSide) {
			return new Vec3(getClientDir());
		} else {
			var sd = skeleton.getChain(0).getBone(0).getDirectionUV();
			return new Vec3(sd.x, sd.y, sd.z);
		}
	}

	public List<Vec3> effectors() {
		return legs.stream().map(l -> {
			var loc = l.getTickTarget(); //getChain().getEffectorLocation();
			return new Vec3(loc.x, loc.y, loc.z);
		}).toList();
	}

	public boolean neighborsStepping(int leg) {
		// assuming bilateral symmetry and zigzag enumeration,
		// a neighbor is either -2, +1 or +2.
		if (leg >= 2 && legs.get(leg - 2).stepping()) {
			return true;
		}
		if (leg + 2 < legs.size() && legs.get(leg + 2).stepping()) {
			return true;
		}
		// special case: check neighbor +1 if even and neighbor -1 if odd
		// only works when left leg is start of enumeration at zero
		int adjacentNeighbor = leg % 2 == 0 ? leg + 1 : leg - 1;
		return adjacentNeighbor >= 0 && adjacentNeighbor < legs.size() && legs.get(adjacentNeighbor).stepping();
	}

	@Override
	public void renderDebug(MultiBufferSource bufferSource, PoseStack poseStack) {
		super.renderDebug(bufferSource, poseStack);

		for (int i = 0; i < skeleton.getNumChains(); i++) {
//			var effectorLoc = skeleton.getChain(i).getEffectorLocation();
//			poseStack.pushPose();
//			{
//				poseStack.translate(effectorLoc.x, effectorLoc.y, effectorLoc.z);
//				poseStack.mulPose(Minecraft.getInstance().gameRenderer.getMainCamera().rotation());
//				poseStack.scale(0.03f, -0.03f, 0.03f);
//				Minecraft.getInstance().font.drawInBatch(String.valueOf(i), 0.0F, 0.0F, -1, false, poseStack.last().pose(), bufferSource, Font.DisplayMode.NORMAL, 0, 15728880);
//			}
//			poseStack.popPose();

			skeleton.getChain(i).getChain().forEach(
				bone -> {
					DrawHelper.renderHologramFlicker(
						(pos, color) -> drawSeg(
							new Vector3f(bone.getStartLocationAsArray()).add(pos.toVector3f()),
							new Vector3f(bone.getEndLocationAsArray()).add(pos.toVector3f()),
							1,
							1,
							1,
							poseStack,
							bufferSource,
							color
						),
						Vec3.ZERO,
						MechaHudRenderer.lightbulbColor()
					);

				}
			);
		}

		// centroid-informed map target direction
//		var td = legMap.targetDir(effectors());
////		drawSeg(core.position().toVector3f(), core.position().add(td).toVector3f(), 0, 0, 1, poseStack, bufferSource,);
//
//		Vec3 desiredPos = legMap.targetCentroid(td, effectors());
//
//		drawLoc(desiredPos.toVector3f(), 0, 1, 0, poseStack, bufferSource);
//
//		for (int i = 0; i < legs.size(); i++) {
//			// leg map
//			VertexConsumer quad = bufferSource.getBuffer(RenderType.debugQuads());
//			Vec3 target = legMap().legTarget(i);
//			double tol = legMap().stepTolerance();
//			quad.addVertex(poseStack.last(), target.add(-tol, 0, -tol).toVector3f()).setColor(1.0f, 0.0f, 0.0f, 1.0f);
//			quad.addVertex(poseStack.last(), target.add(tol, 0, -tol).toVector3f()).setColor(1.0f, 0.0f, 0.0f, 1.0f);
//			quad.addVertex(poseStack.last(), target.add(tol, 0, tol).toVector3f()).setColor(1.0f, 0.0f, 0.0f, 1.0f);
//			quad.addVertex(poseStack.last(), target.add(-tol, 0, tol).toVector3f()).setColor(1.0f, 0.0f, 0.0f, 1.0f);
//			drawLoc(target.toVector3f(), 0, 0, 1, poseStack, bufferSource);
//			poseStack.pushPose();
//			{
//				poseStack.translate(target.x, target.y + 0.4, target.z);
//				poseStack.mulPose(Minecraft.getInstance().gameRenderer.getMainCamera().rotation());
//				poseStack.scale(0.03f, -0.03f, 0.03f);
//				Minecraft.getInstance().font.drawInBatch(String.valueOf(i + 1), 0.0F, 0.0F, -1, false, poseStack.last().pose(), bufferSource, Font.DisplayMode.NORMAL, 0, 15728880);
//			}
//			poseStack.popPose();
//
//			drawLoc(legs.get(i).getTickTarget().toVector3f(), 1, 1, 0, poseStack, bufferSource);
//		}
//
//		if (pathingTarget != null) {
//			// target, based on core pos
//			VertexConsumer targetLine = bufferSource.getBuffer(RenderType.debugLineStrip(4.0));
//			targetLine.addVertex(poseStack.last(), core.position().add(0, 1, 0).add(core.direction()).toVector3f())
//				.setColor(0.0f, 1.0f, 0.0f, 1.0f);
//			targetLine.addVertex(poseStack.last(), pathingTarget.toVector3f())
//				.setColor(0.0f, 1.0f, 0.0f, 1.0f);
//		}
//
////		core.hull.renderDebug(bufferSource, poseStack);
	}

	private static void drawLoc(Vector3f p, float r, float g, float b, PoseStack poseStack, MultiBufferSource bf) {
		VertexConsumer vc = bf.getBuffer(RenderType.debugLineStrip(4.0));
		vc.addVertex(poseStack.last(), p).setColor(r, g, b, 1.0f);
		vc.addVertex(poseStack.last(), p.add(0, 1, 0, new Vector3f()))
			.setColor(r, g, b, 1.0f);
	}

	private static void drawSeg(Vector3f p1, Vector3f p2, float r, float g, float b, PoseStack poseStack, MultiBufferSource bf, Vector4f color) {
		VertexConsumer bufferBuilder = bf.getBuffer(MechaEntityRenderer.LINE_STRIP);

//		BufferBuilder bufferBuilder = Tesselator.getInstance().begin(
//			VertexFormat.Mode.DEBUG_LINE_STRIP,
//			DefaultVertexFormat.POSITION_COLOR
//		);

		bufferBuilder.addVertex(poseStack.last(), p1).setColor(color.x, color.y, color.z, color.w);
		bufferBuilder.addVertex(poseStack.last(), p2).setColor(color.x, color.y, color.z, color.w);

//		BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
	}
}
