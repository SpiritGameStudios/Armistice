package symbolics.division.armistice.registry;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import symbolics.division.armistice.mecha.MechaEntity;
import symbolics.division.armistice.util.registrar.EntityTypeRegistrar;

@SuppressWarnings("unused")
public final class ArmisticeEntityTypeRegistrar implements EntityTypeRegistrar {
	public static final EntityType<MechaEntity> MECHA = EntityType.Builder.of(
		MechaEntity::temp, MobCategory.MISC
	).sized(5, 5).updateInterval(1).build("mecha");
}
