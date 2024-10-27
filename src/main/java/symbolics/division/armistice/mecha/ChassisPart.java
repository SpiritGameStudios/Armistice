package symbolics.division.armistice.mecha;

import au.edu.federation.caliko.FabrikBone3D;
import au.edu.federation.caliko.FabrikChain3D;
import au.edu.federation.caliko.FabrikStructure3D;
import au.edu.federation.utils.Vec3f;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import symbolics.division.armistice.mecha.movement.ChassisLeg;
import symbolics.division.armistice.mecha.movement.DirectionState;
import symbolics.division.armistice.mecha.movement.IKUtil;
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
	protected final double moveSpeed;
	protected List<ChassisLeg> legs;
	protected Vec3 movement = Vec3.ZERO;
	protected Vec3 pathingTarget = Vec3.ZERO;
	protected LegMap legMap;
	protected FabrikStructure3D skeleton;

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
		rootChain.setFixedBaseMode(false);
		rootChain.setGlobalHingedBasebone(
			IKUtil.Y_AXIS,
			180, 180, IKUtil.Z_AXIS
		);
//		rootChain.setFreelyRotatingGlobalHingedBasebone(IKUtil.Y_AXIS);

		this.skeleton.addChain(rootChain);
		IKUtil.configureDefaultChainSettings(this.skeleton.getChain(0));

		// root bone is like the "nose" of the skeleton, guides the center towards desired position.
		// each leg determines its own fixed rotation relative to the nose, and adds its chain to the skeleton.
		this.legs = new ArrayList<>();
		for (int i = 0; i < core.model().legInfo().size(); i++) {
			legs.add(new ChassisLeg(core.model().legInfo().get(i), this, i, skeleton));
		}
