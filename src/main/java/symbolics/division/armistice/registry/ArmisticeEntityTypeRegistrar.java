package symbolics.division.armistice.registry;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import symbolics.division.armistice.mecha.MechaEntity;
import symbolics.division.armistice.projectile.ArtilleryShell;
import symbolics.division.armistice.projectile.Missile;
import symbolics.division.armistice.util.registrar.EntityTypeRegistrar;

@SuppressWarnings("unused")
public final class ArmisticeEntityTypeRegistrar implements EntityTypeRegistrar {
	public static final EntityType<MechaEntity> MECHA = EntityType.Builder.of(
		MechaEntity::temp, MobCategory.MISC
	).sized(5, 5).updateInterval(1).build("mecha");

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
}
