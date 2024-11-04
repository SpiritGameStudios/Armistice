package symbolics.division.armistice.projectile;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;
import symbolics.division.armistice.registry.ArmisticeEntityTypeRegistrar;

import javax.annotation.Nullable;

import java.util.Optional;
import java.util.UUID;

import static symbolics.division.armistice.Armistice.LOGGER;

public class Missile extends AbstractOrdnanceProjectile {

	private static final EntityDataAccessor<Byte> MISSILE_STATE = SynchedEntityData.defineId(Missile.class, EntityDataSerializers.BYTE);
	private static final EntityDataAccessor<Optional<UUID>> TARGET_UUID = SynchedEntityData.defineId(Missile.class, EntityDataSerializers.OPTIONAL_UUID);

	private MissileState cashedState;
	private Entity target;
	private int stateTimer;
	private int fuel;

	public Missile(EntityType<? extends Missile> entityType, Level level) {
		super(entityType, level);
		stateTimer = 0;
		fuel = 100; // 5 seconds
		switchState(MissileState.MISSING_TARGET);
	}

	public static Missile aimedMissile(Vec3 pos, @Nullable Entity owner, Entity target, float velocity) {
		Missile missile = new Missile(ArmisticeEntityTypeRegistrar.MISSILE, target.level());
		missile.setOwner(owner);
		missile.setPos(pos);
		missile.setTarget(target);
		missile.switchState(MissileState.LOCKED);
		Vec3 targetVec = target.position().subtract(pos).normalize();
		missile.shoot(targetVec.x(), targetVec.y(), targetVec.z(), velocity, 0F);
		return missile;
	}

	public void switchState(MissileState state) {
		stateTimer = 0;
		this.entityData.set(MISSILE_STATE, state.getID());
		this.cashedState = state;
		this.cashedState.enter(this);
	}

	public void setTarget(Entity target) {
		this.entityData.set(TARGET_UUID, target == null ? Optional.empty() : Optional.of(target.getUUID()));
		this.target = target;
	}

	public MissileState getState() {
		if (cashedState == null) {
			byte stateByte = this.entityData.get(MISSILE_STATE);
			// TODO: replace switch with proper MissileType mapping
			switch (stateByte) {
				case 0:
					cashedState = MissileState.MISSING_TARGET;
					break;
				case 1:
					cashedState = MissileState.LOCKED;
					break;
				case 2:
					cashedState = MissileState.OUT_OF_FUEL;
					break;
				default:
					LOGGER.warn("Unknown missile state id: {}", stateByte);
					cashedState = MissileState.MISSING_TARGET;
					break;
			}
		}
		return cashedState;
	}

	public Entity getTarget() {
		if (target == null) {
			entityData.get(TARGET_UUID).ifPresent(uuid1 -> {
				level().getEntities((Entity) null, AABB.INFINITE, entity -> (entity.getUUID() == uuid1)).stream().findFirst().ifPresentOrElse(entity -> {
					this.target = entity;
				}, () -> {
					entityData.set(TARGET_UUID, Optional.empty());
				});
			});
		}
		return target;
	}

	@Override
	public void tick() {
		super.tick();

//		stateTimer++;
		if (fuel <= 0 && getState() != MissileState.OUT_OF_FUEL) {
			switchState(MissileState.OUT_OF_FUEL);
		}

		getState().tick(this);
	}

	@Override
	protected void onHit(HitResult result) {
		super.onHit(result);
		if (level().isClientSide) return;
		LOGGER.debug("Missile hit! At {}", result.getLocation());

		Vec3 hitLocation = result.getLocation();
		level().broadcastEntityEvent(this, (byte)3);
		DamageSource damagesource = damageSources().explosion(this, getOwner());
		level().explode(this, damagesource, null, hitLocation.x(), hitLocation.y(), hitLocation.z(), 3.0F, false, Level.ExplosionInteraction.BLOCK);
	}

	@Override
	protected void onHitEntity(EntityHitResult result) {
		super.onHitEntity(result);
		LOGGER.debug("Missile entity hit!");
	}

	@Override
	protected void onHitBlock(BlockHitResult result) {
		super.onHitBlock(result);
		LOGGER.debug("Missile block hit!");
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		builder.define(MISSILE_STATE, (byte)0);
		builder.define(TARGET_UUID, Optional.empty());
	}

	public abstract static class MissileState {
		public void enter(Missile missile) {}
		public abstract void tick(Missile missile);
		public abstract byte getID();
		public static MissileState MISSING_TARGET = new MissileState() {
			@Override
			public void tick(Missile missile) {
				Vec3 vec3 = missile.getDeltaMovement();
				double d0 = missile.getX() + vec3.x;
				double d1 = missile.getY() + vec3.y;
				double d2 = missile.getZ() + vec3.z;
				missile.setPos(d0, d1, d2);
				missile.fuel--;

				Level level = missile.level();
				if (level.isClientSide()) {
					Vec3 particleVec = missile.getDeltaMovement().normalize().scale(-0.5);
					level.addAlwaysVisibleParticle(
						ParticleTypes.LAVA,
						missile.getX(),
						missile.getY(),
						missile.getZ(),
						particleVec.x(),
						particleVec.y(),
						particleVec.z()
					);
				}
			}

			@Override
			public byte getID() {
				return 0;
			}
		};
		public static MissileState LOCKED = new MissileState() {
			@Override
			public void tick(Missile missile) {
				Entity target = missile.getTarget();
				if (target == null ||
					target.isRemoved() ||
					!target.level().equals(missile.level()) ||
					missile.position().distanceToSqr(target.position()) > 2500 //TODO distance magicnum
				) {
					missile.setTarget(null);
					missile.switchState(MissileState.LOCKED);
				} else {

				}

				Vec3 vec3 = missile.getDeltaMovement();
				double d0 = missile.getX() + vec3.x;
				double d1 = missile.getY() + vec3.y;
				double d2 = missile.getZ() + vec3.z;
				missile.setPos(d0, d1, d2);
				missile.fuel--;

				Level level = missile.level();
				if (level.isClientSide()) {
					Vec3 particleVec = missile.getDeltaMovement().normalize().scale(-0.5);
					level.addAlwaysVisibleParticle(
						ParticleTypes.SOUL_FIRE_FLAME,
						missile.getX(),
						missile.getY(),
						missile.getZ(),
						particleVec.x(),
						particleVec.y(),
						particleVec.z()
					);
				}
			}

			@Override
			public byte getID() {
				return 1;
			}
		};
		public static MissileState OUT_OF_FUEL = new MissileState() {
			@Override
			public void tick(Missile missile) {
				Vec3 vec3 = missile.getDeltaMovement();
				double d0 = missile.getX() + vec3.x;
				double d1 = missile.getY() + vec3.y;
				double d2 = missile.getZ() + vec3.z;
				missile.applyGravity();
				missile.setPos(d0, d1, d2);

				Level level = missile.level();
				if (level.isClientSide() && level.getGameTime() % 3 == 0) {
					level.addAlwaysVisibleParticle(
						ParticleTypes.CAMPFIRE_SIGNAL_SMOKE,
						missile.getX(),
						missile.getY(),
						missile.getZ(),
						0,
						0,
						0
					);
				}
			}

			@Override
			public byte getID() {
				return 2;
			}
		};
	}
}
