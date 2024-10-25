package symbolics.division.armistice.mecha.movement;

import net.minecraft.world.phys.Vec3;
import symbolics.division.armistice.mecha.ChassisPart;
import symbolics.division.armistice.model.MechaModelData;

import java.util.ArrayList;
import java.util.List;

/**
 * Tracks leg position relative to
 */
public class LegMap {
	protected ChassisPart chassis;
	protected List<Vec3> legTipOffsets;
	protected Vec3 mapOffset = Vec3.ZERO;
	protected double stepTolerance = 1;
	public LegMap(MechaModelData data, ChassisPart chassis) {
		this.chassis = chassis;
		legTipOffsets = new ArrayList<>();
		legTipOffsets = data.legInfo().stream().map(MechaModelData.LegInfo::tip).toList();
	}

	public void setMapOffset(Vec3 offset) {
		// offset relative to our forward direction
		this.mapOffset = offset;
	}
}