//		for (ChassisLeg leg : this.legs) {
//			this.skeleton.connectChain(leg.getChain(), 0, 0, BoneConnectionPoint.END);
//		}
	}

	@Override
	public void tick() {
		super.tick();
		// temp: need non horizontal-only movement
		// update desired movement
		if (pathingTarget != null && !pathingTarget.closerThan(new Vec3(absPos()), followTolerance)) {
			// if we're not facing the target, try to rotate towards it.
			Vec3 targetHorizontalDir = pathingTarget.subtract(core.position()).with(Direction.Axis.Y, 0).normalize();
			if (direction().dot(targetHorizontalDir) < 0.9) {
				// kindly ask out legs to rotate us
				float sign = (direction().yRot(Mth.PI / 2).dot(targetHorizontalDir) < 0) ? -1 : 1;
				legMap.setMapRotation(sign * Mth.PI / 6);

				direction.setTarget(targetHorizontalDir);
				if (!stepping()) {
					this.direction.tick();
				}
			} else {
				// otherwise we are facing it, so we ask our legs to move us towards it.
				// temp: set from model data
				float stepOffsetDistance = 1f;
				legMap.setMapOffset(new Vec3(0, 0, stepOffsetDistance));
				legMap.setMapRotation(0);
			}
		} else {
			// don't need to go anywhere, give our legs a rest
			legMap.setMapOffset(Vec3.ZERO);
			legMap.setMapRotation(0);
		}

		for (int i = 0; i < legs.size(); i++) {
			legs.get(i).tick();
		}
		var targetCenter = legMap.targetCentroid();

//		skeleton.solveForTarget(IKUtil.mc2fab(targetCenter));
		var p = absPos();
		float age = (float) core.entity().tickCount / 60;
		var tgt = new Vec3f(p.x, p.y, p.z);

		if (firstTick) {
			firstTick = false;
		} else {
			// bug in caliko: targeting an effector's base is undefined
			var baseBone = skeleton.getChain(0).getBone(0);
			Vec3f baseStart = baseBone.getStartLocation();
			if (baseStart.approximatelyEquals(tgt, 0.01f)) { // apply perturbation
				baseBone.setStartLocation(new Vec3f(baseStart.x + 0.01f, baseStart.y, baseStart.z));
			}

			if (true) {
				skeleton.solveForTarget(new Vec3f(p.x(), p.y(), p.z()));
			}

//			skeleton.solveForTarget(new Vec3f(p.x(), p.y(), p.z()));

			for (var leg : legs) {
				// live-update chain (caliko bug: fixedbase doesn't update on time)
//				leg.getChain().getBone(0).setStartLocation(baseBone.getEndLocation());
			}
		}
	}

	private boolean firstTick = true;

	@Override
	public void serverTick() {
		super.serverTick();

		core.hull.serverTick();

		// debug disable :PPPPP
//		if (!core.entity().onGround()) movement = movement.subtract(0, 0.1, 0);
//		else movement = new Vec3(movement.x, 0, movement.z);
	}

	@Override
	public void clientTick(float tickDelta) {
		super.clientTick(tickDelta);

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
//		return IKUtil.fab2mc(skeleton.getChain(0).getBone(0).getDirectionUV());
		return direction.curDir();
	}

	public List<Vec3> effectors() {
		return legs.stream().map(l -> IKUtil.fab2mc(l.getChain().getEffectorLocation())).toList();
	}

	@Override
	public void renderDebug(MultiBufferSource bufferSource, PoseStack poseStack) {
		for (int i = 0; i < skeleton.getNumChains(); i++) {
			var effectorLoc = skeleton.getChain(i).getEffectorLocation();
			poseStack.pushPose();
			{
				poseStack.translate(effectorLoc.x, effectorLoc.y, effectorLoc.z);
				poseStack.mulPose(Minecraft.getInstance().gameRenderer.getMainCamera().rotation());
				poseStack.scale(0.03f, -0.03f, 0.03f);
				Minecraft.getInstance().font.drawInBatch(String.valueOf(i), 0.0F, 0.0F, -1, false, poseStack.last().pose(), bufferSource, Font.DisplayMode.NORMAL, 0, 15728880);
			}
			poseStack.popPose();
			skeleton.getChain(i).getChain()
				.stream().forEach(
					bone -> {
						drawSeg(new Vector3f(bone.getStartLocationAsArray()), new Vector3f(bone.getEndLocationAsArray()), 1, 1, 1, poseStack, bufferSource);
					}
				);
		}

		// real center (shifted up to not hide target centroid)
		drawLoc(absPos().add(0, 1, 0), 1, 0, 0, poseStack, bufferSource);
		// target centroid
		drawLoc(legMap().targetCentroid().toVector3f(), 1, 1, 0, poseStack, bufferSource);

		// leg map
		for (int i = 0; i < legs.size(); i++) {
			VertexConsumer quad = bufferSource.getBuffer(RenderType.debugQuads());
			Vec3 target = legMap().legTarget(i);
			double tol = legMap().stepTolerance();
			quad.addVertex(poseStack.last(), target.add(-tol, 0, -tol).toVector3f()).setColor(1.0f, 0.0f, 0.0f, 1.0f);
			quad.addVertex(poseStack.last(), target.add(tol, 0, -tol).toVector3f()).setColor(1.0f, 0.0f, 0.0f, 1.0f);
			quad.addVertex(poseStack.last(), target.add(tol, 0, tol).toVector3f()).setColor(1.0f, 0.0f, 0.0f, 1.0f);
			quad.addVertex(poseStack.last(), target.add(-tol, 0, tol).toVector3f()).setColor(1.0f, 0.0f, 0.0f, 1.0f);
			drawLoc(target.toVector3f(), 0, 0, 1, poseStack, bufferSource);

			poseStack.pushPose();
			{
				poseStack.translate(target.x, target.y + 0.4, target.z);
				poseStack.mulPose(Minecraft.getInstance().gameRenderer.getMainCamera().rotation());
				poseStack.scale(0.03f, -0.03f, 0.03f);
				Minecraft.getInstance().font.drawInBatch(String.valueOf(i + 1), 0.0F, 0.0F, -1, false, poseStack.last().pose(), bufferSource, Font.DisplayMode.NORMAL, 0, 15728880);
			}
			poseStack.popPose();
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

	private static void drawSeg(Vector3f p1, Vector3f p2, float r, float g, float b, PoseStack poseStack, MultiBufferSource bf) {
		VertexConsumer vc = bf.getBuffer(RenderType.debugLineStrip(4.0));
		vc.addVertex(poseStack.last(), p1).setColor(r, g, b, 1.0f);
		vc.addVertex(poseStack.last(), p2).setColor(r, g, b, 1.0f);
	}
}
