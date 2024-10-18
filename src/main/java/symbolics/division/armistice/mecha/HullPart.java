package symbolics.division.armistice.mecha;

import net.minecraft.core.NonNullList;
import org.joml.Vector3fc;
import symbolics.division.armistice.mecha.schematic.HeatData;
import symbolics.division.armistice.mecha.schematic.HullSchematic;
import symbolics.division.armistice.mecha.schematic.OrdnanceSchematic;

public class HullPart extends AbstractMechaPart {
	protected final NonNullList<OrdnancePart> ordnance;
	protected final HeatData heatData;
	protected MechaCore core = null;

	protected int heat;
	protected int coolingDelay;

	public HullPart(HullSchematic schematic) {
		ordnance = NonNullList.withSize(schematic.slots().size() - 1, new NullOrdnancePart());
		this.heatData = schematic.heat();
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
	public void tick() {
		super.tick();

		int prevHeat = heat;
		ordnance.forEach(part -> heat += part.heat());

		if (heat > prevHeat) coolingDelay = heatData.delay();
		else coolingDelay = Math.max(coolingDelay - 1, 0);

		if (coolingDelay == 0)
			heat = Math.max(heat - heatData.decay(), 0);

		if (heat > heatData.max()) onOverheat();
	}

	@Override
	public void clientTick(float tickDelta) {
		ordnance.forEach(part -> part.clientTick(tickDelta));

		super.clientTick(tickDelta);
	}

	@Override
	public void serverTick() {
		ordnance.forEach(Part::serverTick);

		super.serverTick();
	}

	protected OrdnancePart getOrdnance(int slot) {
		return ordnance.get(slot);
	}

	protected void onOverheat() {

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
