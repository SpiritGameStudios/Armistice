package symbolics.division.armistice.client.render.ordnance;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import symbolics.division.armistice.client.render.MechaEntityRenderer;
import symbolics.division.armistice.projectile.HitscanBullet;

public class HitscanBulletRenderer extends EntityRenderer<HitscanBullet> {
	public HitscanBulletRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public ResourceLocation getTextureLocation(HitscanBullet entity) {
		return MechaEntityRenderer.TEXTURE;
	}

	@Override
	public void render(HitscanBullet bullet, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
		poseStack.pushPose();
		poseStack.translate(-bullet.getX(), -bullet.getY(), -bullet.getZ());
		var vc = bufferSource.getBuffer(RenderType.DEBUG_LINE_STRIP.apply(4d));
		vc.addVertex(poseStack.last(), bullet.position().toVector3f()).setColor(-1);
		vc.addVertex(poseStack.last(), bullet.end().toVector3f()).setColor(-1);
		poseStack.popPose();
	}
}
