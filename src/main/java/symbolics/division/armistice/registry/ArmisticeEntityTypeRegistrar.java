package symbolics.division.armistice.registry;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import symbolics.division.armistice.mecha.MechaEntity;
import symbolics.division.armistice.util.registrar.EntityTypeRegistrar;

public final class ArmisticeEntityTypeRegistrar implements EntityTypeRegistrar {
	public static final EntityType<MechaEntity> MECHA = EntityType.Builder.of(
		MechaEntity::temp, MobCategory.MISC
	).build("peace_engine");

	@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
	public static final class Attributes {
		@SubscribeEvent
		public static void addDefaultAttributes(EntityAttributeCreationEvent event) {
			event.put(MECHA, Mob.createMobAttributes().build());
		}
	}

}
