package symbolics.division.armistice.mecha;

import au.edu.federation.caliko.FabrikStructure3D;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.joml.Vector3f;
import symbolics.division.armistice.client.sound.CockpitSoundInstance;
import symbolics.division.armistice.mecha.movement.ChassisLeg;
import symbolics.division.armistice.mecha.movement.Euclidean;
import symbolics.division.armistice.mecha.schematic.MechaSchematic;
import symbolics.division.armistice.model.MechaModelData;
import symbolics.division.armistice.registry.ArmisticeSoundEventRegistrar;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

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
	private CockpitSoundInstance clientSound = null;

	public static final StreamCodec<RegistryFriendlyByteBuf, MechaCore> TO_CLIENT_STREAM_CODEC = StreamCodec.of(
		(buffer, value) -> {
			MechaSchematic.STREAM_CODEC.encode(buffer, value.schematic);
			MechaSkin.STREAM_CODEC.encode(buffer, value.skin);
		},
		buffer -> new MechaCore(
			MechaSchematic.STREAM_CODEC.decode(buffer),
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
	@OnlyIn(Dist.CLIENT)
	public void clientTick(float tickDelta) {
		Part.super.clientTick(tickDelta);

		chassis.clientTick(tickDelta);

		Player player = Minecraft.getInstance().player;
		Objects.requireNonNull(player);

		if (entity.hasPassenger(player)) {
			// play interior sound
			if (clientSound == null || clientSound.isStopped()) {
				Minecraft.getInstance().getSoundManager().stop();
				clientSound = new CockpitSoundInstance(entity, player, 0.35f, 1, entity.getRandom());
				Minecraft.getInstance().getSoundManager().play(
					clientSound
				);
			}
		} else {
			if (clientSound != null) {
				clientSound = null;
			}
			soundCooldown--;
			if (soundCooldown <= 0) {
				if (player.distanceTo(entity) <= 40) {
					Minecraft.getInstance().getSoundManager().play(
						new SimpleSoundInstance(ArmisticeSoundEventRegistrar.AMBIENT$GEIGER, SoundSource.NEUTRAL, 4, entity.getRandom().nextFloat() * (1.25F - 0.75F) + 0.75F, entity.getRandom(), entity.blockPosition())
					);
				} else {
					Minecraft.getInstance().getSoundManager().play(
						new SimpleSoundInstance(
							entity.getRandom().nextBoolean() ? ArmisticeSoundEventRegistrar.AMBIENT$MECHA1 : ArmisticeSoundEventRegistrar.AMBIENT$MECHA2,
							SoundSource.NEUTRAL, 5, entity.getRandom().nextFloat() * (1.25F - 0.75F) + 0.75F, entity.getRandom(), entity.blockPosition())
					);
				}
				soundCooldown = entity.getRandom().nextIntBetweenInclusive(20 * 50, 20 * 80);
			}
		}

	}

	@Override
	public void serverTick() {
		Part.super.serverTick();

		chassis.serverTick();
		entity.setDeltaMovement(acceleration());
	}

	@Override
	public void tick() {

	}

	@Override
	public Part parent() {
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
		return chassis.acceleration();
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
		Map<Integer, Vector2f> barrelRotations = entity().getEntityData().get(BARREL_ROTATIONS);
		if (!barrelRotations.containsKey(index)) return new Vector2f();
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

	@Nullable
	public Vec3 getPathingTarget() {
		return chassis.pathingTarget;
	}

	public void mapChassisRender(Consumer<FabrikStructure3D> consumer) {
		consumer.accept(chassis.skeleton);
	}

	public void clearAllOrdnanceTargets() {
		for (var ord : ordnance()) {
			ord.clearTargets();
		}
	}

	public boolean overheatingDanger(int nextHeat) {
		return (nextHeat + getHeat()) >= getMaxHeat() * 0.9;
	}
}
