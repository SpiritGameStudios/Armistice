package symbolics.division.armistice.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import symbolics.division.armistice.mecha.MechaEntity;

@OnlyIn(Dist.CLIENT)
public class MechaEntityRenderer extends EntityRenderer<MechaEntity> {
	private static final ResourceLocation TEXTURE = ResourceLocation.withDefaultNamespace("textures/block/cobblestone.png");

	public MechaEntityRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public @NotNull ResourceLocation getTextureLocation(@NotNull MechaEntity entity) {
		return TEXTURE;
	}

	@Override
	public void render(MechaEntity mecha, float entityYaw, float partialTick, PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight) {
		poseStack.pushPose();
		poseStack.translate(-mecha.getX(), -mecha.getY(), -mecha.getZ());
		int i = 0;
		for (var leg : mecha.legs()) {
			var vc = bufferSource.getBuffer(RenderType.debugLineStrip(2.0));
			for (var joint : leg.jointPositions()) {
				vc.addVertex(poseStack.last(), joint.toVector3f()).setColor(1.0f, 1.0f, 1.0f, 1.0f);
			}


			var vc2 = bufferSource.getBuffer(RenderType.debugQuads());
			Vec3 t = mecha.stepAreas.get(i);
			i++;
			vc2.addVertex(poseStack.last(), t.add(-1, 0, -1).toVector3f()).setColor(1.0f, 0.0f, 0.0f, 1.0f);
			vc2.addVertex(poseStack.last(), t.add(1, 0, -1).toVector3f()).setColor(1.0f, 0.0f, 0.0f, 1.0f);
			vc2.addVertex(poseStack.last(), t.add(1, 0, 1).toVector3f()).setColor(1.0f, 0.0f, 0.0f, 1.0f);
			vc2.addVertex(poseStack.last(), t.add(-1, 0, 1).toVector3f()).setColor(1.0f, 0.0f, 0.0f, 1.0f);
		}
		var lookvc = bufferSource.getBuffer(RenderType.debugLineStrip(4.0));
		Vec3 p = mecha.position().add(0, 1, 0);
		lookvc.addVertex(poseStack.last(), p.toVector3f()).setColor(1.0f, 0.0f, 0.0f, 1.0f);
		Vec3 look = mecha.direction.curDir();
		var look_adjusted = p.add(look).toVector3f();
		lookvc.addVertex(poseStack.last(), look_adjusted).setColor(1.0f, 0.0f, 0.0f, 1.0f);
		lookvc.addVertex(poseStack.last(), look_adjusted).setColor(0.0f, 1.0f, 0.0f, 1.0f);
		lookvc.addVertex(poseStack.last(), mecha.followPos.toVector3f()).setColor(0.0f, 1.0f, 0.0f, 1.0f);
		poseStack.popPose();
	}
}
