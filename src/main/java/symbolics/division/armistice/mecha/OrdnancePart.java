package symbolics.division.armistice.mecha;

import com.google.common.collect.ImmutableList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector2fc;
import org.joml.Vector3fc;
import symbolics.division.armistice.mecha.ordnance.NullOrdnancePart;
import symbolics.division.armistice.mecha.ordnance.OrdnanceRotation;
import symbolics.division.armistice.model.MechaModelData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class OrdnancePart extends AbstractMechaPart {
	protected final int maxTargets;
	private final List<HitResult> targets = new ArrayList<>();
	protected MechaCore core;
	private ResourceLocation id = null;

	protected OrdnanceRotation rotationManager;

	protected OrdnancePart(int maxTargets) {
		this.maxTargets = maxTargets;
	}

	@Override
	public void init(MechaCore core) {
		super.init(core);
		this.core = core;
		if (this instanceof NullOrdnancePart) {
			this.rotationManager = new OrdnanceRotation(this, core, 1, 0, 0, 1, 0, 0, 1);
		} else {
			MechaModelData.RotationConstraints constraints = modelInfo().rotationConstraints();
			this.rotationManager = new OrdnanceRotation(
				this,
				core,
				(float) modelInfo().body().origin().length(), // this needs to be the length from the connection point to the pivot
				constraints.minYaw(),
				constraints.maxYaw(),
				180f / 80,
				constraints.minPitch(),
				constraints.maxPitch(),
				180f / 80);
		}
	}

	public int heat() {
		return 0;
	}

	@Override
	public Part parent() {
		return core.hull;
	}

	@Override
	public Quaternionf relRot() {
		return new Quaternionf(modelInfo().mountPoint().rotationInfo().rotation());
	}

	@Override
	public Vector3fc relPos() {
		return modelInfo().mountPoint().origin().toVector3f();
	}

	protected abstract boolean isValidTarget(HitResult hitResult);

	protected List<HitResult> targets() {
		targets.removeIf(target -> target instanceof EntityHitResult result && (result.getEntity().isRemoved() || !result.getEntity().isAlive()));

		return ImmutableList.copyOf(targets);
	}

	public Quaternionf baseRotation() {
		// default barrel rotation
		return new Quaternionf(core.hull.absRot().mul(modelInfo().mountPoint().rotationInfo().rotation()));
	}

	public Vector2fc barrelRotation() {
		return rotationManager.relYawPitchRad();
	}

	public boolean startTargeting(HitResult hitResult) {
		// temp: use blockpos zero as signal to clear targets
		if (hitResult.getLocation().equals(Vec3.ZERO)) {
			targets.clear();
			return false;
		}
		if (!isValidTarget(hitResult)) return false;
		if (targets.size() >= maxTargets) targets.set(0, hitResult);
		else targets.add(hitResult);

		return true;
	}

	public boolean isTargeting() {
		return !targets.isEmpty();
	}

	public void clearTargets() {
		targets.clear();
	}

	public List<HitResult> getTargets() {
		return Collections.unmodifiableList(targets);
	}

	public void setId(ResourceLocation id) {
		if (this.id != null) throw new IllegalStateException();
		this.id = id;
	}

	public Vec3 currentDirection() {
		return rotationManager.currentDirection();
	}

	public MechaModelData.OrdnanceInfo modelInfo() {
		return core.model().ordnanceInfo(this, core);
	}

	@Nullable
	public ResourceLocation id() {
		return this.id;
	}
}
