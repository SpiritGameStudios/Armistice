package symbolics.division.armistice.client.render;

import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EnderDragonRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import symbolics.division.armistice.mecha.MechaEntity;

@OnlyIn(Dist.CLIENT)
public class MechaEntityRenderer extends EntityRenderer<MechaEntity> {
	private static final ResourceLocation TEXTURE = ResourceLocation.withDefaultNamespace("textures/block/cobblestone.png");

	public MechaEntityRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public ResourceLocation getTextureLocation(MechaEntity entity) {
		return TEXTURE;
	}

	@Override
	public void render(MechaEntity mecha, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
//		var tess = Tesselator.getInstance();
//		var bb = tess.begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);
		poseStack.pushPose();
		poseStack.translate(-mecha.getX(), -mecha.getY(), -mecha.getZ());
		for (var leg : mecha.legs()) {
			var vc = bufferSource.getBuffer(RenderType.debugLineStrip(2.0));
			for (var joint : leg.jointPositions()) {
				vc.addVertex(poseStack.last(), joint.toVector3f()).setColor(1.0f, 1.0f, 1.0f, 1.0f);
			}
		}
		poseStack.popPose();
	}
}
