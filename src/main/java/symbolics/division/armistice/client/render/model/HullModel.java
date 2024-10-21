package symbolics.division.armistice.client.render.model;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import symbolics.division.armistice.Armistice;
import symbolics.division.armistice.mecha.MechaEntity;
import symbolics.division.armistice.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class HullModel {
	/*
		Take model data, retrieve elements and bake into vertices
	 */

	private static final ResourceLocation TEXTURE = Armistice.id("textures/mecha/skin/skin_test.png");

	private static final Map<ResourceLocation, HullModel> models = new Object2ObjectLinkedOpenHashMap<>();

	public static void compileModels() {
		models.clear();
		for (ResourceLocation id : ModelOutlinerReloadListener.getNodes().keySet()) {
			models.put(id, new HullModel(id));
		}
	}

	public static void render(MechaEntity mecha, PoseStack.Pose pose, MultiBufferSource bufferSource, int color, int packedLight, int packedOverlay) {
		models.get(mecha.core().schematic().chassis().id().withPrefix("chassis/"))
			.render(pose, bufferSource, color, packedLight, packedOverlay);
	}

	private final Quad[] quads;

	public HullModel(ResourceLocation hullId) {
		List<Element> elements = ModelElementReloadListener.getModel(hullId);
		List<OutlinerNode> nodes = ModelOutlinerReloadListener.getNode(hullId);
		BBModelTree tree = new BBModelTree(new BBModelData(elements, nodes));
		quads = compile(new ArrayList<>(), tree, new PoseStack()).toArray(Quad[]::new);
	}

	public void render(PoseStack.Pose pose, MultiBufferSource bufferSource, int color, int packedLight, int packedOverlay) {
		VertexConsumer vc = bufferSource.getBuffer(RenderType.entityCutout(TEXTURE));
		Vector3f norm = new Vector3f();
		Vector3f pos = new Vector3f();
		for (Quad face : quads) {
			pose.transformNormal(face.nx, face.ny, face.nz, norm);
			// temp: did we get the winding order wrong?
			for (int i = 3; i >= 0; i--) {
				Vertex v = face.vertices[i];
				pose.pose().transformPosition(v.x, v.y, v.z, pos);
				vc.addVertex(pos.x, pos.y, pos.z, color, v.u, v.v, packedOverlay, packedLight, norm.x, norm.y, norm.z);
			}
		}
	}

	private record Quad(Vertex[] vertices, float nx, float ny, float nz) {
	}

	private record Vertex(float x, float y, float z, float u, float v) {
		public static Vertex of(Vector3fc vx, double u, double v) {
			return new Vertex(vx.x(), vx.y(), vx.z(), (float) u, (float) v);
		}
	}

	private static List<Quad> compile(List<Quad> quads, BBModelTree tree, PoseStack poseStack) {
		Vector3fc origin = tree.node.origin().toVector3f();
		Vector3fc rotation = tree.node.rotation().toVector3f();
		poseStack.pushPose();

		// rotate around this node's axis (vertex coordinates are global)
		poseStack.translate(origin.x(), origin.y(), origin.z());
		poseStack.mulPose(new Quaternionf().rotationZYX(rotation.x(), rotation.y(), rotation.z()));
		poseStack.translate(-origin.x(), -origin.y(), -origin.z());

		for (Element element : tree.elements()) addElement(quads, poseStack, element);
		for (BBModelTree child : tree.children()) compile(quads, child, poseStack);

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

	private static final int[][] VERTEX_INDICES = {
		new int[]{1, 5, 4, 0},
		new int[]{2, 6, 7, 3},
		new int[]{5, 1, 3, 7},
		new int[]{0, 4, 6, 2},
		new int[]{1, 0, 2, 3},
		new int[]{4, 5, 7, 6},
	};

	private static void addElement(List<Quad> quads, PoseStack poseStack, Element element) {
		poseStack.pushPose();
		poseStack.translate(element.origin().x, element.origin().y, element.origin().z);
		element.rotation().ifPresent(r -> {
			poseStack.mulPose(new Quaternionf().rotationZYX((float) r.x, (float) r.y, (float) r.z));
		});

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
			int[] ix = VERTEX_INDICES[dir.get3DDataValue()];
			Vertex[] vertices = {
				Vertex.of(points[ix[0]], face.uv().w / 256f, face.uv().x / 256f),
				Vertex.of(points[ix[1]], face.uv().y / 256f, face.uv().x / 256f),
				Vertex.of(points[ix[2]], face.uv().y / 256f, face.uv().z / 256f),
				Vertex.of(points[ix[3]], face.uv().w / 256f, face.uv().z / 256f),
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
