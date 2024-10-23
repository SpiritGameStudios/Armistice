package symbolics.division.armistice.mecha;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
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

	public void tick() {
		// temp: debug pathing -----------
		Player player = level().getNearestPlayer(entity(), 100);
		if (player != null) {
			var eye = player.getEyePosition();
			var limit = eye.add(player.getLookAngle().scale(30));
			BlockHitResult raycast = level().clip(new ClipContext(
				player.getEyePosition(),
				limit,
				ClipContext.Block.OUTLINE,
				ClipContext.Fluid.NONE,
				player
			));
			if (raycast.getType() == HitResult.Type.BLOCK) {
				chassis.setPathingTarget(raycast.getLocation());
			}
		}
		// --------------------------------

		if (entity().level().isClientSide()) {
			float tickDelta = Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true);
			chassis.clientTick(tickDelta);
		} else {
			chassis.serverTick();
			entity().setPos(entity.position().add(acceleration().scale(0.1)));
		}

//		entity().lookAt(EntityAnchorArgument.Anchor.EYES, entity().getEyePosition().add(chassis.direction()));
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

	public MechaModelData model() {
		return model;
	}

	public MechaSchematic schematic() {
		return schematic;
	}

	@Override
	public Vector3fc absPos() {
		return position().toVector3f();
	}

	@Override
	public Quaternionfc absRot() {
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
}
