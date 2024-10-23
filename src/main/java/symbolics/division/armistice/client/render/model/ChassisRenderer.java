package symbolics.division.armistice.client.render.model;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import symbolics.division.armistice.Armistice;
import symbolics.division.armistice.mecha.MechaEntity;
import symbolics.division.armistice.model.BBModelTree;

public class ChassisRenderer {
	private static final ResourceLocation TEST_TEXTURE = Armistice.id("textures/mecha/skin/chassis_skin_test.png");

	public static void dispatch(MechaEntity mecha, PoseStack poseStack, MultiBufferSource bufferSource, int color, int packedLight, int packedOverlay) {
		poseStack.pushPose();
		// draw self, calls armor render, calls hull render
		var chassis = PartRenderer.chassis.get(mecha.core().schematic().chassis().id());
		if (chassis != null) {
			chassis.render(poseStack.last(), bufferSource, color, packedLight, packedOverlay);
			HullRenderer.dispatch(mecha, poseStack, bufferSource, color, packedLight, packedOverlay);
		} else if (mecha.tickCount % 20 == 0) {
			Armistice.LOGGER.error("chassis model not available");
		}
		poseStack.popPose();
	}

	private final ModelBaker.Quad[] quads;

	public ChassisRenderer(BBModelTree tree) {
		quads = ModelBaker.bake(tree, new PoseStack()).toArray(ModelBaker.Quad[]::new);
	}

	public void render(PoseStack.Pose pose, MultiBufferSource bufferSource, int color, int packedLight, int packedOverlay) {
		PartRenderer.renderQuads(quads, TEST_TEXTURE, pose, bufferSource, color, packedLight, packedOverlay);
	}
}
