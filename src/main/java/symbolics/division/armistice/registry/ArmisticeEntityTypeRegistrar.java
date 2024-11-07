package symbolics.division.armistice.registry;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import symbolics.division.armistice.mecha.MechaEntity;
import symbolics.division.armistice.projectile.ArtilleryShell;
import symbolics.division.armistice.projectile.HitscanBullet;
import symbolics.division.armistice.projectile.Missile;
import symbolics.division.armistice.util.registrar.EntityTypeRegistrar;

@SuppressWarnings("unused")
public final class ArmisticeEntityTypeRegistrar implements EntityTypeRegistrar {
	public static final EntityType<MechaEntity> MECHA = EntityType.Builder.of(
		MechaEntity::temp, MobCategory.MISC
	).sized(5, 5).updateInterval(1).noSummon().build("mecha");

	public static final EntityType<ArtilleryShell> ARTILLERY_SHELL = EntityType.Builder.of(
			ArtilleryShell::new, MobCategory.MISC
		)
		.sized(0.5F, 0.5F)
		.eyeHeight(0.13F)
		.clientTrackingRange(4)
		.updateInterval(2)
		.build("artillery_shell");

	public static final EntityType<Missile> MISSILE = EntityType.Builder.of(
			Missile::new, MobCategory.MISC
		)
		.sized(0.5F, 0.5F)
		.eyeHeight(0.13F)
		.clientTrackingRange(4)
		.updateInterval(2)
		.build("missile");

	public static final EntityType<HitscanBullet> HITSCAN_BULLET = EntityType.Builder.<HitscanBullet>of(
			HitscanBullet::new, MobCategory.MISC
		)
		.sized(0.1f, 0.1f)
		.eyeHeight(0.05f)
		.build("hitscan_bullet");
}
