package symbolics.division.armistice.client.render.model;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import symbolics.division.armistice.model.BBModelTree;
import symbolics.division.armistice.model.Element;

import java.util.ArrayList;
import java.util.List;

public class ModelBaker {
	public record Quad(Vertex[] vertices, float nx, float ny, float nz) {
	}

	public record Vertex(float x, float y, float z, float u, float v) {
		public static Vertex of(Vector3fc vx, double u, double v) {
			return new Vertex(vx.x(), vx.y(), vx.z(), (float) u, (float) v);
		}
	}

	public static List<Quad> bake(BBModelTree tree, PoseStack poseStack) {
		return bake(new ArrayList<>(), tree, poseStack);
	}

	private static List<Quad> bake(List<Quad> quads, BBModelTree tree, PoseStack poseStack) {
		Vector3fc origin = tree.node.origin().toVector3f();
		Vector3fc rotation = tree.node.rotation().toVector3f();
		poseStack.pushPose();

		// rotate around this node's axis (vertex coordinates are global)
		poseStack.translate(origin.x(), origin.y(), origin.z());
		poseStack.mulPose(new Quaternionf().rotationZYX(rotation.z() * Mth.DEG_TO_RAD, rotation.y() * Mth.DEG_TO_RAD, rotation.x() * Mth.DEG_TO_RAD));
		poseStack.translate(-origin.x(), -origin.y(), -origin.z());

		for (Element element : tree.elements()) addElement(quads, poseStack, element);
		for (BBModelTree child : tree.children()) bake(quads, child, poseStack);

		poseStack.popPose();
		return quads;
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

	public static final int[][] VERTEX_INDICES = {
		new int[]{1, 5, 4, 0},
		new int[]{2, 6, 7, 3},
		new int[]{6, 2, 0, 4},
		new int[]{3, 7, 5, 1},
		new int[]{2, 3, 1, 0},
		new int[]{7, 6, 4, 5},
	};

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
			; // null face, will have invalid uv
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

	private static Vector3f vertex(int ordinal, Vec3 from, Vec3 to, Matrix4f transform) {
		return transform.transformPosition(
			(ordinal & 0b100) == 0 ? (float) from.x : (float) to.x,
			(ordinal & 0b010) == 0 ? (float) from.y : (float) to.y,
			(ordinal & 0b001) == 0 ? (float) from.z : (float) to.z,
			new Vector3f()
		);
	}
}
