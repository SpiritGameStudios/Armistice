package symbolics.division.armistice.client.render.model;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import org.joml.Quaternionf;
import org.joml.Vector2fc;
import symbolics.division.armistice.Armistice;
import symbolics.division.armistice.client.render.debug.ArmisticeClientDebugValues;
import symbolics.division.armistice.mecha.MechaEntity;
import symbolics.division.armistice.model.BBModelTree;

public class OrdnanceRenderer {
	private static final ResourceLocation TEST_TEXTURE = Armistice.id("textures/mecha/skin/ordnance_skin_test.png");
	private static final OrdnanceRenderer MISSING = new OrdnanceRenderer();

	private final ResourceLocation texture;
	private final ModelBaker.Quad[] quads;
	private final ModelBaker.Quad[] bodyQuads;

	public OrdnanceRenderer(BBModelTree tree) {
		quads = ModelBaker.bake(tree, child -> child.node.name().equals("body")).toArray(ModelBaker.Quad[]::new);
		BBModelTree body = tree.child("body");
		if (body != null) {
			bodyQuads = ModelBaker.bake(body).toArray(ModelBaker.Quad[]::new);
		} else {
			bodyQuads = new ModelBaker.Quad[0];
		}
		texture = TEST_TEXTURE;
	}

	private OrdnanceRenderer() {
		this.quads = ModelBaker.DEBUG_QUADS.toArray(new ModelBaker.Quad[0]);
		this.bodyQuads = new ModelBaker.Quad[0];
		texture = TEST_TEXTURE;
	}

	public static void dispatch(MechaEntity mecha, int ordnance, PoseStack poseStack, MultiBufferSource bufferSource, int color, int packedLight, int packedOverlay) {
		poseStack.pushPose();
		mecha.core().ordnanceEuclidean(ordnance)
			.transformAbsolute(poseStack);

		PartRenderer.ordnance.getOrDefault(mecha.core().schematic().ordnance().get(ordnance).id(), MISSING)
			.render(mecha, ordnance, poseStack, bufferSource, color, packedLight, packedOverlay);
		poseStack.popPose();
	}

	public void render(MechaEntity mecha, int ordnance, PoseStack pose, MultiBufferSource bufferSource, int color, int packedLight, int packedOverlay) {
		if (!ArmisticeClientDebugValues.showOrdnance) return;
		PartRenderer.renderQuads(quads, texture, pose.last(), bufferSource, color, packedLight, packedOverlay);
		if (bodyQuads.length > 0) {
			pose.pushPose();
			{
				Vector2fc rot = mecha.core().ordnanceBarrelRotation(ordnance);
				pose.mulPose(new Quaternionf().rotateZYX(0, rot.y(), rot.x()));
				PartRenderer.renderQuads(bodyQuads, texture, pose.last(), bufferSource, color, packedLight, packedOverlay);
			}
			pose.popPose();
		}
	}
}
