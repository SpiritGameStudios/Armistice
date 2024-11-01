package symbolics.division.armistice.mecha.ordnance;

import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3fc;
import symbolics.division.armistice.debug.ArmisticeDebugValues;
import symbolics.division.armistice.mecha.MechaCore;
import symbolics.division.armistice.mecha.OrdnancePart;
import symbolics.division.armistice.model.MechaModelData;

public class SimpleGunOrdnance extends OrdnancePart {
	protected final int cooldown;
	protected final double maxDistance;
	protected final double projectileVelocity;

	protected int cooldownTicks;
	protected MechaModelData.MarkerInfo barrelMarker;

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
	public void init(MechaCore core) {
		super.init(core);

		barrelMarker = core.model().ordnanceInfo(this, core).markers().get(1);
	}

	@Override
	public void serverTick() {
		super.serverTick();

		if (!ArmisticeDebugValues.simpleGun) return;

		// region temp: debug targeting
		Player player = core.level().getNearestPlayer(core.entity(), 100);
		if (player != null) {
			HitResult result = new EntityHitResult(player);
			startTargeting(result);
		}
		// endregion

		cooldownTicks--;
		if (cooldownTicks > 0 || targets().isEmpty() || !(targets().getFirst() instanceof EntityHitResult target))
			return;

		// temp: inappropriate use of rotationmanager. also, try to apply logic to ordnance in general.
		MechaModelData.OrdnanceInfo info = core.model().ordnanceInfo(this, core);

		// NOT A SAFE ASSUMPTION. the body may not always be centered on origin (though it should)
		var barrelLength = barrelMarker.origin().with(Direction.Axis.Y, 0).length();
		var baseRotation = info.mountPoint().rotationInfo().bbRotation().scale(Mth.DEG_TO_RAD);

		Vec3 evilBodyOffsetPleaseUpdateModelData = info.body().origin();

		Vec3 absBody = new Vec3(rel2Abs(
			new Quaternionf().rotateZYX(
				(float) baseRotation.z, (float) baseRotation.y, (float) baseRotation.x
			).transform(evilBodyOffsetPleaseUpdateModelData.toVector3f())
		));

		Vec3 idealBarrelDir = target.getEntity().position().subtract(absBody).normalize().scale(barrelLength);
		Vec3 idealBarrelTipPos = absBody.add(idealBarrelDir);
		Entity projectile = createProjectile(idealBarrelTipPos.toVector3f());

		double x = target.getEntity().getX() - idealBarrelTipPos.x;
		double z = target.getEntity().getZ() - idealBarrelTipPos.z;

		double horizontalDist = Math.sqrt(x * x + z * z);

		double y = (target.getEntity().getY(1.0 / 3.0) - projectile.getY()) + Math.abs(horizontalDist) * (projectile.getGravity() * 5);

		// temp: rotation manager example
		Vec3 desiredDir = new Vec3(x, y, z).normalize();

		rotationManager.setTarget(idealBarrelTipPos.add(idealBarrelDir));
		rotationManager.tick();

		// you can constrain it by angle, dot product, whatever
		// one problem arises where it solves then tries to calc vector. I'm not sure
		// if this is the correct order to do the check on whether it will be able to fire.
		// it should also check if it would hit itself with the gun (though rotations should normally
		// prevent that, and self-spawned projectiles should phase through us)
		Vec3 currentDirection = rotationManager.currentDirection();
		if (currentDirection.dot(desiredDir) < 0.95) return;

		Vec3 velocity = desiredDir
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

	public Entity createProjectile(Vector3fc pos) {
		return new Snowball(core.level(), pos.x(), pos.y(), pos.z());
	}
}
