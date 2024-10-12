package symbolics.division.armistice.util.registrar;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.CreativeModeTab;

public interface CreativeModeTabRegistrar extends Registrar<CreativeModeTab> {
	@Override
	default Registry<CreativeModeTab> getRegistry() {
		return BuiltInRegistries.CREATIVE_MODE_TAB;
	}

	@Override
	default Class<CreativeModeTab> getObjectType() {
		return CreativeModeTab.class;
	}
}
