package symbolics.division.armistice.mecha.movement;

import net.minecraft.world.phys.Vec3;

public interface IKSegment {
	float length();

	RotationConstraint constraint();

	Vec3 direction();

	Vec3 position();

	void setDirection(Vec3 direction);

	void setPosition(Vec3 position);

	void setLength(float length);

	Vec3 endPosition();

	static IKSegment of(float length) {
		return new IKSegmentImpl(length);
	}

	final class IKSegmentImpl implements IKSegment {
		private final RotationConstraint constraint;
		private float length;
		private Vec3 direction = new Vec3(0, 0, 1);
		private Vec3 position = Vec3.ZERO;

		private IKSegmentImpl(float length) {
			setLength(length);
			constraint = RotationConstraint.of(0.01f, (float) Math.PI / 4, length);
		}

		@Override
		public float length() {
			return length;
		}

		@Override
		public RotationConstraint constraint() {
			return constraint;
		}

		@Override
		public Vec3 direction() {
			return direction;
		}

		@Override
		public Vec3 position() {
			return position;
		}

		@Override
		public void setDirection(Vec3 direction) {
			this.direction = direction;
		}

		@Override
		public void setPosition(Vec3 position) {
			this.position = position;
		}

		@Override
		public void setLength(float length) {
			this.length = length;
		}

		@Override
		public Vec3 endPosition() {
			return position().add(direction().scale(length()));
		}
	}
}
