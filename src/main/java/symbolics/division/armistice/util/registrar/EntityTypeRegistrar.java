package symbolics.division.armistice.util.registrar;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;

public interface EntityTypeRegistrar extends Registrar<EntityType<?>> {
	@Override
	default Class<EntityType<?>> getObjectType() {
		return Registrar.fixGenerics(EntityType.class);
	}

	@Override
	default Registry<EntityType<?>> getRegistry() {
		return BuiltInRegistries.ENTITY_TYPE;
	}
}
