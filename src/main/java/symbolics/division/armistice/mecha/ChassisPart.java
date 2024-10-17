package symbolics.division.armistice.mecha;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import symbolics.division.armistice.mecha.movement.DirectionState;
import symbolics.division.armistice.mecha.movement.GeometryUtil;
import symbolics.division.armistice.mecha.movement.Leggy;
import symbolics.division.armistice.mecha.schematic.ChassisSchematic;

import java.util.ArrayList;
import java.util.List;

/**
 * The chassis controls the pathing and manages legs. It is the root of the model,
 * so all other parts have position and rotation defined with this at the base.
 * It also has a separate health pool and hitbox that controls whether it is immobilized.
 */
public class ChassisPart extends AbstractMechaPart {
	public final List<Vec3> debugStepTargets = new ArrayList<>();
	protected final DirectionState direction = new DirectionState(Math.PI);
	protected final List<Leggy> legs = new ArrayList<>();
	protected final ChassisSchematic schematic;
	protected final double followTolerance = 4; // temp: related to model diameter
	protected final double moveSpeed = 1.0;
	protected final int numLegs = 6;
	protected final double stepTolerance = 2;
	protected Vec3 movement = Vec3.ZERO;
	protected Vec3 pathingTarget = Vec3.ZERO;

	// todo notes: chassis schematic/skeleton needs to tell us
	// - number of legs
	// - number of segments for each individual leg
	// - leg root pos relative to chassis
	// - (AI) follow distance
	// - move speed
	// - step tolerance (?) -/- also at-rest tolerance

	public ChassisPart(ChassisSchematic schematic) {
		this.schematic = schematic;
		for (int i = 0; i < numLegs; i++) {
			// temp: Define leg segments and orientation from bone data
			legs.add(new Leggy(7));
			legs.get(i).setRootDir(new Vec3(0, 1, 0));
			debugStepTargets.add(Vec3.ZERO);
		}
	}

	@Override
	public void init(MechaCore core) {
		super.init(core);
		core.hull.init(core);

		for (Leggy l : legs) {
			// temp: leg pos from skeleton etc etc
			l.setRootPosAll(core.position());
		}
	}

	@Override
	public void tick() {
		super.tick();
		// temp: need non horizontal-only movement
		if (pathingTarget != null) {
			Vec3 horz = pathingTarget.add(0, -pathingTarget.y, 0);
			if (horz.distanceTo(core.position()) > followTolerance) {
				this.direction.setTarget(horz.subtract(core.position()));
				if (!stepping()) {
					this.movement = horz.subtract(core.position().with(Direction.Axis.Y, 0)).normalize().scale(moveSpeed);
				}
			}
		}

		if (!stepping()) {
			this.direction.tick();
		}

		for (int i = 0; i < legs.size(); i++) {
			// select locations around this for leg tips
			Vec3 stepTarget = relStepPos(core, i);
			debugStepTargets.set(i, stepTarget);
			Leggy leg = legs.get(i);
			// temp: move this logic inside leg controller, only update target pos at each tick
			if (!GeometryUtil.inRange(leg.getStepTarget(), stepTarget, stepTolerance)) {
				int l = i - 1 < 0 ? legs.size() - 1 : i - 1;
				int r = i + 1 >= legs.size() ? 0 : i + 1;
				boolean lneighborStepping = legs.get(l).stepping();
				boolean rneighborStepping = legs.get(r).stepping();
				if (!(lneighborStepping || rneighborStepping)) {
					leg.setStepTarget(stepTarget);
				}
			}
			// temp: set based on skeleton
			leg.setRootPos(core.position().add(0, 1, 0));
			leg.tick();
		}
	}

	@Override
	public void serverTick() {
		tick();
		core.hull.serverTick();
	}

	@Override
	public void clientTick(float tickDelta) {
		tick();
		core.hull.clientTick(tickDelta);
	}

	@Override
	public Part parent() {
		return core;
	}

	@Override
	public Quaternionfc relRot() {
		return new Quaternionf().rotateTo(new Vector3f(0, 0, 1), this.direction().toVector3f());
	}

	public Vector3fc relHullPos() {
		// temp: change to skeleton based
		return new Vector3f(0, 1, 0);
	}

	public Vec3 getPathingTarget() {
		return this.pathingTarget;
	}

	/**
	 * @param target the location in the level to path to.
	 */
	public void setPathingTarget(Vec3 target) {
		this.pathingTarget = target;
	}

	protected boolean stepping() {
		boolean s = false;
		for (Leggy l : legs) s |= l.stepping();
		return s;
	}

	/**
	 * @return Desired acceleration absent external forces
	 */
	public Vec3 movement() {
		return movement;
	}

	// temp: see where we can replace this with relRot etc
	public Vec3 direction() {
		return direction.curDir();
	}

	/**
	 * Assuming bilateral symmetry with two rows of legs
	 *
	 * @param core
	 * @param leg
	 * @return Expected central target position for leg tip relative to chassis center and direction
	 */
	protected Vec3 relStepPos(MechaCore core, int leg) {
		// temp: may need to define each by max leg distance.
		// 		 definitely need to define by skeleton offset and rotation
		// left legs are even, right are odd
		float side = -(leg % 2 * 2 - 1);// +1 for even (left), -1 for odd (right)
		float rotation = side * ((((float) leg / numLegs) + 0.1f) * (float) Math.PI);
		var base = this.direction().with(Direction.Axis.Y, 0).yRot(rotation).normalize();
		return core.position().add(base.scale(legs.get(leg).getMaxLength() / 2).add(0, -1, 0));
	}

	public List<Leggy> debugGetLegs() {
		return legs;
	}
}
