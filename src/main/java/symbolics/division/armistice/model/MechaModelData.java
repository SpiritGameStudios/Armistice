package symbolics.division.armistice.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.*;
import symbolics.division.armistice.mecha.schematic.MechaSchematic;

import java.lang.Math;
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

	private static Vector3f updateTransform(Matrix4f transform, Vector3fc pivot, Vector3fc rotation) {
		// idk how this works my brain told me to do it
		transform.translate(pivot.x(), pivot.y(), pivot.z());
		transform.rotate(new Quaternionf().rotateZYX(rotation.z() * Mth.DEG_TO_RAD, rotation.y() * Mth.DEG_TO_RAD, rotation.x() * Mth.DEG_TO_RAD));
		transform.translate(-pivot.x(), -pivot.y(), -pivot.z());
		return transform.transformPosition(pivot, new Vector3f());
	}

	public record LegInfo(Vec3 rootOffset, Vec3 tip, List<SegmentInfo> segments) {
		// we don't consider joint vertical offset in the IK calc right now;
		// we'll have to assume that we can fudge it and the angles will
		// look right when they're applied to the model after solving.
		public static LegInfo of(OutlinerNode root, String id) {
			OutlinerNode rootLegSegment = getChild(root, id).orElseThrow();
			List<SegmentInfo> segments = new ArrayList<>();
			Matrix4f transform = new Matrix4f();

			// first segment rotation is yaw
			Optional<OutlinerNode> segmentOptional = getChild(rootLegSegment, 0);
			OutlinerNode segment = segmentOptional.orElseThrow(() -> new RuntimeException("Legs must have at least one segment"));
			Vector3f basePos = updateTransform(transform, rootLegSegment.origin().scale(BBModelData.BASE_SCALE_FACTOR).toVector3f(), root.rotation().toVector3f());
			Vector3f tipPos = updateTransform(
				transform,
				segment.origin().scale(BBModelData.BASE_SCALE_FACTOR).toVector3f(),
				segment.rotation().toVector3f()
			);
			segments.add(
				new SegmentInfo(
					Math.max(0.01, basePos.distance(tipPos)),
					rootLegSegment.rotation().y,
					rootLegSegment.parameters().getOrDefault("minAngle", 15d),
					rootLegSegment.parameters().getOrDefault("maxAngle", 15d)
				));

			Optional<OutlinerNode> nextNode = getChild(segment, 0);
			while (nextNode.isPresent()) {
				basePos = tipPos;
				tipPos = updateTransform(
					transform,
					nextNode.get().origin().scale(BBModelData.BASE_SCALE_FACTOR).toVector3f(),
					nextNode.get().rotation().toVector3f()
				);
				segments.add(
					new SegmentInfo(
						Math.max(0.01, basePos.distance(tipPos)),
						segment.rotation().x,
						segment.parameters().getOrDefault("minAngle", 30d),
						segment.parameters().getOrDefault("maxAngle", 30d)
					)
				);
				segment = nextNode.get();
				nextNode = getChild(segment, 0);
			}

			Vec3 legEnd = getChild(root, id + "_tip").orElseThrow().origin().scale(BBModelData.BASE_SCALE_FACTOR);
			segments.add(new SegmentInfo(
				Math.max(0.01f, tipPos.distance(legEnd.toVector3f())),
				segment.rotation().x,
				segment.parameters().getOrDefault("minAngle", 30d),
				segment.parameters().getOrDefault("maxAngle", 30d)
			));
			return new LegInfo(rootLegSegment.origin().scale(BBModelData.BASE_SCALE_FACTOR), legEnd, segments);
		}
	}

	public record SegmentInfo(double length, double baseAngleDeg, double minAngleDeg, double maxAngleDeg) {
	}
}
