package symbolics.division.armistice.mecha;

public class NullOrdnancePart extends OrdnancePart {
	public NullOrdnancePart() {
		super(null);
	}

	@Override
	public boolean ready() {
		return true;
	}
}
