package symbolics.division.armistice.mecha;

import com.google.common.collect.ImmutableList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector2fc;
import org.joml.Vector3fc;
import symbolics.division.armistice.mecha.ordnance.OrdnanceRotation;
import symbolics.division.armistice.mecha.schematic.OrdnanceSchematic;

import java.util.ArrayList;
import java.util.List;

public abstract class OrdnancePart extends AbstractMechaPart {
	protected final int maxTargets;
	private final List<HitResult> targets = new ArrayList<>();
	protected MechaCore core;
	private ResourceLocation id = null;
	protected OrdnanceSchematic schematic;

	public Quaternionf lastRenderRotation = new Quaternionf();

	protected OrdnanceRotation rotationManager;

	protected OrdnancePart(int maxTargets) {
		this.maxTargets = maxTargets;
	}

	@Override
	public void init(MechaCore core) {
		super.init(core);
		this.core = core;
		this.rotationManager = new OrdnanceRotation(this,
			1f, // this needs to be the length from the connection point to the pivot
			180, 180, core, 180f / 20, 45f, 90f, 180f / 20);
		this.schematic = schematic;
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
		return new Quaternionf(core.model().ordnanceInfo(this, core).mountPoint().rotationInfo().rotation());
	}

	@Override
	public Vector3fc relPos() {
		return core.model().ordnanceInfo(this, core).mountPoint().origin().toVector3f();
	}

	protected abstract boolean isValidTarget(HitResult hitResult);

	protected List<HitResult> targets() {
		targets.removeIf(target -> target instanceof EntityHitResult result && (result.getEntity().isRemoved() || !result.getEntity().isAlive()));

		return ImmutableList.copyOf(targets);
	}

	public Quaternionf baseRotation() {
		// default barrel rotation
		return new Quaternionf(core.hull.absRot().mul(core.model().ordnanceInfo(this, core).mountPoint().rotationInfo().rotation()));
	}

	public Vector2fc barrelRotation() {
		return rotationManager.relYawPitchRad();
	}

	public boolean startTargeting(HitResult hitResult) {
		if (!isValidTarget(hitResult)) return false;
		if (targets.size() >= maxTargets) targets.set(0, hitResult);
		else targets.add(hitResult);

		return true;
	}

	public void clearTargets() {
		targets.clear();
	}

	public void setId(ResourceLocation id) {
		if (this.id != null) throw new IllegalStateException();
		this.id = id;
	}

	@Nullable
	public ResourceLocation id() {
		return this.id;
	}
}
