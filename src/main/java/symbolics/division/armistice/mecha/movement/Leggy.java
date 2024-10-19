package symbolics.division.armistice.mecha.movement;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class Leggy {
	protected final List<IKSegment> segments = new ArrayList<>();
	protected Vec3 targetPos = Vec3.ZERO;
	protected LegController controller;

	public Vec3 rot_normal = Vec3.ZERO;

	public Leggy(int nSegments) {
		if (nSegments < 1) throw new RuntimeException("leg must have at least one segment");
		for (int i = 0; i < nSegments; i++) {
			segments.add(IKSegment.of(1));
		}
		this.controller = new LegController(this);
	}

	public Vec3 getRootPos() {
		return segments.getFirst().position();
	}

	public void setRootPosAll(Vec3 pos) {
		segments.getFirst().setPosition(pos);
		for (int i = 1; i < segments.size(); i++) {
			var parent = segments.get(i - 1);
			segments.get(i).setPosition(parent.position().add(parent.direction().scale(parent.length())));
		}
	}

	public void setRootPos(Vec3 pos) {
		segments.getFirst().setPosition(pos);
	}

	public void setRootDir(Vec3 dir) {
		segments.getFirst().setDirection(dir);
	}

	public Vec3 getTarget() {
		return this.targetPos;
	}

	public void setTarget(Vec3 target) {
		controller.clearTarget();
		this.targetPos = target;
	}

	public void setStepTarget(Vec3 target) {
		controller.setTarget(target);
	}

	public Vec3 getStepTarget() {
		return controller.getTarget();
	}

	public boolean stepping() {
		return controller.stepping();
	}

	public double getMaxLength() {
		double d = 0;
		for (var s : segments) {
			d += s.length();
		}
		return d;
	}

	public Vec3 getTipPos() {
		return segments.getLast().endPosition();
	}

	public void tick() {
		controller.tick();
		// point base towards target
		segments.getFirst().setDirection(targetPos.subtract(segments.getFirst().position()).with(Direction.Axis.Y, 0).normalize().add(0, 0.5, 0).normalize());
		KinematicsSolver.solve(targetPos, segments, getMaxLength(), this);
	}

	public List<Vec3> jointPositions() {
		return jointsOf(segments);
	}

	public static List<Vec3> jointsOf(List<IKSegment> segments) {
		List<Vec3> out = new ArrayList<>(segments.size() + 1);
		for (var s : segments) out.add(s.position());
		out.add(segments.getLast().endPosition());
		return out;
	}

	private static class LegController {
		private final float ticksToStep;
		private final Leggy leg;
		private float ticksLeft = 0;
		private Vec3 startPos = Vec3.ZERO;
		private Vec3 endPos = Vec3.ZERO;
		private float stepHeight = 1.0f;

		public LegController(Leggy l, float stepHeight) {
			this(l);
			this.stepHeight = stepHeight;
		}

		public LegController(Leggy l) {
			ticksToStep = 10;
			this.leg = l;
			this.startPos = l.getTarget();
		}

		public void setTarget(Vec3 newTarget) {
			startPos = leg.getTipPos();
			endPos = newTarget;
			ticksLeft = ticksToStep;
		}

		public Vec3 getTarget() {
			return endPos;
		}

		public void clearTarget() {
			ticksLeft = 0;
		}

		public void tick() {
			if (ticksLeft <= 0) return;
			ticksLeft -= 1;
			var dist = (ticksToStep - ticksLeft) / ticksToStep;
			var t = startPos.add(endPos.subtract(startPos).scale(dist));
			if (ticksLeft > 0) t = t.add(0, GeometryUtil.easedCurve(dist) * stepHeight, 0);
			leg.targetPos = t;
			//leg.targetPos = KinematicsSolver.adjustRelative(startPos, endPos, startPos.distanceTo(endPos) * dist);
		}

		public boolean stepping() {
			return ticksLeft > 0;
		}

	}
}
