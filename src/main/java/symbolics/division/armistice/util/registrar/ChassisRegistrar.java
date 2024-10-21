package symbolics.division.armistice.util.registrar;

import net.minecraft.core.Registry;
import symbolics.division.armistice.mecha.schematic.ChassisSchematic;
import symbolics.division.armistice.registry.ArmisticeRegistries;

public class ChassisRegistrar implements Registrar<ChassisSchematic> {
	@Override
	public Class<ChassisSchematic> getObjectType() {
		return ChassisSchematic.class;
	}

	@Override
	public Registry<ChassisSchematic> getRegistry() {
		return ArmisticeRegistries.CHASSIS;
	}
}
