package symbolics.division.armistice.mecha.ordnance;

import net.minecraft.world.phys.HitResult;
import symbolics.division.armistice.mecha.OrdnancePart;

public class NullOrdnancePart extends OrdnancePart {
	public NullOrdnancePart() {
		super(0);
	}

	@Override
	public boolean ready() {
		return true;
	}

	@Override
	public boolean isValidTarget(HitResult hitResult) {
		return false;
	}
}
