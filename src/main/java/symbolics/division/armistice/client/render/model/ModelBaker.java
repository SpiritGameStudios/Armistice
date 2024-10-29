package symbolics.division.armistice.client.render.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.*;
import symbolics.division.armistice.model.BBModelTree;
import symbolics.division.armistice.model.Element;
import symbolics.division.armistice.model.OutlinerNode;

import java.util.*;
import java.util.function.Predicate;

public class ModelBaker {
	public static final int[][] VERTEX_INDICES = {
		new int[]{1, 5, 4, 0},
		new int[]{2, 6, 7, 3},
		new int[]{6, 2, 0, 4},
		new int[]{3, 7, 5, 1},
		new int[]{2, 3, 1, 0},
		new int[]{7, 6, 4, 5},
	};
	public static final List<Quad> DEBUG_QUADS;

	static {
		Element nullCube = new Element(
			"debug",
			false,
			false,
			false,
			0,
			Element.RenderOrder.DEFAULT,
			false,
			new Vec3(-16, -16, -16),
			new Vec3(16, 16, 16),
			0,
			0, Optional.empty(), Vec3.ZERO,
			Map.of(
				Direction.NORTH, new Element.Face(new Vector4d(0, 0, 256, 256), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()),
				Direction.EAST, new Element.Face(new Vector4d(0, 0, 256, 256), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()),
				Direction.SOUTH, new Element.Face(new Vector4d(0, 0, 256, 256), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()),
				Direction.WEST, new Element.Face(new Vector4d(0, 0, 256, 256), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()),
				Direction.UP, new Element.Face(new Vector4d(0, 0, 256, 256), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()),
				Direction.DOWN, new Element.Face(new Vector4d(0, 0, 256, 256), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty())
			),
			UUID.randomUUID()
		);

		List<Quad> quads = new ArrayList<>();
		addElement(quads, new PoseStack(), nullCube);
		DEBUG_QUADS = ImmutableList.copyOf(quads);
	}

	public static List<Quad> bake(BBModelTree tree) {
		return bake(tree, n -> true);
	}

	public static List<Quad> bake(BBModelTree tree, Predicate<BBModelTree> filter) {
		PoseStack poseStack = new PoseStack();
		var s = OutlinerNode.BASE_SCALE_FACTOR;
		poseStack.scale(s, s, s);
		return bake(tree, filter, poseStack);
	}

	public static List<Quad> bake(BBModelTree tree, Predicate<BBModelTree> filter, PoseStack poseStack) {
		return bake(new ArrayList<>(), tree, poseStack, filter);
	}

	public static List<Quad> bakeNoTransform(List<Quad> quads, BBModelTree tree, PoseStack poseStack, Predicate<BBModelTree> filter) {
		if (!filter.test(tree)) return quads;
		poseStack.pushPose();

		for (Element element : tree.elements()) addElement(quads, poseStack, element);
		for (BBModelTree child : tree.children()) bake(quads, child, poseStack, filter);

		poseStack.popPose();
		return quads;
	}

	private static List<Quad> bake(List<Quad> quads, BBModelTree tree, PoseStack poseStack, Predicate<BBModelTree> filter) {
		if (!filter.test(tree)) return quads;
		Vector3fc origin = tree.node.origin().toVector3f();
		Vector3fc rotation = tree.node.rotation().toVector3f();
		poseStack.pushPose();

		// rotate around this node's axis (vertex coordinates are global)
		poseStack.translate(origin.x(), origin.y(), origin.z());
		poseStack.mulPose(new Quaternionf().rotationZYX(rotation.z() * Mth.DEG_TO_RAD, rotation.y() * Mth.DEG_TO_RAD, rotation.x() * Mth.DEG_TO_RAD));
		poseStack.translate(-origin.x(), -origin.y(), -origin.z());

		for (Element element : tree.elements()) addElement(quads, poseStack, element);
		for (BBModelTree child : tree.children()) bake(quads, child, poseStack, filter);

		poseStack.popPose();
		return quads;
	}

	private static void addElement(List<Quad> quads, PoseStack poseStack, Element element) {
		poseStack.pushPose();
		poseStack.translate(element.origin().x, element.origin().y, element.origin().z);
		element.rotation().ifPresent(r ->
			poseStack.mulPose(new Quaternionf().rotationZYX((float) r.z * Mth.DEG_TO_RAD, (float) r.y * Mth.DEG_TO_RAD, (float) r.x * Mth.DEG_TO_RAD))
		);
		poseStack.translate(-element.origin().x, -element.origin().y, -element.origin().z);

		PoseStack.Pose transform = poseStack.last();

		Vector3f[] points = {
			vertex(0, element.from(), element.to(), transform.pose()),
			vertex(1, element.from(), element.to(), transform.pose()),
			vertex(2, element.from(), element.to(), transform.pose()),
			vertex(3, element.from(), element.to(), transform.pose()),
			vertex(4, element.from(), element.to(), transform.pose()),
			vertex(5, element.from(), element.to(), transform.pose()),
			vertex(6, element.from(), element.to(), transform.pose()),
			vertex(7, element.from(), element.to(), transform.pose()),
		};

		for (var entry : element.faces().entrySet()) {
			Direction dir = entry.getKey();
			Element.Face face = entry.getValue();
			if (face.texture().isEmpty()) {
				int a = 1;
				continue;
			}
			// null face, will have invalid uv
			int[] ix = VERTEX_INDICES[dir.get3DDataValue()];
			Vertex[] vertices = {
				Vertex.of(points[ix[0]], face.uv().x / 256f, face.uv().y / 256f),
				Vertex.of(points[ix[1]], face.uv().z / 256f, face.uv().y / 256f),
				Vertex.of(points[ix[2]], face.uv().z / 256f, face.uv().w / 256f),
				Vertex.of(points[ix[3]], face.uv().x / 256f, face.uv().w / 256f),
			};
			Vector3f normal = transform.transformNormal(dir.step(), new Vector3f());
			quads.add(new Quad(vertices, normal.x, normal.y, normal.z));
		}
		poseStack.popPose();
	}

	/*
	    3-------7
       /|      /|
      2-+-----6 |
      | |     | |   y
      | 1-----+-5   | z
      |/      |/    |/
      0-------4     +--x
	 */

	private static Vector3f vertex(int ordinal, Vec3 from, Vec3 to, Matrix4f transform) {
		return transform.transformPosition(
			(ordinal & 0b100) == 0 ? (float) from.x : (float) to.x,
			(ordinal & 0b010) == 0 ? (float) from.y : (float) to.y,
			(ordinal & 0b001) == 0 ? (float) from.z : (float) to.z,
			new Vector3f()
		);
	}

	public record Quad(Vertex[] vertices, float nx, float ny, float nz) {
	}

	public record Vertex(float x, float y, float z, float u, float v) {
		public static Vertex of(Vector3fc vx, double u, double v) {
			return new Vertex(vx.x(), vx.y(), vx.z(), (float) u, (float) v);
		}
	}
}
