package symbolics.division.armistice.projectile;

import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;

import static symbolics.division.armistice.Armistice.LOGGER;

public class ArtilleryShell extends Projectile {

	public ArtilleryShell(EntityType<? extends ArtilleryShell> entityType, Level level) {
		super(entityType, level);
	}

	@Override
	public void tick() {
		super.tick();

		HitResult hitresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
		if (hitresult.getType() != HitResult.Type.MISS && !EventHooks.onProjectileImpact(this, hitresult)) {
			this.hitTargetOrDeflectSelf(hitresult);
		}

		Vec3 vec3 = this.getDeltaMovement();
		double d0 = this.getX() + vec3.x;
		double d1 = this.getY() + vec3.y;
		double d2 = this.getZ() + vec3.z;
		this.applyGravity();
		this.setPos(d0, d1, d2);
	}

	@Override
	protected void onHit(HitResult result) {
		super.onHit(result);
		if (level().isClientSide) return;
		LOGGER.debug("Shell hit! At {}", result.getLocation());
		level().broadcastEntityEvent(this, (byte)3);
		DamageSource damagesource = damageSources().explosion(this, getOwner());
		level().explode(this, damagesource, null, getX(), getY(), getZ(), 3.0F, false, Level.ExplosionInteraction.BLOCK);
		discard();
	}

	@Override
	protected void onHitEntity(EntityHitResult result) {
		super.onHitEntity(result);
		LOGGER.debug("Shell entity hit!");
	}

	@Override
	protected void onHitBlock(BlockHitResult result) {
		super.onHitBlock(result);
//		level().setBlock(result.getBlockPos(), Blocks.MAGMA_BLOCK.defaultBlockState(), 11);
		LOGGER.debug("Shell block hit!");
	}

	@Override
	public boolean canUsePortal(boolean allowPassengers) {
		return false;
	}

	@Override
	protected double getDefaultGravity() {
		return 0.05d;
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {

	}
}
