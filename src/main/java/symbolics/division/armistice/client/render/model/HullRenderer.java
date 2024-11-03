package symbolics.division.armistice.client.render.model;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import symbolics.division.armistice.Armistice;
import symbolics.division.armistice.client.render.debug.ArmisticeClientDebugValues;
import symbolics.division.armistice.mecha.MechaEntity;
import symbolics.division.armistice.model.BBModelTree;

@OnlyIn(Dist.CLIENT)
public class HullRenderer {
	private static final HullRenderer MISSING = new HullRenderer();

	private final ResourceLocation texture;
	private final ModelBaker.Quad[] quads;

	private HullRenderer() {
		this.quads = ModelBaker.DEBUG_QUADS.toArray(new ModelBaker.Quad[0]);
		this.texture = MissingTextureAtlasSprite.getLocation();
	}

	public HullRenderer(BBModelTree tree, ResourceLocation id) {
		quads = ModelBaker.bake(tree).toArray(ModelBaker.Quad[]::new);
		texture = ResourceLocation.fromNamespaceAndPath(
			id.getNamespace(),
			"textures/hull/standard.png"
//			"textures/hull/" + id.getPath() + ".png"
		);
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
		if (!ArmisticeClientDebugValues.showHull) return;
		PartRenderer.renderQuads(quads, texture, pose, bufferSource, color, packedLight, packedOverlay);
	}
}
