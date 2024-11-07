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

	public ArtilleryShell(EntityType<? extends ArtilleryShell> entityType, Level level) {
		super(entityType, level);
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
			level().addParticle(
				ParticleTypes.FLASH,
				getX(), getY(), getZ(),
				0, 0, 0
			);
	}

	@Override
	protected void onHit(HitResult result) {
		super.onHit(result);
		if (level().isClientSide) return;

		Vec3 hitLocation = result.getLocation();
		level().broadcastEntityEvent(this, (byte) 3);
		DamageSource damagesource = damageSources().explosion(this, getOwner());
		level().explode(this, damagesource, null, hitLocation.x(), hitLocation.y(), hitLocation.z(), 10.0F, false, Level.ExplosionInteraction.BLOCK);
	}

	@Override
	protected void defineSynchedData(@NotNull SynchedEntityData.Builder builder) {

	}
}
