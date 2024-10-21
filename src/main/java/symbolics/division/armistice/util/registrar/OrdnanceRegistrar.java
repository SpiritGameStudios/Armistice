package symbolics.division.armistice.util.registrar;

import net.minecraft.core.Registry;
import symbolics.division.armistice.mecha.schematic.OrdnanceSchematic;
import symbolics.division.armistice.registry.ArmisticeRegistries;

public class OrdnanceRegistrar implements Registrar<OrdnanceSchematic> {
	@Override
	public Class<OrdnanceSchematic> getObjectType() {
		return OrdnanceSchematic.class;
	}

	@Override
	public Registry<OrdnanceSchematic> getRegistry() {
		return ArmisticeRegistries.ORDNANCE;
	}
}
