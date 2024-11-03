package symbolics.division.armistice.util.registrar;

import net.minecraft.core.Registry;
import symbolics.division.armistice.mecha.schematic.ChassisSchematic;
import symbolics.division.armistice.registry.ArmisticeRegistries;

public class ChassisRegistrar implements Registrar<ChassisSchematic> {

	public static final ChassisSchematic NIMBLE_CHASSIS = new ChassisSchematic(
		1, 1, 1, 1
	);

	public static final ChassisSchematic STURDY_CHASSIS = new ChassisSchematic(
		1, 1, 1, 1
	);

	public static final ChassisSchematic WALKING_CHASSIS = new ChassisSchematic(
		1, 1, 1, 1
	);

	@Override
	public Class<ChassisSchematic> getObjectType() {
		return ChassisSchematic.class;
	}

	@Override
	public Registry<ChassisSchematic> getRegistry() {
		return ArmisticeRegistries.CHASSIS;
	}
}
