package symbolics.division.armistice.client.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.joml.Vector4f;
import symbolics.division.armistice.Armistice;
import symbolics.division.armistice.client.render.hud.MechaHudRenderer;
import symbolics.division.armistice.client.render.model.PartRenderer;
import symbolics.division.armistice.mecha.MechaEntity;

import java.util.OptionalDouble;
import java.util.function.BiConsumer;

@OnlyIn(Dist.CLIENT)
public class MechaEntityRenderer extends EntityRenderer<MechaEntity> {
	public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("armistice", "textures/block/mecha/skin_test.png");

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
		if (mecha.core().entity() == null) {
			if (mecha.tickCount % 20 == 0) {
				Armistice.LOGGER.error("trying to render mecha without entity set -- core must not have been initialized.");
			}
			return;
		}

		if (Minecraft.getInstance().player == null) return;
		if (!mecha.core().entity().hasPassenger(Minecraft.getInstance().player)) {
			PartRenderer.renderParts(mecha, partialTick, poseStack, bufferSource, 0xFFFFFFFF, packedLight, OverlayTexture.NO_OVERLAY);
		} else {
			PartRenderer.renderPilotParts(mecha, partialTick, poseStack, bufferSource, 0xFFFFFFFF, packedLight, OverlayTexture.NO_OVERLAY);
			mecha.core().mapChassisRender(skeleton -> {
				poseStack.pushPose();

				poseStack.translate(-mecha.position().x, -mecha.position().y, -mecha.position().z);
				for (int i = 0; i < skeleton.getNumChains(); i++) {
					skeleton.getChain(i).getChain().forEach(
						bone -> renderHologramFlicker(
							(pos, col) -> drawSeg(
								new Vector3f(bone.getStartLocationAsArray()).add(pos.toVector3f()),
								new Vector3f(bone.getEndLocationAsArray()).add(pos.toVector3f()),
								poseStack,
								bufferSource,
								col
							),
							MechaHudRenderer.lightbulbColor(),
							mecha.getRandom()
						)
					);
				}

				poseStack.popPose();
			});
		}
	}

	public static void drawSeg(Vector3f p1, Vector3f p2, PoseStack poseStack, MultiBufferSource bf, Vector4f color) {
		VertexConsumer bufferBuilder = bf.getBuffer(MechaEntityRenderer.LINE_STRIP);

		bufferBuilder.addVertex(poseStack.last(), p1).setColor(color.x, color.y, color.z, color.w);
		bufferBuilder.addVertex(poseStack.last(), p2).setColor(color.x, color.y, color.z, color.w);
	}

	public static void renderHologramFlicker(BiConsumer<Vec3, Vector4f> render, Vector4f color, RandomSource randomSource) {
		float alpha = randomSource.nextInt(10) == 0 ? Math.min(randomSource.nextFloat(), 0.25F) : 1.0F;
		alpha = (0.3F + (alpha * 0.7F)) * color.w;

		render.accept(Vec3.ZERO.offsetRandom(randomSource, 0.0025F), new Vector4f(color.x, color.y, color.z, alpha));
		render.accept(Vec3.ZERO.offsetRandom(randomSource, 0.075F), new Vector4f(color.x, color.y, color.z, alpha));
	}
}
