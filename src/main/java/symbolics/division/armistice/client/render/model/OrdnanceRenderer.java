package symbolics.division.armistice.client.render.model;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import symbolics.division.armistice.Armistice;
import symbolics.division.armistice.client.render.debug.ArmisticeClientDebugValues;
import symbolics.division.armistice.mecha.MechaEntity;
import symbolics.division.armistice.model.BBModelTree;

public class OrdnanceRenderer {
	private static final ResourceLocation TEST_TEXTURE = Armistice.id("textures/mecha/skin/ordnance_skin_test.png");
	private static final OrdnanceRenderer MISSING = new OrdnanceRenderer();

	private final ResourceLocation texture;
	private final ModelBaker.Quad[] quads;

	public OrdnanceRenderer(BBModelTree tree) {
		quads = ModelBaker.bake(tree).toArray(ModelBaker.Quad[]::new);
		texture = TEST_TEXTURE;
	}

	private OrdnanceRenderer() {
		this.quads = ModelBaker.DEBUG_QUADS.toArray(new ModelBaker.Quad[0]);
		texture = TEST_TEXTURE;
	}

	public static void dispatch(MechaEntity mecha, int ordnance, PoseStack poseStack, MultiBufferSource bufferSource, int color, int packedLight, int packedOverlay) {
		poseStack.pushPose();
		mecha.core().ordnanceEuclidean(ordnance)
			.transformAbsolute(poseStack);

		PartRenderer.ordnance.getOrDefault(mecha.core().schematic().ordnance().get(ordnance).id(), MISSING)
			.render(poseStack.last(), bufferSource, color, packedLight, packedOverlay);
		poseStack.popPose();
	}

	public void render(PoseStack.Pose pose, MultiBufferSource bufferSource, int color, int packedLight, int packedOverlay) {
		if (!ArmisticeClientDebugValues.showOrdnance) return;
		PartRenderer.renderQuads(quads, texture, pose, bufferSource, color, packedLight, packedOverlay);
	}
}
