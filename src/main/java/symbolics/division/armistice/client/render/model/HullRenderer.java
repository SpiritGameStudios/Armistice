package symbolics.division.armistice.client.render.model;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import symbolics.division.armistice.Armistice;
import symbolics.division.armistice.mecha.MechaEntity;
import symbolics.division.armistice.model.BBModelTree;

@OnlyIn(Dist.CLIENT)
public class HullRenderer {
	private static final ResourceLocation TEST_TEXTURE = Armistice.id("textures/mecha/skin/skin_template.png");

	public static void dispatch(MechaEntity mecha, PoseStack poseStack, MultiBufferSource bufferSource, int color, int packedLight, int packedOverlay) {
		var id = mecha.core().schematic().hull().id();
		var renderer = PartRenderer.hull.get(id);
		if (renderer != null) {
			poseStack.pushPose();
			mecha.core().hullEuclidean().transformAbsolute(poseStack);
			renderer.render(poseStack.last(), bufferSource, color, packedLight, packedOverlay);
			poseStack.popPose();
		} else if (mecha.tickCount % 20 == 0) {
			Armistice.LOGGER.error("hull model not found: {}", mecha.core().schematic().hull().id());
		}
	}

	private final ModelBaker.Quad[] quads;

	public HullRenderer(BBModelTree tree) {
		quads = ModelBaker.bake(tree).toArray(ModelBaker.Quad[]::new);
	}

	public void render(PoseStack.Pose pose, MultiBufferSource bufferSource, int color, int packedLight, int packedOverlay) {
		PartRenderer.renderQuads(quads, TEST_TEXTURE, pose, bufferSource, color, packedLight, packedOverlay);
	}
}
