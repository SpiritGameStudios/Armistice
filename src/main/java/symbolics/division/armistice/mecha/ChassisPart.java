package symbolics.division.armistice.mecha;

import symbolics.division.armistice.mecha.schematic.ChassisSchematic;

public class ChassisPart implements Part {
	protected ChassisSchematic schematic;

	public ChassisPart(ChassisSchematic schematic) {
		this.schematic = schematic;
	}
}
