package symbolics.division.armistice.client.render.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.commands.Commands;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Vector3f;
import symbolics.division.armistice.mecha.movement.Leggy;
import symbolics.division.armistice.registry.ArmisticeEntityTypeRegistrar;

import java.util.List;

public final class MechaDebugRenderer {
	private static boolean enabled = true;

	@SubscribeEvent
	private static void onRenderLevelStage(RenderLevelStageEvent event) {
		if (!enabled || event.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES) return;
		Vec3 camera = event.getCamera().getPosition();
		PoseStack poseStack = event.getPoseStack();
		poseStack.pushPose();

		MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();

		LocalPlayer player = Minecraft.getInstance().player;
		if (player == null) return;

		player.level().getEntities(ArmisticeEntityTypeRegistrar.MECHA, player.getBoundingBox().inflate(100.0), ignored -> true).forEach(mecha -> {
			poseStack.pushPose();

			Vec3 offset = mecha.position().subtract(camera);
			poseStack.translate(offset.x, offset.y, offset.z);
			poseStack.translate(-mecha.position().x, -mecha.position().y, -mecha.position().z);

			List<Leggy> debugGetLegs = mecha.core().debugGetChassis().debugGetLegs();
			for (int i = 0; i < debugGetLegs.size(); i++) {
				Leggy leg = debugGetLegs.get(i);

				VertexConsumer lineStrip2 = bufferSource.getBuffer(RenderType.debugLineStrip(2.0));
				for (Vec3 joint : leg.jointPositions())
					lineStrip2.addVertex(poseStack.last(), joint.toVector3f()).setColor(1.0f, 1.0f, 1.0f, 1.0f);

				VertexConsumer quad = bufferSource.getBuffer(RenderType.debugQuads());
				Vec3 target = mecha.core().debugGetChassis().debugStepTargets.get(i);

				quad.addVertex(poseStack.last(), target.add(-1, 0, -1).toVector3f()).setColor(1.0f, 0.0f, 0.0f, 1.0f);
				quad.addVertex(poseStack.last(), target.add(1, 0, -1).toVector3f()).setColor(1.0f, 0.0f, 0.0f, 1.0f);
				quad.addVertex(poseStack.last(), target.add(1, 0, 1).toVector3f()).setColor(1.0f, 0.0f, 0.0f, 1.0f);
				quad.addVertex(poseStack.last(), target.add(-1, 0, 1).toVector3f()).setColor(1.0f, 0.0f, 0.0f, 1.0f);
			}

			VertexConsumer lineStrip4 = bufferSource.getBuffer(RenderType.debugLineStrip(4.0));

			Vec3 posUp = mecha.position().add(0, 1, 0);
			Vector3f adjustedDirection = posUp.add(mecha.core().direction()).toVector3f();

			lineStrip4.addVertex(poseStack.last(), posUp.toVector3f()).setColor(1.0f, 0.0f, 0.0f, 1.0f);
			lineStrip4.addVertex(poseStack.last(), adjustedDirection).setColor(1.0f, 0.0f, 0.0f, 1.0f);
			lineStrip4.addVertex(poseStack.last(), adjustedDirection).setColor(0.0f, 1.0f, 0.0f, 1.0f);
			lineStrip4.addVertex(poseStack.last(), mecha.core().debugGetChassis().getPathingTarget().toVector3f()).setColor(0.0f, 1.0f, 0.0f, 1.0f);

			poseStack.popPose();
		});

		poseStack.popPose();
	}

	@SubscribeEvent
	private static void onRegisterClientCommands(RegisterClientCommandsEvent event) {
		event.getDispatcher().register(
			Commands.literal("armistice_debug")
				.then(Commands.literal("mecha")
					.requires(src -> src.hasPermission(Commands.LEVEL_ADMINS))
					.then(Commands.argument("enable", BoolArgumentType.bool())
						.executes(ctx -> {
							enabled = BoolArgumentType.getBool(ctx, "enable");
							return Command.SINGLE_SUCCESS;
						}))));
	}
}
