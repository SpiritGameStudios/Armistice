package symbolics.division.armistice.util.registrar;

import net.minecraft.core.Registry;
import symbolics.division.armistice.mecha.schematic.HullSchematic;
import symbolics.division.armistice.registry.ArmisticeRegistries;

public class HullRegistrar implements Registrar<HullSchematic> {
	@Override
	public Class<HullSchematic> getObjectType() {
		return HullSchematic.class;
	}

	@Override
	public Registry<HullSchematic> getRegistry() {
		return ArmisticeRegistries.HULL;
	}
}
