package symbolics.division.armistice.mecha;

import org.apache.commons.lang3.NotImplementedException;
import symbolics.division.armistice.mecha.schematic.HullSchematic;

public class HullPart implements Part {
	public HullPart(HullSchematic schematic) {
	}

	protected OrdnancePart getOrdnance(int slot) {
		throw new NotImplementedException();
	}
}
