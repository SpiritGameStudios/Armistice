package symbolics.division.armistice.mecha;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;
import org.joml.Vector3f;
import symbolics.division.armistice.Armistice;
import symbolics.division.armistice.mecha.schematic.*;
import symbolics.division.armistice.registry.ArmisticeEntityDataSerializerRegistrar;
import symbolics.division.armistice.registry.ArmisticeOrdnanceRegistrar;
import symbolics.division.armistice.registry.ArmisticeRegistries;
import symbolics.division.armistice.registry.ArmisticeSoundEventRegistrar;
import symbolics.division.armistice.util.AudioUtil;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

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

	public static final EntityDataAccessor<Map<Integer, Vector2f>> BARREL_ROTATIONS = SynchedEntityData.defineId(
		MechaEntity.class,
		ArmisticeEntityDataSerializerRegistrar.INT_VEC2_MAP
	);

	protected static final EntityDataAccessor<Integer> HEAT = SynchedEntityData.defineId(
		MechaEntity.class,
		EntityDataSerializers.INT
	);

	protected static final EntityDataAccessor<MechaCore> CORE = SynchedEntityData.defineId(
		MechaEntity.class,
		ArmisticeEntityDataSerializerRegistrar.CORE
	);

	protected boolean morality = true;

	protected MechaEntity(EntityType<? extends Entity> entityType, Level level, MechaCore core) {
		super(entityType, level);
		getEntityData().set(CORE, core);
		setViewScale(10.0);
		this.noCulling = true;
	}

	public MechaEntity(EntityType<? extends Entity> entityType, Level level, MechaSchematic schematic, @Nullable MechaSkin skin) {
		this(entityType, level, schematic.make(skin));
	}

	public static MechaEntity temp(EntityType<? extends Entity> entityType, Level level) {
		RegistryAccess access = level.registryAccess();

		HullSchematic hull = access.registryOrThrow(ArmisticeRegistries.HULL_KEY).get(Armistice.id("depth"));
		ChassisSchematic chassis = access.registryOrThrow(ArmisticeRegistries.CHASSIS_KEY).get(Armistice.id("nimble"));
		List<OrdnanceSchematic> ordnance = List.of(ArmisticeOrdnanceRegistrar.MINIGUN);
		ArmorSchematic armor = access.registryOrThrow(ArmisticeRegistries.ARMOR_KEY).get(Armistice.id("armisteel"));

		return new MechaEntity(entityType, level, new MechaCore(new MechaSchematic(hull, ordnance, chassis, armor), null));
	}

	@Override
	public void tick() {
		super.tick();
		if (core().entity() == null) core().initCore(this);

		if (!morality) {
			crueltyEngineTick();
		}

		if (this.level().isClientSide())
			core().clientTick(Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true));
		else core().serverTick();

		this.move(MoverType.SELF, this.getDeltaMovement());
	}

	public MechaCore core() {
		return getEntityData().get(CORE);
	}

	@Override
	protected void defineSynchedData(@NotNull SynchedEntityData.Builder builder) {
		RegistryAccess access = level().registryAccess();

		HullSchematic hull = access.registryOrThrow(ArmisticeRegistries.HULL_KEY).get(Armistice.id("depth"));
		ChassisSchematic chassis = access.registryOrThrow(ArmisticeRegistries.CHASSIS_KEY).get(Armistice.id("nimble"));
		List<OrdnanceSchematic> ordnance = List.of(ArmisticeOrdnanceRegistrar.MINIGUN);
		ArmorSchematic armor = access.registryOrThrow(ArmisticeRegistries.ARMOR_KEY).get(Armistice.id("armisteel"));

		builder.define(LEG_TICK_TARGETS, List.of());
		builder.define(CLIENT_POS, new Vector3f());
		builder.define(CLIENT_DIR, new Vector3f());
		builder.define(HEAT, 0);
		builder.define(CORE, new MechaCore(new MechaSchematic(hull, ordnance, chassis, armor), null));
		builder.define(BARREL_ROTATIONS, new Int2ObjectArrayMap<>());
	}

	@Override
	protected void readAdditionalSaveData(@NotNull CompoundTag compound) {
		if (compound.contains("core")) {
			MechaCore core = new MechaCore(
				MechaSchematic.CODEC.decode(NbtOps.INSTANCE, compound.get("core")).getOrThrow().getFirst(),
				MechaSkin.CODEC.decode(NbtOps.INSTANCE, compound.get("skin")).map(Pair::getFirst).result().orElse(null)
			);

			getEntityData().set(CORE, core);
		}

		if (compound.contains("morality")) {
			morality = compound.getBoolean("morality");
		}
	}

	@Override
	protected void addAdditionalSaveData(@NotNull CompoundTag compound) {
		compound.put("skin", MechaSkin.CODEC.encodeStart(NbtOps.INSTANCE, core().skin()).getOrThrow());
		compound.put("core", MechaSchematic.CODEC.encodeStart(NbtOps.INSTANCE, core().schematic).getOrThrow());
		compound.putBoolean("morality", morality);
	}

	@Override
	public boolean isPickable() {
		return true;
	}

	@NotNull
	@Override
	protected Vec3 getPassengerAttachmentPoint(@NotNull Entity entity, @NotNull EntityDimensions dimensions, float partialTick) {
		return Optional.ofNullable(core().model().seatOffset())
			.orElse(super.getPassengerAttachmentPoint(entity, dimensions, partialTick));
	}

	@NotNull
	@Override
	public InteractionResult interact(@NotNull Player player, @NotNull InteractionHand hand) {
		InteractionResult result = super.interact(player, hand);
		if (result.consumesAction()) return result;
		if (player.isSecondaryUseActive() || this.isVehicle() || !morality) return InteractionResult.PASS;

		if (this.level().isClientSide)
			return InteractionResult.SUCCESS;

		return player.startRiding(this) ? InteractionResult.CONSUME : InteractionResult.PASS;
	}

	protected enum CrueltyMode {
		ROAM, SPY, KILL
	}

	protected CrueltyMode crueltyMode = CrueltyMode.ROAM;
	protected int modeTicks = 10000;
	//	protected TargetingConditions TARGET_CONDITIONS = TargetingConditions.forCombat().range(200);
	protected UUID fixation = null;
	protected int ticksSincePlayerSeen = 0;

	protected static int SPY_TICKS = 20 * 6;
	protected int KILL_TICKS = 20 * 60;

	protected void crueltyEngineTick() {
		if (level().isClientSide) return;
		var serverLevel = (ServerLevel) level();
		modeTicks++;
		switch (crueltyMode) {
			case ROAM -> {
				if (modeTicks > 20 * 120) {
					modeTicks = 0;
					core().setPathingTarget(new Vector3f((float) getRandomX(1000), (float) getY() + 50, (float) getRandomZ(1000)));
				}
//				level().getNearbyPlayers(TARGET_CONDITIONS, this, new AABB(this.getX()-200, this.getY()-50, this.getZ()-200, this.getX()+200, this.getY()+50,this.getZ()+200)))
				for (var player : serverLevel.getPlayers(p -> validCrueltyTarget(p) && p.distanceTo(this) < 200 && p.hasLineOfSight(this), 1)) {
					crueltyMode = CrueltyMode.SPY;
					fixation = player.getUUID();
					modeTicks = 0;
					core().setPathingTarget(position().toVector3f());
					playSound(ArmisticeSoundEventRegistrar.ENTITY$MECHA$ALERT, 5, AudioUtil.randomizedPitch(random, 1, 0.2f));
				}
			}
			case SPY -> {
				if (modeTicks < SPY_TICKS) return;
				if (fixation != null && level().getPlayerByUUID(fixation).hasLineOfSight(this) && validCrueltyTarget(level().getPlayerByUUID(fixation))) {
					playSound(ArmisticeSoundEventRegistrar.ENTITY$MECHA$ALERT, 5, AudioUtil.randomizedPitch(random, 1, 0.2f));
					crueltyMode = CrueltyMode.KILL;
					modeTicks = 0;
					ticksSincePlayerSeen = 0;
				} else {
					crueltyMode = CrueltyMode.ROAM;
					modeTicks = 10000;
				}
			}
			case KILL -> {
				var targetPlayer = level().getPlayerByUUID(fixation);
				if (targetPlayer != null && validCrueltyTarget(targetPlayer)) {
					if (!targetPlayer.hasLineOfSight(this)) {
						ticksSincePlayerSeen++;
						if (ticksSincePlayerSeen > KILL_TICKS) {
							crueltyMode = CrueltyMode.ROAM;
							modeTicks = 10000;
						}
						core().clearAllOrdnanceTargets();
						return;
					}
					ticksSincePlayerSeen = 0;
					if (tickCount % 2 == 0) {
						core().setPathingTarget(targetPlayer.position().toVector3f());
						for (var ord : core().ordnance()) {
							ord.startTargeting(new EntityHitResult(targetPlayer));
						}
					}

				} else {
					crueltyMode = CrueltyMode.ROAM;
					modeTicks = 10000;
				}
			}
		}
	}

	public static boolean validCrueltyTarget(Player player) {
		return !player.isDeadOrDying() && !player.isCreative() && !player.isSpectator();
	}
}
