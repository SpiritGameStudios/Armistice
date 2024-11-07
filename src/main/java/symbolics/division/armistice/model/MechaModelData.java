package symbolics.division.armistice.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.*;
import symbolics.division.armistice.math.GeometryUtil;
import symbolics.division.armistice.mecha.MechaCore;
import symbolics.division.armistice.mecha.OrdnancePart;
import symbolics.division.armistice.mecha.schematic.MechaSchematic;
import symbolics.division.armistice.mecha.schematic.OrdnanceSchematic;

import java.lang.Math;
import java.util.*;

public class MechaModelData {
	public final int numLegs;
	private final OutlinerNode hull;
	private final OutlinerNode chassis;

	private final List<OrdnanceInfo> ordnanceInfo;
	private final List<LegInfo> legInfo;

	private final Vector3fc relativeHullPosition;
	@Nullable
	private final Vec3 seatOffset;

	public MechaModelData(MechaSchematic schematic) {
		hull = Objects.requireNonNull(ModelOutlinerReloadListener.getNode(schematic.hull().id().withPrefix("hull/")))
			.stream()
			.filter(n -> n.name().equals("root"))
			.findAny()
			.orElseThrow();

		chassis = Objects.requireNonNull(ModelOutlinerReloadListener.getNode(schematic.chassis().id().withPrefix("chassis/")))
			.stream()
			.filter(n -> n.name().equals("root"))
			.findAny()
			.orElseThrow();

		ImmutableList.Builder<OrdnanceInfo> ordnanceInfoBuilder = ImmutableList.builder();

		for (int i = 1; i <= schematic.ordnance().size(); i++) {
			OrdnanceSchematic ordnanceSchematic = schematic.ordnance().get(i - 1);
			OutlinerNode ordnanceNode = Objects.requireNonNull(ModelOutlinerReloadListener.getNode(ordnanceSchematic.id().withPrefix("ordnance/")))
				.stream()
				.filter(n -> n.name().equals("root"))
				.findAny()
				.orElseThrow();

			List<OutlinerNode> markerNodes = ordnanceNode.children().stream()
				.filter(c -> c.left().isPresent())
				.map(c -> c.left().orElse(null))
				.filter(obj -> Objects.nonNull(obj) && obj.name().startsWith("marker"))
				.toList();

			Map<Integer, MarkerInfo> markers = new Int2ObjectArrayMap<>();

			markerNodes.forEach(node ->
				markers.put(
					Integer.parseInt(node.name().replaceAll("[^0-9]", "")),
					new MarkerInfo(node.origin(), RotationInfo.of(node.rotation()))
				)
			);

			OutlinerNode body = ordnanceNode.getChild("body").orElseThrow();
			OutlinerNode mountPoint = hull.getChild("ordnance" + i).orElseThrow();

			ordnanceInfoBuilder
				.add(new OrdnanceInfo(
					ordnanceNode.origin(),
					RotationInfo.of(ordnanceNode.rotation()),
					ImmutableMap.copyOf(markers),
					new MarkerInfo(body.origin(), RotationInfo.of(body.rotation())),
					new MountPoint(mountPoint.origin(), RotationInfo.of(mountPoint.rotation())),
					new RotationConstraints(
						mountPoint.parameters().getOrDefault("minYaw", 60.0).floatValue(),
						mountPoint.parameters().getOrDefault("maxYaw", 60.0).floatValue(),
						mountPoint.parameters().getOrDefault("minPitch", 45.0).floatValue(),
						mountPoint.parameters().getOrDefault("maxPitch", 90.0).floatValue()
					)
				));
		}

		ordnanceInfo = ordnanceInfoBuilder.build();

		ImmutableList.Builder<LegInfo> legInfoBuilder = ImmutableList.builder();

		numLegs = (int) chassis.children().stream()
			.filter(c -> c.left().map(n -> n.name().matches("^leg[0-9]$")).orElse(false))
			.count();

		for (int i = 1; i <= numLegs; i++) legInfoBuilder.add(LegInfo.of(chassis, "leg" + i));

		legInfo = legInfoBuilder.build();

		// temp: also include scale per-part
		// bbmodels are by default 16x actual coordinates, so all distances emitted by this
		// class need to be divided by 16.
		relativeHullPosition = chassis.getChild("hull").orElseThrow().origin().toVector3f();
		seatOffset = hull.getChild("seat").map(OutlinerNode::origin).orElse(null);
	}

	public OrdnanceInfo ordnanceInfo(OrdnancePart part, MechaCore core) {
		return ordnanceInfo.get(core.ordnanceIndex(part));
	}

	public OrdnanceInfo ordnanceInfo(int index) {
		return ordnanceInfo.get(index);
	}

	@Nullable
	public Vec3 seatOffset() {
		return seatOffset;
	}

	public List<LegInfo> legInfo() {
		return legInfo;
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

	public record MountPoint(Vec3 origin, RotationInfo rotationInfo) {
	}

	public record OrdnanceInfo(Vec3 origin, RotationInfo rotation, Map<Integer, MarkerInfo> markers,
							   MarkerInfo body, MountPoint mountPoint, RotationConstraints rotationConstraints) {
	}

	public record MarkerInfo(Vec3 origin, RotationInfo rotation) {
	}

	public record RotationConstraints(float minYaw, float maxYaw, float minPitch, float maxPitch) {
	}

	public record RotationInfo(Vec3 bbRotation, Vec3 direction, Quaternionfc rotation) {
		public static RotationInfo of(Vec3 bbRotation) {
			return new RotationInfo(bbRotation, GeometryUtil.bbRot2Direction(bbRotation), GeometryUtil.bbRot2Quaternion(bbRotation));
		}
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
					rootLegSegment.parameters().getOrDefault("minAngle", 45d),
					rootLegSegment.parameters().getOrDefault("maxAngle", 45d)
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
						segment.parameters().getOrDefault("minAngle", 45d),
						segment.parameters().getOrDefault("maxAngle", 45d)
					)
				);
				segment = nextNode.get();
				nextNode = getChild(segment, 0);
			}

			Vec3 legEnd = root.getChild(id + "_tip").orElseThrow().origin();
			segments.add(new SegmentInfo(
				Math.max(0.01f, 0.8 * tipPos.distance(legEnd.toVector3f())),
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
