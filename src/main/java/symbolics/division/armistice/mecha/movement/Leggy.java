package symbolics.division.armistice.mecha.movement;

import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class Leggy {
	protected final List<JointNode> joints = new ArrayList<>();
	protected Vec3 targetPos = Vec3.ZERO;

	// max rotation is relative to the line formed between this and its parent.
	// for now, the line of the root node is considered to be vertical.
	public final Vec2 maxRotation = new Vec2(25, 25);

	public Leggy(int segments) {
		if (segments < 1) throw new RuntimeException("leg must have at least one segment");
		JointNode j = new JointNode();
		joints.add(j);
		for (int i = 0; i < segments; i++) {
			j = new JointNode(j);
			j.setOffset(1.0);
			joints.add(j);
		}
		targetPos = j.getPos();
	}

	public Vec3 getRootPos() {
		return joints.get(0).getPos();
	}

	public void setRootPos(Vec3 pos) {
		joints.get(0).setPos(pos);
	}

	public Vec3 getTarget() {
		return this.targetPos;
	}

	public void setTarget(Vec3 target) {
		this.targetPos = target;
	}

	public double getMaxDistance() {
		double d = 0;
		for (JointNode joint : joints) {
			d += joint.getOffset();
		}
		return d;
	}

	public void tick() {
		KinematicsSolver.solve(targetPos, joints.getLast());
	}

	public List<Vec3> jointPositions() {
		return joints.stream().map(JointNode::getPos).toList();
	}
}
