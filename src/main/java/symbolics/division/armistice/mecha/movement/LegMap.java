package symbolics.division.armistice.mecha.movement;

import net.minecraft.world.phys.Vec3;
import symbolics.division.armistice.math.GeometryUtil;
import symbolics.division.armistice.mecha.ChassisPart;
import symbolics.division.armistice.model.MechaModelData;

import java.util.List;

/**
 * Tracks leg position relative to
 */
public class LegMap {
	protected final ChassisPart chassis;
	protected final List<Vec3> legTipOffsets;
	protected final Vec3 centroidOffset;
	protected Vec3 mapOffset = Vec3.ZERO;
	protected float mapYaw = 0;
	protected double stepTolerance = 0.5;
	protected double[] directionWeights;

	public LegMap(MechaModelData data, ChassisPart chassis) {
		this.chassis = chassis;
		this.legTipOffsets = data.legInfo().stream().map(MechaModelData.LegInfo::tip).toList();
		Vec3 baseCentroid = centroid(this.legTipOffsets);
		// if center of mech is origin, -baseCentroid is offset to obtain target centroid.
		this.centroidOffset = baseCentroid.scale(-1);

		// compute direction weights. closer = higher weight.
		double weightSum = 0;
		Vec3 centroidHorizontal = baseCentroid.multiply(1, 0, 1);
		Vec3 directionRef = centroidHorizontal.add(0, 0, 1);
		directionWeights = new double[legTipOffsets.size()];
		for (int i = 0; i < directionWeights.length; i++) {
			var offset = legTipOffsets.get(i).multiply(-1, 0, 1);
			directionWeights[i] =
				offset.distanceTo(centroidHorizontal) / offset.distanceTo(directionRef);
			weightSum += directionWeights[i];
		}
		for (int i = 0; i < directionWeights.length; i++) {
			directionWeights[i] /= weightSum;
		}
	}

	public void setMapOffset(Vec3 offset) {
		// offset relative to our forward direction
		this.mapOffset = offset;
	}

	public void setMapRotation(float yaw) {
		this.mapYaw = yaw;
	}

	public Vec3 legTarget(int leg) {
		// leg target in world-space.
		// special case: we *do* want to offset then rotate unlike usual,
		// based on how we wish for legs to  move
		return model2world(legTipOffsets.get(leg).add(mapOffset).yRot(mapYaw));
	}

	public Vec3 targetDir(List<Vec3> effectors) {
		Vec3 centroid = centroid(effectors);
		Vec3 weightedPosition = Vec3.ZERO;
		for (int i = 0; i < effectors.size(); i++) {
			weightedPosition = weightedPosition.add(effectors.get(i).multiply(directionWeights[i], 0, directionWeights[i]));
		}
		return weightedPosition.subtract(centroid.multiply(1, 0, 1)).normalize();
	}

	public Vec3 targetCentroid(Vec3 dir, List<Vec3> effectors) {
		var c = centroid(effectors);
		var m2w = centroidOffset.yRot((float) GeometryUtil.yaw(dir));
		return c.add(m2w);
	}

	public void setStepTolerance(double v) {
		this.stepTolerance = v;
	}

	public double stepTolerance() {
		return stepTolerance;
	}

//	private Vec3 world2model()

	private Vec3 model2world(Vec3 modelSpacePosition) {
		return new Vec3(
			modelSpacePosition
				.toVector3f()
				.rotateY((float) GeometryUtil.yaw(chassis.direction()))
				.add(chassis.absPos())
		);
	}


	public static Vec3 weightedCentroid(List<Vec3> positions, double[] weights) {
		return positions.stream().reduce(Vec3.ZERO, Vec3::add).scale(1d / positions.size());
	}

	public static Vec3 centroid(List<Vec3> positions) {
		return positions.stream().reduce(Vec3.ZERO, Vec3::add).scale(1d / positions.size());
	}

	/*
	weighted centroid gives us a heuristic of "direction".
	first, we compute the weights, then we can
	 */
}
