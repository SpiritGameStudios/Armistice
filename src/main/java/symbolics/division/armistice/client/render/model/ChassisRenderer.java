package symbolics.division.armistice.client.render.model;

import au.edu.federation.caliko.FabrikChain3D;
import au.edu.federation.utils.Vec3f;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import symbolics.division.armistice.Armistice;
import symbolics.division.armistice.client.render.debug.ArmisticeClientDebugValues;
import symbolics.division.armistice.mecha.MechaEntity;
import symbolics.division.armistice.model.BBModelTree;
import symbolics.division.armistice.model.OutlinerNode;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChassisRenderer {
	private static final ResourceLocation TEST_TEXTURE = Armistice.id("textures/mecha/skin/chassis_skin_test.png");

	private static final ChassisRenderer MISSING = new ChassisRenderer();

	private final ResourceLocation texture;
	private final ModelBaker.Quad[] quads;
	private final LegRenderer[] legRenderers;

	private final class LegRenderer {
		private final List<List<ModelBaker.Quad>> segmentQuads = new ArrayList<>();
		private final List<ModelBaker.Quad[]> quadArrays;
		private final List<OutlinerNode> segmentNodes = new ArrayList<>();
		private final int index;

		public LegRenderer(BBModelTree leg, int index) {
			this.index = index;
			addSegment(leg);
			quadArrays = segmentQuads.stream().map(q -> q.toArray(ModelBaker.Quad[]::new)).toList();
		}

		private void addSegment(BBModelTree segment) {
			BBModelTree nextNode = null;
			segmentNodes.add(segment.node);
			for (var child : segment.children()) {
				nextNode = child;
				break;
			}
			if (nextNode != null) {
				String cname = nextNode.node.name();
				segmentQuads.add(ModelBaker.bake(segment, n -> !n.node.name().equals(cname)));
				addSegment(nextNode);
			} else {
				segmentQuads.add(ModelBaker.bake(segment));
			}

			// undo baking operation: go to pivot, unrotate, go back

		}

		public void render(MechaEntity mecha, PoseStack matrices, MultiBufferSource bufferSource, int color, int packedLight, int packedOverlay) {
			FabrikChain3D chain = mecha.core().skeleton().getChain(index + 1);
			// skip fake base bone that's not present in model
			for (int i = 0; i < quadArrays.size(); i++) {
				if (quadArrays.get(i).length != 0) {
					var bone = chain.getBone(i + 1);
					float yaw = bone.getGlobalYawDegs() * Mth.DEG_TO_RAD;
					float pitch = bone.getGlobalPitchDegs() * Mth.DEG_TO_RAD;
					Vec3f base = bone.getStartLocation();
					matrices.pushPose();
					{
//						matrices.mulPose(new Quaternionf().rotateZYX(0, yaw, pitch));
						var p = mecha.position();
						matrices.translate(base.x, base.y, base.z);
//						segmentNodes.get()
						PartRenderer.renderQuads(quadArrays.get(i), texture, matrices.last(), bufferSource, color, packedLight, packedOverlay);
					}
					matrices.popPose();
				}
			}
		}
	}

	private ChassisRenderer() {
		quads = ModelBaker.DEBUG_QUADS.toArray(new ModelBaker.Quad[0]);
		texture = MissingTextureAtlasSprite.getLocation();
		legRenderers = new LegRenderer[0];
	}

	Pattern legName = Pattern.compile("^leg([0-9]+)$");

	public ChassisRenderer(BBModelTree tree) {
		quads = ModelBaker.bake(tree, t -> !t.node.name().startsWith("leg")).toArray(ModelBaker.Quad[]::new);
		int count = (int) tree.children().stream().filter(c -> legName.matcher(c.node.name()).find()).count();
		legRenderers = new LegRenderer[count];
		for (var child : tree.children()) {
			Matcher m = legName.matcher(child.node.name());
			if (m.find()) {
				count++;
				int index = Integer.parseInt(m.group(1));
				legRenderers[index - 1] = new LegRenderer(child, index - 1);
			}
		}
		texture = TEST_TEXTURE;
	}

	public static void dispatch(MechaEntity mecha, PoseStack poseStack, MultiBufferSource bufferSource, int color, int packedLight, int packedOverlay) {
		// draw self, calls armor render, calls hull render

		var chassis = PartRenderer.chassis.getOrDefault(mecha.core().schematic().chassis().id(), MISSING);
		if (chassis != null) {
			poseStack.pushPose();
			mecha.core().chassisEuclidean().transformAbsolute(poseStack);
			chassis.render(poseStack.last(), bufferSource, color, packedLight, packedOverlay);
			poseStack.popPose();
			for (var leg : chassis.legRenderers) {
				leg.render(mecha, poseStack, bufferSource, color, packedLight, packedOverlay);
			}
		} else if (mecha.tickCount % 20 == 0) {
			Armistice.LOGGER.error("chassis model not found: {}", mecha.core().schematic().chassis().id());
		}
	}

	public void render(PoseStack.Pose pose, MultiBufferSource bufferSource, int color, int packedLight, int packedOverlay) {
		if (!ArmisticeClientDebugValues.showChassis) return;
		PartRenderer.renderQuads(quads, texture, pose, bufferSource, color, packedLight, packedOverlay);
	}
}
