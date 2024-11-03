package symbolics.division.armistice.mecha;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.joml.Vector3f;
import symbolics.division.armistice.mecha.movement.ChassisLeg;
import symbolics.division.armistice.mecha.movement.Euclidean;
import symbolics.division.armistice.mecha.schematic.MechaSchematic;
import symbolics.division.armistice.model.MechaModelData;
import symbolics.division.armistice.registry.ArmisticeSoundEventRegistrar;

import java.util.List;

import static symbolics.division.armistice.mecha.MechaEntity.BARREL_ROTATIONS;

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
	protected final MechaSkin skin;
	private MechaEntity entity = null;

	private int soundCooldown;

	public static final StreamCodec<ByteBuf, MechaCore> TO_CLIENT_STREAM_CODEC = StreamCodec.of(
		(buffer, value) -> {
			value.schematic.streamCodec().encode(buffer, value.schematic);
			MechaSkin.STREAM_CODEC.encode(buffer, value.skin);
		},
		buffer -> new MechaCore(
			ByteBufCodecs.fromCodec(MechaSchematic.CODEC).decode(buffer),
			MechaSkin.STREAM_CODEC.decode(buffer)
		)
	);

	public MechaCore(MechaSchematic schematic, @Nullable MechaSkin skin) {
		this.schematic = schematic;
		this.chassis = schematic.chassis().make();
		this.hull = schematic.hull().make();
		this.model = new MechaModelData(schematic);
		this.skin = skin == null ? MechaSkin.DEFAULT : skin;
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

		soundCooldown--;

		if (soundCooldown <= 0) {
			entity.playSound(
				entity.getRandom().nextBoolean() ? ArmisticeSoundEventRegistrar.AMBIENT$MECHA1 : ArmisticeSoundEventRegistrar.AMBIENT$MECHA2,
				1.0F,
				entity.getRandom().nextFloat() * (1.25F - 0.75F) + 0.75F
			);

			soundCooldown = entity.getRandom().nextIntBetweenInclusive(20 * 20, 40 * 20);
		}
	}

	@Override
	public void tick() {
		// region temp: debug pathing
//		Player player = level().getNearestPlayer(entity(), 100);
//		if (player != null) {
//			Vec3 eye = player.getEyePosition();
//			Vec3 limit = eye.add(player.getLookAngle().scale(30));
//			BlockHitResult raycast = level().clip(new ClipContext(
//				player.getEyePosition(),
//				limit,
//				ClipContext.Block.OUTLINE,
//				ClipContext.Fluid.NONE,
//				player
//			));
//			if (raycast.getType() == HitResult.Type.BLOCK) chassis.setPathingTarget(raycast.getLocation());
//		}
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

	public MechaSkin skin() {
		return skin;
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

	public Vector2fc ordnanceBarrelRotation(int index) {
		List<Vector2f> barrelRotations = entity().getEntityData().get(BARREL_ROTATIONS);
		if (index >= barrelRotations.size()) return new Vector2f();
		return barrelRotations.get(index);
	}

	public ChassisLeg leg(int leg) {
		// temp: ONLY for rendering!
		return chassis.legs.get(leg);
	}

	@VisibleForTesting
	public void setHeat(int heat) {
		hull.setHeat(heat);
	}

	public void setPathingTarget(Vector3f pos) {
		chassis.setPathingTarget(new Vec3(pos));
	}
}
