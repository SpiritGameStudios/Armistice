package symbolics.division.armistice.util.registrar;

import net.minecraft.core.Registry;
import symbolics.division.armistice.mecha.schematic.HeatData;
import symbolics.division.armistice.mecha.schematic.HullSchematic;
import symbolics.division.armistice.registry.ArmisticeRegistries;

import java.util.List;

public class HullRegistrar implements Registrar<HullSchematic> {

	public static final HullSchematic DEPTH_HULL = new HullSchematic(
		1, List.of(1, 2), new HeatData(100, 20, 5)
	);

	public static final HullSchematic TECH_HULL = new HullSchematic(
		1, List.of(2, 1, 1), new HeatData(100, 20, 5)
	);

	@Override
	public Class<HullSchematic> getObjectType() {
		return HullSchematic.class;
	}

	@Override
	public Registry<HullSchematic> getRegistry() {
		return ArmisticeRegistries.HULL;
	}
}
