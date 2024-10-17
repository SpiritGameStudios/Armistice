package symbolics.division.armistice.mecha;

import net.minecraft.core.NonNullList;
import org.joml.Vector3fc;
import symbolics.division.armistice.mecha.schematic.HullSchematic;
import symbolics.division.armistice.mecha.schematic.OrdnanceSchematic;

public class HullPart extends AbstractMechaPart {
	protected final NonNullList<OrdnancePart> ordnance;
	protected MechaCore core = null;

	public HullPart(HullSchematic schematic) {
		ordnance = NonNullList.withSize(schematic.slots().size() - 1, new NullOrdnancePart());
	}

	@Override
	public void init(MechaCore core) {
		super.init(core);

		core.ordnance().stream()
			.map(OrdnanceSchematic::make)
			.forEach(ordnance::add);

		ordnance.forEach(part -> part.init(core));
	}

	@Override
	public void clientTick(float tickDelta) {
		super.clientTick(tickDelta);
		ordnance.forEach(part -> part.clientTick(tickDelta));
	}

	@Override
	public void serverTick() {
		super.serverTick();
		ordnance.forEach(Part::serverTick);
	}

	protected OrdnancePart getOrdnance(int slot) {
		return ordnance.get(slot - 1);
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
