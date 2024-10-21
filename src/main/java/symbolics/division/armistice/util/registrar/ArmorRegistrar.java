package symbolics.division.armistice.util.registrar;

import net.minecraft.core.Registry;
import symbolics.division.armistice.mecha.schematic.ArmorSchematic;
import symbolics.division.armistice.registry.ArmisticeRegistries;

public class ArmorRegistrar implements Registrar<ArmorSchematic> {
	@Override
	public Class<ArmorSchematic> getObjectType() {
		return ArmorSchematic.class;
	}

	@Override
	public Registry<ArmorSchematic> getRegistry() {
		return ArmisticeRegistries.ARMOR;
	}
}
