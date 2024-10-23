package symbolics.division.armistice.model;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import symbolics.division.armistice.mecha.schematic.MechaSchematic;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class MechaModelData {
	public final int numLegs;
	private final OutlinerNode hull;
	private final OutlinerNode chassis;
	private final List<Bone> ordnanceInfo = new ArrayList<>();
	private final List<Bone> legInfo = new ArrayList<>();

	private final Vector3fc relativeHullPosition;
	@Nullable
	private final Vec3 seatOffset;

	public MechaModelData(MechaSchematic schematic) {
		hull = Objects.requireNonNull(ModelOutlinerReloadListener.getNode(schematic.hull().id().withPrefix("hull/")))
			.stream().filter(n -> n.name().equals("root")).findAny().orElseThrow();
		chassis = Objects.requireNonNull(ModelOutlinerReloadListener.getNode(schematic.chassis().id().withPrefix("chassis/")))
			.stream().filter(n -> n.name().equals("root")).findAny().orElseThrow();

		for (int i = 1; i <= schematic.ordnance().size(); i++) {
			ordnanceInfo.add(Bone.of(getChild(hull, "ordnance" + i).orElseThrow()));
		}

		numLegs = (int) chassis.children().stream()
			.filter(c -> c.left().map(n -> n.name().startsWith("leg")).orElse(false))
			.count();
		for (int i = 1; i <= numLegs; i++) {
			legInfo.add(Bone.of(getChild(chassis, "leg" + i).orElseThrow()));
		}

		// temp: also include scale per-part
		// bbmodels are by default 16x actual coordinates, so all distances emitted by this
		// class need to be divided by 16.
    relativeHullPosition = getChild(chassis, "hull").orElseThrow().origin().toVector3f().mul(BBModelData.BASE_SCALE_FACTOR);

		seatOffset = getChild(hull, "seat").map(seat -> seat.origin().scale(BBModelData.BASE_SCALE_FACTOR)).orElse(null);
  }

	public Vector3fc relativeHullPosition() {
		// change to Bone
		return relativeHullPosition;
	}

	public Bone ordnance(int i) {
		return ordnanceInfo.get(i);
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

	private static Quaternionfc bbRot2Quaternion(Vec3 xyz) {
		// bb model rotations are in zyx order, and performed in sequence from the root.
		Vec3 rad = deg2rad(xyz);
		return new Quaternionf().rotateZYX((float) rad.z, (float) rad.y, (float) rad.x);
	}

	private static Vec3 deg2rad(Vec3 deg) {
		return new Vec3(deg.x * Mth.DEG_TO_RAD, deg.y * Mth.DEG_TO_RAD, deg.z * Mth.DEG_TO_RAD);
	}

	private static Optional<OutlinerNode> getChild(OutlinerNode node, String id) {
		return node.children().stream()
			.filter(c -> c.left().map(n -> n.name().equals(id)).orElse(false))
			.map(c -> c.left())
			.findFirst().orElse(Optional.empty());
	}

	public Vector3fc relativeHullPosition() {
		return relativeHullPosition;
	}

	@Nullable
	public Vec3 seatOffset() {
		return seatOffset;
	}

	// position, xyz rotation, and 3d direction unit vector
	public record Bone(Vec3 pos, Vec3 rot, Vec3 dir, Quaternionfc quat) {
		// only works for top-level nodes, ie those directly under the root.
		public static Bone of(OutlinerNode node) {
			return new Bone(node.origin().scale(BBModelData.BASE_SCALE_FACTOR), node.rotation(), bbRot2Direction(node.rotation()), bbRot2Quaternion(node.rotation()));
		}
	}
}
