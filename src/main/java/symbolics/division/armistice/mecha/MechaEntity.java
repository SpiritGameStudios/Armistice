package symbolics.division.armistice.mecha;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import org.joml.Vector3f;
import symbolics.division.armistice.Armistice;
import symbolics.division.armistice.mecha.schematic.*;
import symbolics.division.armistice.registry.ArmisticeEntityDataSerializerRegistrar;
import symbolics.division.armistice.registry.ArmisticeOrdnanceRegistrar;
import symbolics.division.armistice.registry.ArmisticeRegistries;

import java.util.List;
import java.util.Optional;

public class MechaEntity extends Entity {
	public static final EntityDataAccessor<List<Vector3f>> LEG_TICK_TARGETS = SynchedEntityData.defineId(
		MechaEntity.class,
		ArmisticeEntityDataSerializerRegistrar.VEC3_LIST
	);

	protected static final EntityDataAccessor<Vector3f> CLIENT_POS = SynchedEntityData.defineId(
		MechaEntity.class,
		EntityDataSerializers.VECTOR3
	);

	protected static final EntityDataAccessor<Vector3f> CLIENT_DIR = SynchedEntityData.defineId(
		MechaEntity.class,
		EntityDataSerializers.VECTOR3
	);

	public static final EntityDataAccessor<List<Vector2f>> BARREL_ROTATIONS = SynchedEntityData.defineId(
		MechaEntity.class,
		ArmisticeEntityDataSerializerRegistrar.VEC2_LIST
	);

	protected static final EntityDataAccessor<Integer> HEAT = SynchedEntityData.defineId(
		MechaEntity.class,
		EntityDataSerializers.INT
	);

	protected final MechaCore core;
	private boolean firstTick = true;

	protected MechaEntity(EntityType<? extends Entity> entityType, Level level, MechaCore core) {
		super(entityType, level);
		this.core = core;
		setViewScale(10.0);
		this.noCulling = true;
	}

	public MechaEntity(EntityType<? extends Entity> entityType, Level level, MechaSchematic schematic) {
		this(entityType, level, schematic.make());
	}

	public static MechaEntity temp(EntityType<? extends Entity> entityType, Level level) {
		HullSchematic hull = ArmisticeRegistries.HULL.get(Armistice.id("test_hull"));
		ChassisSchematic chassis = ArmisticeRegistries.CHASSIS.get(Armistice.id("test_chassis"));
		List<OrdnanceSchematic> ordnance = List.of(ArmisticeOrdnanceRegistrar.MINIGUN);
		ArmorSchematic armor = ArmisticeRegistries.ARMOR.get(Armistice.id("test_armor"));
		return new MechaEntity(entityType, level, new MechaSchematic(hull, ordnance, chassis, armor));
	}

	@Override
	public void tick() {
		super.tick();
		if (firstTick) {
			firstTick = false;
			core.initCore(this);
		}

		if (this.level().isClientSide())
			core.clientTick(Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true));
		else core.serverTick();

		this.move(MoverType.SELF, this.getDeltaMovement());
	}

	public MechaCore core() {
		return core;
	}

	@Override
	protected void defineSynchedData(@NotNull SynchedEntityData.Builder builder) {
		builder.define(LEG_TICK_TARGETS, List.of());
		builder.define(CLIENT_POS, new Vector3f());
		builder.define(CLIENT_DIR, new Vector3f());
		builder.define(HEAT, 0);
		builder.define(BARREL_ROTATIONS, List.of());
	}

	@Override
	protected void readAdditionalSaveData(@NotNull CompoundTag compound) {

	}

	@Override
	protected void addAdditionalSaveData(@NotNull CompoundTag compound) {

	}

	@Override
	public boolean isPickable() {
		return true;
	}

	@NotNull
	@Override
	protected Vec3 getPassengerAttachmentPoint(@NotNull Entity entity, @NotNull EntityDimensions dimensions, float partialTick) {
		return Optional.ofNullable(core.model().seatOffset())
			.orElse(super.getPassengerAttachmentPoint(entity, dimensions, partialTick));
	}

	@NotNull
	@Override
	public InteractionResult interact(@NotNull Player player, @NotNull InteractionHand hand) {
		InteractionResult result = super.interact(player, hand);
		if (result.consumesAction()) return result;
		if (player.isSecondaryUseActive() || this.isVehicle()) return InteractionResult.PASS;

		if (this.level().isClientSide)
			return InteractionResult.SUCCESS;

		return player.startRiding(this) ? InteractionResult.CONSUME : InteractionResult.PASS;
	}
}
