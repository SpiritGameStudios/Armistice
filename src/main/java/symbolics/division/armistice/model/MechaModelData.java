package symbolics.division.armistice.model;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import symbolics.division.armistice.mecha.schematic.MechaSchematic;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MechaModelData {
	public final int numLegs;
	public final OutlinerNode hull;
	public final OutlinerNode chassis;
	private final List<Bone> ordnanceInfo = new ArrayList<>();
	private final List<Bone> legInfo = new ArrayList<>();

	public MechaModelData(MechaSchematic schematic) {
		var loc = schematic.hull().id().withPrefix("hull/");
		var h = ModelOutlinerReloadListener.getNode(loc);
		hull = Objects.requireNonNull(h).stream().filter(n -> n.name().equals("root")).findAny().orElseThrow();
		chassis = Objects.requireNonNull(ModelOutlinerReloadListener.getNode(schematic.chassis().id().withPrefix("chassis/")))
			.stream().filter(n -> n.name().equals("root")).findAny().orElseThrow();
//		armor = BBModelTree.loadArmorModel(schematic.armor().id());

		for (int i = 1; i < schematic.ordnance().size(); i++) {
			ordnanceInfo.add(Bone.of(getChild(hull, "ordnance" + i)));
		}

		numLegs = (int) chassis.children().stream()
			.filter(c -> c.left().map(n -> n.name().startsWith("leg")).orElse(false))
			.count();
		for (int i = 1; i <= numLegs; i++) {
			legInfo.add(Bone.of(getChild(chassis, "leg" + i)));
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

	// position, xyz rotation, and 3d direction unit vector
	public record Bone(Vec3 pos, Vec3 rot, Vec3 dir) {
		// only works for top-level nodes, ie those directly under the root.
		public static Bone of(OutlinerNode node) {
			return new Bone(node.origin(), node.rotation(), bbRot2Direction(node.rotation()));
		}
	}

	private static OutlinerNode getChild(OutlinerNode node, String id) {
		return node.children().stream()
			.filter(c -> c.left().map(n -> n.name().equals(id)).orElse(false))
			.map(c -> c.left().get())
			.findAny().orElseThrow();
	}
}
