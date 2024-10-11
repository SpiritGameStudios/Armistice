package symbolics.division.armistice.mecha;

import symbolics.division.armistice.mecha.schematic.MechaSchematic;

/**
 * state holder and entity controller
 *
 * parts are not visible to external objects, must provide all interfaces
 * to the rest of the system
 *
 * updates model to reflect state.
 */
public class MechaCore {
	// codec contains schematic + state data for this specific instance
	// decoding reconstructs itself from the schem THEN applies state
	protected final MechaSchematic schematic;

	public MechaCore(MechaSchematic schematic) {
		this.schematic = schematic;
	}
}
