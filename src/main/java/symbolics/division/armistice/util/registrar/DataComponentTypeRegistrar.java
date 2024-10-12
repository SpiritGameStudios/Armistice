package symbolics.division.armistice.util.registrar;

import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;

public interface DataComponentTypeRegistrar extends Registrar<DataComponentType<?>> {
	@Override
	default Class<DataComponentType<?>> getObjectType() {
		return Registrar.fixGenerics(DataComponentType.class);
	}

	@Override
	default Registry<DataComponentType<?>> getRegistry() {
		return BuiltInRegistries.DATA_COMPONENT_TYPE;
	}
}
