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

	public LegMap(MechaModelData data, ChassisPart chassis) {
		this.chassis = chassis;
		this.legTipOffsets = data.legInfo().stream().map(MechaModelData.LegInfo::tip).toList();
		this.centroidOffset = centroid(this.legTipOffsets).scale(-1);
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
		return model2world(legTipOffsets.get(leg).yRot(mapYaw));
	}

	public Vec3 targetCentroid() {
		return centroid(chassis.effectors()).add(model2world(centroidOffset));
	}

	public void setStepTolerance(double v) {
		this.stepTolerance = v;
	}

	public double stepTolerance() {
		return stepTolerance;
	}

	private Vec3 model2world(Vec3 modelSpacePosition) {
		return new Vec3(
			modelSpacePosition
				.toVector3f()
				.rotateY((float) GeometryUtil.yaw(chassis.direction()))
				.add(chassis.absPos())
		);
	}

	private static Vec3 centroid(List<Vec3> positions) {
		return positions.stream().reduce(Vec3.ZERO, Vec3::add).scale(1d / positions.size());
	}
}
