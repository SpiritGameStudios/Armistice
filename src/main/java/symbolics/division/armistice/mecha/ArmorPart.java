package symbolics.division.armistice.mecha;

import symbolics.division.armistice.mecha.schematic.ArmorSchematic;

public class ArmorPart extends AbstractMechaPart {
	protected final int level;
	protected final double platingAmount;
	protected MechaCore core = null;

	public ArmorPart(ArmorSchematic schematic) {
		this.level = schematic.size();
		this.platingAmount = schematic.plating();
	}

	@Override
	public Part parent() {
		return core.chassis;
	}
}
