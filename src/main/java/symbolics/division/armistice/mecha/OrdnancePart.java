package symbolics.division.armistice.mecha;

import org.joml.Quaternionf;
import org.joml.Vector3fc;
import symbolics.division.armistice.mecha.schematic.OrdnanceSchematic;

public class OrdnancePart extends AbstractMechaPart {
	private MechaCore core;

	public OrdnancePart(OrdnanceSchematic schematic) {
	}

	@Override
	public void init(MechaCore core) {
		super.init(core);
		this.core = core;
	}

	// TODO: Implement ordnance heat
	public int heat() {
		return 0;
	}

	@Override
	public Part parent() {
		return core.hull;
	}

	@Override
	public Quaternionf relRot() {
		return new Quaternionf(core.model().ordnance(core.ordnanceIndex(this)).quat());
	}

	@Override
	public Vector3fc relPos() {
		return core.model().ordnance(core.ordnanceIndex(this)).pos().toVector3f();
	}
}
