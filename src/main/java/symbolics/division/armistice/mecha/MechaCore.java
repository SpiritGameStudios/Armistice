package symbolics.division.armistice.mecha;

import au.edu.federation.caliko.FabrikStructure3D;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.VisibleForTesting;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import symbolics.division.armistice.mecha.movement.Euclidean;
import symbolics.division.armistice.mecha.schematic.MechaSchematic;
import symbolics.division.armistice.model.MechaModelData;

import java.util.List;

/**
 * state holder and entity controller
 * <p>
 * parts are not visible to external objects, must provide all interfaces
 * to the rest of the system
 * <p>
 * updates model to reflect state.
 */
public class MechaCore implements Part {
	// codec contains schematic + state data for this specific instance
	// decoding reconstructs itself from the schem THEN applies state
	protected final MechaSchematic schematic;
	protected final ChassisPart chassis;
	protected final HullPart hull;
	protected final MechaModelData model;
	private MechaEntity entity = null;

	public MechaCore(MechaSchematic schematic) {
		this.schematic = schematic;
		this.chassis = schematic.chassis().make();
		this.hull = schematic.hull().make();
		this.model = new MechaModelData(schematic);
	}

	public void initCore(MechaEntity e) {
		this.entity = e;
		init(this);
	}

	@Override
	public void init(MechaCore core) {
		chassis.init(core);
	}

	@Override
	public void clientTick(float tickDelta) {
		Part.super.clientTick(tickDelta);

		chassis.clientTick(tickDelta);
	}

	@Override
	public void serverTick() {
		Part.super.serverTick();

		chassis.serverTick();
		entity.setDeltaMovement(acceleration());
	}

	@Override
	public void tick() {
		// region temp: debug pathing
		Player player = level().getNearestPlayer(entity(), 100);
		if (player != null) {
			Vec3 eye = player.getEyePosition();
			Vec3 limit = eye.add(player.getLookAngle().scale(30));
			BlockHitResult raycast = level().clip(new ClipContext(
				player.getEyePosition(),
				limit,
				ClipContext.Block.OUTLINE,
				ClipContext.Fluid.NONE,
				player
			));
			if (raycast.getType() == HitResult.Type.BLOCK) chassis.setPathingTarget(raycast.getLocation());
		}
		// endregion
	}

	@Override
	public Part parent() {
		// todo: better than null for now. consider alternatives.
		throw new RuntimeException("MechaCore has no parent part");
	}

	public Vec3 position() {
		return entity.position();
	}

	public Vec3 direction() {
		return chassis.direction();
	}

	public Level level() {
		return entity.level();
	}

	public MechaEntity entity() {
		return entity;
	}

	public Vec3 acceleration() {
		return chassis.movement();
	}

	public List<OrdnancePart> ordnance() {
		return hull.ordnance;
	}

	public int ordnanceIndex(OrdnancePart part) {
		return ordnance().indexOf(part);
	}

	public MechaModelData model() {
		return model;
	}

	public MechaSchematic schematic() {
		return schematic;
	}

	public boolean ready() {
		return hull.ready() && chassis.ready();
	}

	public int getHeat() {
		return hull.getHeat();
	}

	public int getMaxHeat() {
		return hull.getMaxHeat();
	}

	@Override
	public Vector3f absPos() {
		return position().toVector3f();
	}

	@Override
	public Quaternionf absRot() {
		return new Quaternionf().identity();
	}

	@Override
	public void renderDebug(MultiBufferSource bufferSource, PoseStack poseStack) {
		if (this.entity == null) return;

		VertexConsumer lineStrip4 = bufferSource.getBuffer(RenderType.debugLineStrip(4.0));

		Vec3 posUp = position().add(0, 1, 0);
		Vector3f adjustedDirection = posUp.add(direction()).toVector3f();

		lineStrip4.addVertex(poseStack.last(), posUp.toVector3f()).setColor(1.0f, 0.0f, 0.0f, 1.0f);
		lineStrip4.addVertex(poseStack.last(), adjustedDirection).setColor(1.0f, 0.0f, 0.0f, 1.0f);


		VertexConsumer lineStrip10 = bufferSource.getBuffer(RenderType.debugLineStrip(10));
		lineStrip10.addVertex(poseStack.last(), new Vector3f(0, 0, 0)).setColor(1.0f, 0.0f, 0.0f, 1.0f);
		lineStrip10.addVertex(poseStack.last(), direction().toVector3f()).setColor(1.0f, 0.0f, 0.0f, 1.0f);

		chassis.renderDebug(bufferSource, poseStack);
	}

	public Euclidean hullEuclidean() {
		return hull;
	}

	public Euclidean chassisEuclidean() {
		return chassis;
	}

	public Euclidean ordnanceEuclidean(int index) {
		return ordnance().get(index);
	}

	public FabrikStructure3D skeleton() {
		// temp: ONLY for rendering!
		return chassis.skeleton;
	}

	@VisibleForTesting
	public void setHeat(int heat) {
		hull.setHeat(heat);
	}
}
