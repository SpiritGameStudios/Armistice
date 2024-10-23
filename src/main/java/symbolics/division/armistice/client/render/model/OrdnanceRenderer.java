package symbolics.division.armistice.client.render.model;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import symbolics.division.armistice.Armistice;
import symbolics.division.armistice.mecha.MechaEntity;
import symbolics.division.armistice.model.BBModelTree;

public class OrdnanceRenderer {
	private static final ResourceLocation TEST_TEXTURE = Armistice.id("textures/mecha/skin/ordnance_skin_test.png");

	public static void dispatch(MechaEntity mecha, int ordnance, PoseStack poseStack, MultiBufferSource bufferSource, int color, int packedLight, int packedOverlay) {
		poseStack.pushPose();
		PartRenderer.ordnance.get(mecha.core().schematic().ordnance().get(ordnance).id())
			.render(poseStack.last(), bufferSource, color, packedLight, packedOverlay);
		poseStack.popPose();
	}

	private final ModelBaker.Quad[] quads;

	public OrdnanceRenderer(BBModelTree tree) {
		quads = ModelBaker.bake(tree, new PoseStack()).toArray(ModelBaker.Quad[]::new);
	}

	public void render(PoseStack.Pose pose, MultiBufferSource bufferSource, int color, int packedLight, int packedOverlay) {
		PartRenderer.renderQuads(quads, TEST_TEXTURE, pose, bufferSource, color, packedLight, packedOverlay);
	}
}
