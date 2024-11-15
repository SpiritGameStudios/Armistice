package symbolics.division.armistice.projectile;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class ArtilleryShell extends AbstractOrdnanceProjectile {

	protected final float power;

	public ArtilleryShell(EntityType<? extends ArtilleryShell> entityType, Level level, float power) {
		super(entityType, level);
		this.power = power;
	}

	@Override
	public void tick() {
		super.tick();

		// region Movement code
		Vec3 vec3 = this.getDeltaMovement();
		double d0 = this.getX() + vec3.x;
		double d1 = this.getY() + vec3.y;
		double d2 = this.getZ() + vec3.z;
		this.applyGravity();
		this.setPos(d0, d1, d2);
		// endregion

		if (random.nextFloat() < 0.25F)
			level().addAlwaysVisibleParticle(
				ParticleTypes.FLASH,
				true,
				getX(), getY(), getZ(),
				0, 0, 0
			);
	}

	@Override
	protected void onHit(HitResult result) {
		super.onHit(result);
		if (level().isClientSide) {
			for (int i = 0; i < 40; i++) {
				level().addAlwaysVisibleParticle(
					ParticleTypes.CAMPFIRE_SIGNAL_SMOKE,
					true,
					result.getLocation().x(),
					result.getLocation().y(),
					result.getLocation().z(),
					0.5 - getRandom().nextFloat(),
					1 + getRandom().nextFloat(),
					0.5 - getRandom().nextFloat()
				);
			}
		}

		Vec3 hitLocation = result.getLocation();
		level().broadcastEntityEvent(this, (byte) 3);
		DamageSource damageSource = damageSources().explosion(this, getOwner());
		explode(damageSource, hitLocation);
	}

	protected void explode(DamageSource damageSource, Vec3 hitLocation) {
		level().explode(this, damageSource, null, hitLocation.x(), hitLocation.y(), hitLocation.z(), power, false, Level.ExplosionInteraction.MOB);
	}

	@Override
	protected void defineSynchedData(@NotNull SynchedEntityData.Builder builder) {

	}
}
