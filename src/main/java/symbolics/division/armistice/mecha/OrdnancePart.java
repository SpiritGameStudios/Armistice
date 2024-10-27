package symbolics.division.armistice.mecha;

import com.google.common.collect.ImmutableList;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.joml.Quaternionf;
import org.joml.Vector3fc;

import java.util.ArrayList;
import java.util.List;

public abstract class OrdnancePart extends AbstractMechaPart {
	protected final int maxTargets;
	private final List<HitResult> targets = new ArrayList<>();
	protected MechaCore core;

	protected OrdnancePart(int maxTargets) {
		this.maxTargets = maxTargets;
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
		return new Quaternionf(core.model().ordnancePoint(core.ordnanceIndex(this)).quat());
	}

	@Override
	public Vector3fc relPos() {
		return core.model().ordnancePoint(core.ordnanceIndex(this)).pos().toVector3f();
	}

	protected abstract boolean isValidTarget(HitResult hitResult);

	protected List<HitResult> targets() {
		targets.removeIf(target -> target instanceof EntityHitResult result && (result.getEntity().isRemoved() || !result.getEntity().isAlive()));

		return ImmutableList.copyOf(targets);
	}

	public boolean startTargeting(HitResult hitResult) {
		if (!isValidTarget(hitResult) || targets.size() >= maxTargets) return false;
		targets.add(hitResult);
		return true;
	}
}
