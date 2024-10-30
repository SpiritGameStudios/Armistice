package symbolics.division.armistice.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import symbolics.division.armistice.mecha.schematic.MechaSchematic;
import symbolics.division.armistice.mecha.schematic.OrdnanceSchematic;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class MechaModelData {
	public final int numLegs;
	private final OutlinerNode hull;
	private final OutlinerNode chassis;
	private final List<OutlinerNode> ordnanceNodes = new ArrayList<>();

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
			ordnanceInfo.add(Bone.of(hull.getChild("ordnance" + i).orElseThrow()));

			OrdnanceSchematic ordnanceSchematic = schematic.ordnance().get(i - 1);
			ordnanceNodes.add(Objects.requireNonNull(ModelOutlinerReloadListener.getNode(ordnanceSchematic.id().withPrefix("ordnance/")))
				.stream().filter(n -> n.name().equals("root")).findAny().orElseThrow());
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
		relativeHullPosition = chassis.getChild("hull").orElseThrow().origin().toVector3f();

		seatOffset = hull.getChild("seat").map(OutlinerNode::origin).orElse(null);
	}

	public Bone getMarker(OutlinerNode node, int i) {
		return node.getChild("marker" + i)
			.map(Bone::of)
			.orElse(Bone.ZERO);
	}

	public Bone ordnancePoint(int i) {
		return ordnanceInfo.get(i);
	}

	public OutlinerNode ordnance(int i) {
		return ordnanceNodes.get(i);
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
			OutlinerNode rootLegSegment = root.getChild(id).orElseThrow();
			List<SegmentInfo> segments = new ArrayList<>();
			Matrix4f transform = new Matrix4f();

			// first segment rotation is yaw
			Optional<OutlinerNode> segmentOptional = getChild(rootLegSegment, 0);
			OutlinerNode segment = segmentOptional.orElseThrow(() -> new RuntimeException("Legs must have at least one segment"));
			Vector3f basePos = updateTransform(transform, rootLegSegment.origin().toVector3f(), root.rotation().toVector3f());
			Vector3f tipPos = updateTransform(
				transform,
				segment.origin().toVector3f(),
				segment.rotation().toVector3f()
			);
			segments.add(
				new SegmentInfo(
					Math.max(0.01, basePos.distance(tipPos)),
					rootLegSegment.rotation().y,
					rootLegSegment.parameters().getOrDefault("minAngle", 50d),
					rootLegSegment.parameters().getOrDefault("maxAngle", 50d)
				));

			Optional<OutlinerNode> nextNode = getChild(segment, 0);
			while (nextNode.isPresent()) {
				basePos = tipPos;
				tipPos = updateTransform(
					transform,
					nextNode.get().origin().toVector3f(),
					nextNode.get().rotation().toVector3f()
				);
				segments.add(
					new SegmentInfo(
						Math.max(0.01, basePos.distance(tipPos)),
						segment.rotation().x,
						segment.parameters().getOrDefault("minAngle", 60d),
						segment.parameters().getOrDefault("maxAngle", 60d)
					)
				);
				segment = nextNode.get();
				nextNode = getChild(segment, 0);
			}

			Vec3 legEnd = root.getChild(id + "_tip").orElseThrow().origin();
			segments.add(new SegmentInfo(
				Math.max(0.01f, tipPos.distance(legEnd.toVector3f()) * 0.8),
				segment.rotation().x,
				segment.parameters().getOrDefault("minAngle", 45d),
				segment.parameters().getOrDefault("maxAngle", 45d)
			));
			return new LegInfo(rootLegSegment.origin(), legEnd, segments);
		}
	}

	public record SegmentInfo(double length, double baseAngleDeg, double minAngleDeg, double maxAngleDeg) {
	}
}
