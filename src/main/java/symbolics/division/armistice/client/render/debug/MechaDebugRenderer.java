package symbolics.division.armistice.client.render.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import symbolics.division.armistice.registry.ArmisticeEntityTypeRegistrar;

@OnlyIn(value = Dist.CLIENT)
public final class MechaDebugRenderer {
	@SubscribeEvent
	private static void onRenderLevelStage(RenderLevelStageEvent event) {
		if (!ArmisticeClientDebugValues.debugRenderer || event.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES)
			return;
		Vec3 camera = event.getCamera().getPosition();
		PoseStack poseStack = event.getPoseStack();
		poseStack.pushPose();

		MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
		LocalPlayer player = Minecraft.getInstance().player;
		if (player == null) return;

		player.level().getEntities(ArmisticeEntityTypeRegistrar.MECHA, player.getBoundingBox().inflate(100.0), ignored -> true).forEach(mecha -> {
			if (!mecha.core().ready()) return;

			poseStack.pushPose();

			Vec3 offset = mecha.position().subtract(camera);
			poseStack.translate(offset.x, offset.y, offset.z);
			poseStack.translate(-mecha.position().x, -mecha.position().y, -mecha.position().z);

			mecha.core().renderDebug(bufferSource, poseStack);

			poseStack.popPose();
		});

		GeometryDebugRenderer.render(event, player, bufferSource, camera, poseStack);
		poseStack.popPose();
	}
}
