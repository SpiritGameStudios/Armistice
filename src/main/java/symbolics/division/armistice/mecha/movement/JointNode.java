package symbolics.division.armistice.mecha.movement;

import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class JointNode {
	protected final JointNode parent;

	protected int depth;
	protected Vec3 pos = Vec3.ZERO;  // joint pos
	protected double offset; // distance from parent

	// max rotation is relative to the line formed between this and its parent.
	// for now, the line of the root node is considered to be vertical.
	// yaw, pitch


	public JointNode() {
		depth = 1;
		parent = null;
	}

	public JointNode(JointNode parent) {
		depth = parent.depth + 1;
		this.parent = parent;
	}

	public void setPos(Vec3 pos) {
		this.pos = pos;
	}

	public Vec3 getPos() {
		return this.pos;
	}

	public void setOffset(double offset) {
		this.offset = offset;
	}

	public double getOffset() {
		return this.offset;
	}

	public boolean root() {
		return this.parent == null;
	}

	public JointNode getRoot() {
		if (!root()) return parent.getRoot();
		return this;
	}

	public JointNode[] getChain(@Nullable JointNode relativeRoot) {
		if (relativeRoot == null) relativeRoot = getRoot();
		int length = this.depth - relativeRoot.depth + 1;
		if (length < 1) {
			throw new RuntimeException("Attempted to get chain with invalid depth " + length);
		}
		JointNode[] nodes = new JointNode[length];
		JointNode j = this;
		for (int i = length - 1; i >= 0; i--) {
			nodes[i] = j;
			if ((j == null || i == 0) && j != relativeRoot) {
				throw new RuntimeException("Attempted to get chain relative to a root not in node's ancestry");
			}
			j = j.parent;
		}
		return nodes;


//		JointNode j = this;
//		do {
//			length--;
//			nodes[length] = j;
//			j = j.parent;
//		} while (j != relativeRoot && j != null);
//		if (j == null && relativeRoot != null) {
//			throw new RuntimeException("Attempted to get chain relative to a root not in node's ancestry");
//		}
//		return nodes;
	}
}
