package symbolics.division.armistice.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import symbolics.division.armistice.client.render.model.TestModel;
import symbolics.division.armistice.mecha.MechaEntity;

@OnlyIn(Dist.CLIENT)
public class MechaEntityRenderer extends EntityRenderer<MechaEntity> {
	private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("armistice", "textures/block/mecha/skin_test.png");

	public MechaEntityRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public @NotNull ResourceLocation getTextureLocation(@NotNull MechaEntity entity) {
		return TEXTURE;
	}

	@Override
	public void render(MechaEntity mecha, float entityYaw, float partialTick, PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight) {
		BakedModel test = Minecraft.getInstance().getModelManager().getModel(TestModel.TEST_MODEL);

		RenderType renderType = RenderType.cutout();
		VertexConsumer consumer = bufferSource.getBuffer(renderType);

		poseStack.pushPose();
		poseStack.scale(5, 5, 5);
		for (BakedQuad quad : test.getQuads(null, null, mecha.getRandom(), ModelData.EMPTY, null)) {
			consumer.putBulkData(poseStack.last(), quad, 1f, 1f, 1f, 1f, packedLight, OverlayTexture.NO_OVERLAY);
		}

		poseStack.popPose();
	}
}
