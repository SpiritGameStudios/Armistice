package symbolics.division.armistice.client.render.model;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import symbolics.division.armistice.Armistice;
import symbolics.division.armistice.client.render.debug.ArmisticeClientDebugValues;
import symbolics.division.armistice.mecha.MechaEntity;
import symbolics.division.armistice.model.BBModelTree;

public class ChassisRenderer {
	private static final ResourceLocation TEST_TEXTURE = Armistice.id("textures/mecha/skin/chassis_skin_test.png");

	private static final ChassisRenderer MISSING = new ChassisRenderer();

	private final ResourceLocation texture;
	private final ModelBaker.Quad[] quads;

	private ChassisRenderer() {
		quads = ModelBaker.DEBUG_QUADS.toArray(new ModelBaker.Quad[0]);
		texture = MissingTextureAtlasSprite.getLocation();
	}

	public ChassisRenderer(BBModelTree tree) {
		quads = ModelBaker.bake(tree).toArray(ModelBaker.Quad[]::new);
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
		} else if (mecha.tickCount % 20 == 0) {
			Armistice.LOGGER.error("chassis model not found: {}", mecha.core().schematic().chassis().id());
		}
	}

	public void render(PoseStack.Pose pose, MultiBufferSource bufferSource, int color, int packedLight, int packedOverlay) {
		if (!ArmisticeClientDebugValues.showChassis) return;
		PartRenderer.renderQuads(quads, texture, pose, bufferSource, color, packedLight, packedOverlay);
	}
}
