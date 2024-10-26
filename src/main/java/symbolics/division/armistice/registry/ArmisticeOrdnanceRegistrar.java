package symbolics.division.armistice.registry;

import symbolics.division.armistice.mecha.ordnance.SimpleGunOrdnance;
import symbolics.division.armistice.mecha.schematic.OrdnanceSchematic;
import symbolics.division.armistice.util.registrar.OrdnanceRegistrar;

@SuppressWarnings("unused")
public final class ArmisticeOrdnanceRegistrar implements OrdnanceRegistrar {
	public static final OrdnanceSchematic CROSSBOW = new OrdnanceSchematic(
		1,
		() -> new SimpleGunOrdnance(
			1,
			9999999,
			1.5
		)
	);
}
