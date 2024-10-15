package symbolics.division.armistice.registry;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import symbolics.division.armistice.mecha.MechaEntity;
import symbolics.division.armistice.util.registrar.EntityTypeRegistrar;

public final class ArmisticeEntityTypeRegistrar implements EntityTypeRegistrar {
	public static final EntityType<MechaEntity> MECHA = EntityType.Builder.of(
		MechaEntity::temp, MobCategory.MISC
	).build("peace_engine");

//	@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
//	public static final class Attributes {
//		@SubscribeEvent
//		public static void addDefaultAttributes(EntityAttributeCreationEvent event) {
//
//			event.put(MECHA, Mob.createMobAttributes().add(net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH, 8.0).add(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE, 4.0).add(FOLLOW_RANGE, 70).build());
//		}
//	}

}
