package symbolics.division.armistice.mecha;

import symbolics.division.armistice.mecha.schematic.OrdnanceSchematic;

public class OrdnancePart extends AbstractMechaPart {
	public OrdnancePart(OrdnanceSchematic schematic) {
		
	}

	@Override
	public Part parent() {
		return core.hull;
	}
}
