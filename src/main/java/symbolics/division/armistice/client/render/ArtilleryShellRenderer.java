package symbolics.division.armistice.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import symbolics.division.armistice.projectile.AbstractOrdnanceProjectile;

// TODO make actual renderer. For now it's just Spectral Arrow renderer
public class ArtilleryShellRenderer extends EntityRenderer<AbstractOrdnanceProjectile> {

	public static final ResourceLocation SPECTRAL_ARROW_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/projectiles/spectral_arrow.png");
//	public static final ResourceLocation PROJECTILE_TEXTURES = ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/projectiles.png");

	public ArtilleryShellRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	public void render(AbstractOrdnanceProjectile entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
		poseStack.pushPose();
		poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, entity.yRotO, entity.getYRot()) - 90.0F));
		poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTicks, entity.xRotO, entity.getXRot())));

		poseStack.mulPose(Axis.XP.rotationDegrees(45.0F));
		poseStack.scale(0.05625F, 0.05625F, 0.05625F);
		poseStack.translate(-4.0F, 0.0F, 0.0F);
		VertexConsumer vertexconsumer = buffer.getBuffer(RenderType.entityCutout(this.getTextureLocation(entity)));
		PoseStack.Pose posestack$pose = poseStack.last();
		this.vertex(posestack$pose, vertexconsumer, -7, -2, -2, 0.0F, 0.15625F, -1, 0, 0, packedLight);
		this.vertex(posestack$pose, vertexconsumer, -7, -2, 2, 0.15625F, 0.15625F, -1, 0, 0, packedLight);
		this.vertex(posestack$pose, vertexconsumer, -7, 2, 2, 0.15625F, 0.3125F, -1, 0, 0, packedLight);
		this.vertex(posestack$pose, vertexconsumer, -7, 2, -2, 0.0F, 0.3125F, -1, 0, 0, packedLight);
		this.vertex(posestack$pose, vertexconsumer, -7, 2, -2, 0.0F, 0.15625F, 1, 0, 0, packedLight);
		this.vertex(posestack$pose, vertexconsumer, -7, 2, 2, 0.15625F, 0.15625F, 1, 0, 0, packedLight);
		this.vertex(posestack$pose, vertexconsumer, -7, -2, 2, 0.15625F, 0.3125F, 1, 0, 0, packedLight);
		this.vertex(posestack$pose, vertexconsumer, -7, -2, -2, 0.0F, 0.3125F, 1, 0, 0, packedLight);

		for(int j = 0; j < 4; ++j) {
			poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
			this.vertex(posestack$pose, vertexconsumer, -8, -2, 0, 0.0F, 0.0F, 0, 1, 0, packedLight);
			this.vertex(posestack$pose, vertexconsumer, 8, -2, 0, 0.5F, 0.0F, 0, 1, 0, packedLight);
			this.vertex(posestack$pose, vertexconsumer, 8, 2, 0, 0.5F, 0.15625F, 0, 1, 0, packedLight);
			this.vertex(posestack$pose, vertexconsumer, -8, 2, 0, 0.0F, 0.15625F, 0, 1, 0, packedLight);
		}

		poseStack.popPose();
		super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
	}

	public void vertex(PoseStack.Pose pose, VertexConsumer consumer, int x, int y, int z, float u, float v, int normalX, int normalY, int normalZ, int packedLight) {
		consumer.addVertex(pose, (float)x, (float)y, (float)z).setColor(-1).setUv(u, v).setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(pose, (float)normalX, (float)normalZ, (float)normalY);
	}

	@Override
	public ResourceLocation getTextureLocation(AbstractOrdnanceProjectile artilleryShell) {
		return SPECTRAL_ARROW_LOCATION;
	}
}
