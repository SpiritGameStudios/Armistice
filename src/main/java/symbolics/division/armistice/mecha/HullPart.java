package symbolics.division.armistice.mecha;

import org.apache.commons.lang3.NotImplementedException;
import org.joml.Vector3fc;
import symbolics.division.armistice.mecha.schematic.HullSchematic;

public class HullPart extends AbstractMechaPart {
	protected MechaCore core = null;

	public HullPart(HullSchematic schematic) {
	}

	@Override
	public void init(MechaCore core) {
		this.core = core;
	}

	@Override
	public void clientTick(float tickDelta) {
		super.clientTick(tickDelta);
		// ordnance.clientTick
	}

	@Override
	public void serverTick() {
		super.serverTick();
		// ordnance.serverTick
	}

	protected OrdnancePart getOrdnance(int slot) {
		throw new NotImplementedException();
	}

	@Override
	public ChassisPart parent() {
		return this.core.chassis;
	}

	@Override
	public Vector3fc relPos() {
		return parent().relHullPos();
	}
}
