package symbolics.division.armistice.model;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import symbolics.division.armistice.mecha.schematic.MechaSchematic;

import java.util.ArrayList;
import java.util.List;

public class MechaModelData {
	// position, xyz rotation, and 3d direction unit vector
	public record Bone(Vec3 pos, Vec3 rot, Vec3 dir) {
		// only works for top-level nodes, ie those directly under the root.
		public static Bone of(OutlinerNode node) {
			return new Bone(node.origin(), node.rotation(), bbRot2Direction(node.rotation()));
		}
	}

	private static Vec3 bbRot2Direction(Vec3 xyz) {
		// bb model rotations are in zyx order, and performed in sequence from the root.
		Vec3 rad = deg2rad(xyz);
		return new Vec3(new Vector3f(0, 0, 1)
			.rotateZ((float) rad.z)
			.rotateY((float) rad.y)
			.rotateX((float) rad.x)
		);
	}

	private static Vec3 deg2rad(Vec3 deg) {
		return new Vec3(deg.x * Mth.DEG_TO_RAD, deg.y * Mth.DEG_TO_RAD, deg.z * Mth.DEG_TO_RAD);
	}

	public final int numLegs;

	public final BBModelTree hull;
	public final BBModelTree chassis;

	private List<Bone> ordnanceInfo = new ArrayList<>();
	private List<Bone> legInfo = new ArrayList<>();

	public MechaModelData(MechaSchematic schematic) {
		hull = BBModelTree.loadHull(schematic.hull().id());
		chassis = BBModelTree.loadChassis(schematic.chassis().id());
//		armor = BBModelTree.loadArmorModel(schematic.armor().id());

		for (int i = 0; i < schematic.ordnance().size(); i++) {
			var gun = hull.child("ordnance" + i);
			ordnanceInfo.add(Bone.of(gun.node));
		}

		numLegs = (int) chassis.children().stream().filter(c -> c.node.name().startsWith("leg")).count();
		for (int i = 1; i <= numLegs; i++) {
			var leg = chassis.child("leg" + i);
			legInfo.add(Bone.of(leg.node));
		}
	}
}
