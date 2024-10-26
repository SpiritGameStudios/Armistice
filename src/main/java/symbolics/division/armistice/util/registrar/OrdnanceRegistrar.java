package symbolics.division.armistice.util.registrar;

import net.minecraft.core.Registry;
import symbolics.division.armistice.mecha.schematic.OrdnanceSchematic;
import symbolics.division.armistice.registry.ArmisticeRegistries;

public interface OrdnanceRegistrar extends Registrar<OrdnanceSchematic> {
	@Override
	default Class<OrdnanceSchematic> getObjectType() {
		return OrdnanceSchematic.class;
	}

	@Override
	default Registry<OrdnanceSchematic> getRegistry() {
		return ArmisticeRegistries.ORDNANCE;
	}
}
