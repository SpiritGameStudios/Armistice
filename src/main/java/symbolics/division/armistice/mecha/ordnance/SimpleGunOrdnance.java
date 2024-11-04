package symbolics.division.armistice.mecha.ordnance;

import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import symbolics.division.armistice.debug.ArmisticeDebugValues;
import symbolics.division.armistice.math.OrdnanceFireInfo;
import symbolics.division.armistice.mecha.MechaCore;
import symbolics.division.armistice.mecha.OrdnancePart;
import symbolics.division.armistice.model.MechaModelData;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class SimpleGunOrdnance extends OrdnancePart {
	protected final int cooldown;
	protected final double maxDistance;
	protected final double projectileVelocity;
	protected final BiFunction<MechaCore, OrdnanceFireInfo, Entity> projectileCreator;
	protected final BiConsumer<MechaCore, OrdnanceFireInfo> onShoot;

	protected int cooldownTicks;
	protected MechaModelData.MarkerInfo barrelMarker;
	protected final int heatPerShot;
	protected int heatThisTick = 0;

	public SimpleGunOrdnance(int heatPerShot, int cooldown, double maxDistance, double projectileVelocity, BiFunction<MechaCore, OrdnanceFireInfo, Entity> projectileCreator, BiConsumer<MechaCore, OrdnanceFireInfo> onShoot) {
		super(1);

		this.heatPerShot = heatPerShot;
		this.cooldown = cooldown;
		this.maxDistance = maxDistance;
		this.projectileVelocity = projectileVelocity;
		this.projectileCreator = projectileCreator;
		this.onShoot = onShoot;
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
//		Player player = core.level().getNearestPlayer(core.entity(), 100);
//		if (player != null) {
//			HitResult result = new EntityHitResult(player);
//			startTargeting(result);
//		}
		// endregion

		cooldownTicks--;
		if (targets().isEmpty())
			return;

		HitResult target = targets().getFirst();
		Vec3 targetPos = target instanceof EntityHitResult entity ? entity.getLocation().multiply(1, 1F / 3F, 1) : target.getLocation();

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

		Vec3 idealBarrelDir = targetPos.subtract(absBody).normalize().scale(barrelLength);
		Vec3 idealBarrelTipPos = absBody.add(idealBarrelDir);
		Entity projectile = projectileCreator.apply(
			core,
			new OrdnanceFireInfo(
				idealBarrelTipPos.toVector3f(),
				idealBarrelDir.toVector3f(),
				new Quaternionf(),
				target
			)
		);

		double x = targetPos.x() - idealBarrelTipPos.x;
		double z = targetPos.z() - idealBarrelTipPos.z;

		double horizontalDist = Math.sqrt(x * x + z * z);

		double y = (targetPos.y() - projectile.getY()) + Math.abs(horizontalDist) * (projectile.getGravity() * 5);

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
		if (currentDirection.dot(desiredDir) < 0.95 || cooldownTicks > 0) return;

		if (projectileVelocity > 0) {
			Vec3 velocity = desiredDir
				.scale(projectileVelocity);

			projectile.setDeltaMovement(velocity);
			projectile.hasImpulse = true;

			projectile.setYRot((float) (Mth.atan2(velocity.x, velocity.z) * Mth.RAD_TO_DEG));
			projectile.setXRot((float) (Mth.atan2(velocity.y, horizontalDist) * Mth.RAD_TO_DEG));
			projectile.yRotO = projectile.getYRot();
			projectile.xRotO = projectile.getXRot();
		}
		
		core.level().addFreshEntity(projectile);
		onShoot.accept(
			core,
			new OrdnanceFireInfo(
				idealBarrelTipPos.toVector3f(),
				idealBarrelDir.toVector3f(),
				new Quaternionf(),
				target
			)
		);

		cooldownTicks = cooldown;
		heatThisTick = heatPerShot;
	}

	@Override
	public int heat() {
		int out = heatThisTick;
		heatThisTick = 0;
		return heatThisTick;
	}
}
