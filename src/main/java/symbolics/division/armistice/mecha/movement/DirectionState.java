package symbolics.division.armistice.mecha.movement;

import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class DirectionState {
	private Vector3f currentDirection = new Vector3f(0, 0, 1);
	private Vector3f lastDirection = new Vector3f(0, 0, 1);
	private Vector3f targetDirection = new Vector3f(0, 0, 1);
	private Vector3f axis = new Vector3f(0, 1, 0);
	private float radiansPerTick = (float) Math.PI / 32;
	private double tolerance = 0;

	public DirectionState(double radiansPerSecond) {
		radiansPerTick = (float) (radiansPerSecond * 0.05);
		// tolerance is distance between something radiansPerTick away from something else
		tolerance = GeometryUtil.chord(radiansPerTick);
	}

	public void setAbsolute(Vec3 dir) {
		dir = dir.lengthSqr() != 1 ? dir.normalize() : dir;
		currentDirection.set(dir.x, dir.y, dir.z);
		targetDirection.set(dir.x, dir.y, dir.z);
	}

	public void setTarget(Vec3 dir) {
		dir = dir.lengthSqr() != 1 ? dir.normalize() : dir;
		targetDirection.set(dir.x, dir.y, dir.z);
		currentDirection.cross(targetDirection, axis).normalize();
		if (Float.isNaN(axis.y)) {
			axis = new Vector3f(0, 1, 0);
		}
	}

	public void tick() {
		lastDirection.set(currentDirection);
		if (currentDirection.distanceSquared(targetDirection) <= tolerance * tolerance) {
			currentDirection.set(targetDirection);
		} else {
			currentDirection.rotateAxis(radiansPerTick, axis.x, axis.y, axis.z, currentDirection);
		}
	}

	public Vec3 curDir() {
		return new Vec3(currentDirection);
	}

	/**
	 * @return direction unit vector from previous tick
	 */
	public Vec3 prevDir() {
		return new Vec3(lastDirection);
	}

	public Vec3 targetDir() {
		return new Vec3(targetDirection);
	}
}
