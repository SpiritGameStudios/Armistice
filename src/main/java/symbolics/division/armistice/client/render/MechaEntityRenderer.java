package symbolics.division.armistice.client.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import symbolics.division.armistice.Armistice;
import symbolics.division.armistice.client.render.model.PartRenderer;
import symbolics.division.armistice.mecha.MechaEntity;

import java.util.OptionalDouble;

@OnlyIn(Dist.CLIENT)
public class MechaEntityRenderer extends EntityRenderer<MechaEntity> {
	private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("armistice", "textures/block/mecha/skin_test.png");

	public static final RenderType LINE_STRIP = RenderType.create(
		"line_strip",
		DefaultVertexFormat.POSITION_COLOR,
		VertexFormat.Mode.LINE_STRIP,
		1536,
		RenderType.CompositeState.builder()
			.setShaderState(RenderType.RENDERTYPE_LINES_SHADER)
			.setLineState(new RenderStateShard.LineStateShard(OptionalDouble.of(4)))
			.setTransparencyState(RenderType.ADDITIVE_TRANSPARENCY)
			.setOutputState(RenderType.ITEM_ENTITY_TARGET)
			.setDepthTestState(RenderType.NO_DEPTH_TEST)
			.setCullState(RenderType.NO_CULL)
			.createCompositeState(false)
	);

	public MechaEntityRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public @NotNull ResourceLocation getTextureLocation(@NotNull MechaEntity entity) {
		return TEXTURE;
	}

	@Override
	public void render(MechaEntity mecha, float entityYaw, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight) {
		int color = 0xFFFFFFFF;

		//  render in absolute space

//		var d = mecha.core().direction().toVector3f();
//		var angle = Mth.atan2(d.x, d.z);
//		poseStack.mulPose(new Quaternionf().rotationZYX(0, (float) angle, 0));

		if (mecha.core().entity() == null) {
			if (mecha.tickCount % 20 == 0) {
				Armistice.LOGGER.error("trying to render mecha without entity set -- core must not have been initialized.");
			}
			return;
		}
		if (Minecraft.getInstance().player == null) return;
		if (!mecha.core().entity().hasPassenger(Minecraft.getInstance().player)) {
			PartRenderer.renderParts(mecha, partialTick, poseStack, bufferSource, color, packedLight, OverlayTexture.NO_OVERLAY);
		} else {
			mecha.core().mapChassisRender(chassis -> {
				poseStack.pushPose();

				poseStack.translate(-mecha.position().x, -mecha.position().y, -mecha.position().z);
				chassis.renderDebug(bufferSource, poseStack);

				poseStack.popPose();

			});
		}
	}
}
