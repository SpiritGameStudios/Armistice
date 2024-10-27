package symbolics.division.armistice.mecha.ordnance;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import symbolics.division.armistice.debug.ArmisticeDebugValues;
import symbolics.division.armistice.mecha.OrdnancePart;

public class SimpleGunOrdnance extends OrdnancePart {
	protected final int cooldown;
	protected final double maxDistance;
	protected final double projectileVelocity;

	protected int cooldownTicks;

	public SimpleGunOrdnance(int cooldown, double maxDistance, double projectileVelocity) {
		super(1);

		this.cooldown = cooldown;
		this.maxDistance = maxDistance;
		this.projectileVelocity = projectileVelocity;
	}

	@Override
	protected boolean isValidTarget(HitResult hitResult) {
		return !(hitResult.distanceTo(core.entity()) > maxDistance * maxDistance);
	}

	@Override
	public void serverTick() {
		super.serverTick();

		// region temp: debug targeting
		Player player = core.level().getNearestPlayer(core.entity(), 100);
		if (player != null) {
			HitResult result = new EntityHitResult(player);
			startTargeting(result);
		}
		// endregion

		cooldownTicks--;
		if (!ArmisticeDebugValues.simpleGun || cooldownTicks > 0 || targets().isEmpty() || !(targets().getFirst() instanceof EntityHitResult target))
			return;

		Entity projectile = createProjectile();

		double x = target.getEntity().getX() - absPos().x;
		double z = target.getEntity().getZ() - absPos().z;

		double horizontalDist = Math.sqrt(x * x + z * z);

		double y = (target.getEntity().getY(1.0 / 3.0) - projectile.getY()) + horizontalDist * (projectile.getGravity() * 5);

		Vec3 velocity = new Vec3(x, y, z)
			.normalize()
			.scale(projectileVelocity);

		projectile.setDeltaMovement(velocity);
		projectile.hasImpulse = true;

		projectile.setYRot((float) (Mth.atan2(velocity.x, velocity.z) * Mth.RAD_TO_DEG));
		projectile.setXRot((float) (Mth.atan2(velocity.y, horizontalDist) * Mth.RAD_TO_DEG));
		projectile.yRotO = projectile.getYRot();
		projectile.xRotO = projectile.getXRot();

		core.level().addFreshEntity(projectile);

		cooldownTicks = cooldown;
	}

	public Entity createProjectile() {
		return new Snowball(core.level(), absPos().x, absPos().y, absPos().z);
	}
}
