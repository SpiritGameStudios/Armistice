package symbolics.division.armistice.mecha;

public class ArmorPart implements Part {
	protected final int level;
	protected final int platingAmount;

	ArmorPart(int level, int platingAmount) {
		this.level = level;
		this.platingAmount = platingAmount;
	}
}
