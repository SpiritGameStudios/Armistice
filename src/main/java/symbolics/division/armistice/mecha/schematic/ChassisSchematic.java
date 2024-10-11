package symbolics.division.armistice.mecha.schematic;

import symbolics.division.armistice.mecha.ChassisPart;

public class ChassisSchematic extends PartSchematic<ChassisPart, ChassisSchematic> {
	protected final int tier; // 1-3
	protected final int minArmorLevel;
	protected final int maxArmorLevel;
}
