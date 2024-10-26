package symbolics.division.armistice.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
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
	private final List<LegInfo> legInfo = new ArrayList<>();

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
			.filter(c -> c.left().map(n -> n.name().matches("^leg[0-9]$")).orElse(false))
			.count();
		for (int i = 1; i <= numLegs; i++) {
			legInfo.add(LegInfo.of(chassis, "leg" + i));
		}

		// temp: also include scale per-part
		// bbmodels are by default 16x actual coordinates, so all distances emitted by this
		// class need to be divided by 16.
		relativeHullPosition = getChild(chassis, "hull").orElseThrow().origin().toVector3f().mul(BBModelData.BASE_SCALE_FACTOR);

		seatOffset = getChild(hull, "seat").map(seat -> seat.origin().scale(BBModelData.BASE_SCALE_FACTOR)).orElse(null);
	}

	public Bone ordnance(int i) {
		return ordnanceInfo.get(i);
	}

	@Nullable
	public Vec3 seatOffset() {
		return seatOffset;
	}

	public List<LegInfo> legInfo() {
		return ImmutableList.copyOf(legInfo);
	}

	public Vector3fc relativeHullPosition() {
		// change to Bone
		return relativeHullPosition;
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

	private static Optional<OutlinerNode> getChild(OutlinerNode node, int index) {
		for (var child : node.children()) {
			if (child.left().isPresent()) {
				if (index > 0) {
					index--;
				} else {
					return child.left();
				}
			}
		}
		return Optional.empty();
	}

	// position, xyz rotation, and 3d direction unit vector
	public record Bone(Vec3 pos, Vec3 rot, Vec3 dir, Quaternionfc quat) {
		// only works for top-level nodes, ie those directly under the root.
		public static Bone of(OutlinerNode node) {
			return new Bone(node.origin().scale(BBModelData.BASE_SCALE_FACTOR), node.rotation(), bbRot2Direction(node.rotation()), bbRot2Quaternion(node.rotation()));
		}
	}

	public record LegInfo(Vec3 rootOffset, Vec3 tip, double rootYawDegrees, double minYawDegrees, double maxYawDegrees,
						  List<SegmentInfo> segments) {
		// we don't consider joint offset in the IK calc right now;
		// we'll have to assume that we can fudge it and the angles will
		// look right when they're applied to the model after solving.
		public static LegInfo of(OutlinerNode root, String id) {
			OutlinerNode rootSegment = getChild(root, id).orElseThrow();
			List<SegmentInfo> segments = new ArrayList<>();
			Vec3 rootOrigin = rootSegment.origin().scale(BBModelData.BASE_SCALE_FACTOR);
			Vec3 prevPos = rootOrigin;
			Optional<OutlinerNode> segmentOptional = getChild(root, 0);
			while (segmentOptional.isPresent()) {
				OutlinerNode segment = segmentOptional.get();
				Vec3 segmentOrigin = segment.origin().scale(BBModelData.BASE_SCALE_FACTOR);
				segments.add(new SegmentInfo(
					Math.max(prevPos.distanceTo(segmentOrigin), 0.001), // treat 0-length bones as really really small bones instead
					segment.rotation().x,
					segment.parameters().getOrDefault("maxAngle", 90d),
					segment.parameters().getOrDefault("minAngle", 90d)
				));
				prevPos = segmentOrigin;
				segmentOptional = getChild(segment, 0);
			}
			double maxYaw = rootSegment.parameters().getOrDefault("minAngle", 90d);
			double minYaw = rootSegment.parameters().getOrDefault("maxAngle", 90d);
			return new LegInfo(rootOrigin, getChild(root, id + "_tip").orElseThrow().origin().scale(BBModelData.BASE_SCALE_FACTOR), root.rotation().y, minYaw, maxYaw, segments);
		}
	}

	public record SegmentInfo(double length, double baseAngleDeg, double maxAngleDeg, double minAngleDeg) {
	}
}
