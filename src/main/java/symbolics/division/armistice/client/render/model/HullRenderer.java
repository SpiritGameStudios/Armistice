package symbolics.division.armistice.client.render.model;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import symbolics.division.armistice.Armistice;
import symbolics.division.armistice.mecha.MechaEntity;
import symbolics.division.armistice.mecha.NullOrdnancePart;
import symbolics.division.armistice.model.BBModelTree;

@OnlyIn(Dist.CLIENT)
public class HullRenderer {
	private static final ResourceLocation TEST_TEXTURE = Armistice.id("textures/mecha/skin/skin_template.png");

	public static void dispatch(MechaEntity mecha, PoseStack poseStack, MultiBufferSource bufferSource, int color, int packedLight, int packedOverlay) {
		poseStack.pushPose();
//		PartRenderer.hull.get(mecha.core().schematic().hull().id())
//			.render(poseStack.last(), bufferSource, color, packedLight, packedOverlay);
		// for each ordnance, translate and draw
		poseStack.translate(0, 5, 0);
		for (int i = 0; i < mecha.core().ordnance().size(); i++) {
			if (mecha.core().ordnance().get(i) instanceof NullOrdnancePart) continue;
			OrdnanceRenderer.dispatch(mecha, i, poseStack, bufferSource, color, packedLight, packedOverlay);
		}
		poseStack.popPose();
	}

	private final ModelBaker.Quad[] quads;

	public HullRenderer(BBModelTree tree) {
		quads = ModelBaker.bake(tree, new PoseStack()).toArray(ModelBaker.Quad[]::new);
	}

	public void render(PoseStack.Pose pose, MultiBufferSource bufferSource, int color, int packedLight, int packedOverlay) {
		PartRenderer.renderQuads(quads, TEST_TEXTURE, pose, bufferSource, color, packedLight, packedOverlay);
	}
}
