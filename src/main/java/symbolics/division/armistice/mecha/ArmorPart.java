package symbolics.division.armistice.mecha;

public class ArmorPart extends AbstractMechaPart {
	protected final int level;
	protected final int platingAmount;
	protected MechaCore core = null;

	ArmorPart(int level, int platingAmount) {
		this.level = level;
		this.platingAmount = platingAmount;
	}

	@Override
	public Part parent() {
		return core.chassis;
	}
}
