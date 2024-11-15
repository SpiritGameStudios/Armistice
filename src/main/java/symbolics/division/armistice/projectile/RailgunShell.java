package symbolics.division.armistice.projectile;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class RailgunShell extends ArtilleryShell {
	public RailgunShell(EntityType<? extends ArtilleryShell> entityType, Level level, float power) {
		super(entityType, level, power);
	}

//	@Override
//	protected void explode(DamageSource damageSource, Vec3 hitLocation) {
//				Explosion explosion = new Explosion(
//			this,
//			source,
//			damageSource,
//			damageCalculator,
//			x,
//			y,
//			z,
//			radius,
//			fire,
//			explosion$blockinteraction,
//			smallExplosionParticles,
//			largeExplosionParticles,
//			explosionSound
//		);
//		if (net.neoforged.neoforge.event.EventHooks.onExplosionStart(this, explosion)) return explosion;
//		explosion.explode();
//		explosion.finalizeExplosion(spawnParticles);
//	}
}
