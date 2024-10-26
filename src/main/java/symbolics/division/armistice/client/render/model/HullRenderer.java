package symbolics.division.armistice.client.render.model;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import symbolics.division.armistice.Armistice;
import symbolics.division.armistice.mecha.MechaEntity;
import symbolics.division.armistice.model.BBModelTree;

@OnlyIn(Dist.CLIENT)
public class HullRenderer {
	private static final ResourceLocation TEST_TEXTURE = Armistice.id("textures/mecha/skin/skin_template.png");

	private static final HullRenderer MISSING = new HullRenderer();

	private final ResourceLocation texture;
	private final ModelBaker.Quad[] quads;

	private HullRenderer() {
		this.quads = ModelBaker.DEBUG_QUADS.toArray(new ModelBaker.Quad[0]);
		this.texture = MissingTextureAtlasSprite.getLocation();
	}

	public HullRenderer(BBModelTree tree) {
		quads = ModelBaker.bake(tree).toArray(ModelBaker.Quad[]::new);
		texture = TEST_TEXTURE;
	}

	public static void dispatch(MechaEntity mecha, PoseStack poseStack, MultiBufferSource bufferSource, int color, int packedLight, int packedOverlay) {
		var id = mecha.core().schematic().hull().id();
		var renderer = PartRenderer.hull.getOrDefault(id, MISSING);
		if (renderer != null) {
			poseStack.pushPose();
			mecha.core().hullEuclidean().transformAbsolute(poseStack);
			renderer.render(poseStack.last(), bufferSource, color, packedLight, packedOverlay);
			poseStack.popPose();
		} else if (mecha.tickCount % 20 == 0) {
			Armistice.LOGGER.error("hull model not found: {}", mecha.core().schematic().hull().id());
		}
	}

	public void render(PoseStack.Pose pose, MultiBufferSource bufferSource, int color, int packedLight, int packedOverlay) {
		PartRenderer.renderQuads(quads, texture, pose, bufferSource, color, packedLight, packedOverlay);
	}
}
