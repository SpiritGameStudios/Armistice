package symbolics.division.armistice.client.render.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import symbolics.division.armistice.registry.ArmisticeEntityTypeRegistrar;

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

			mecha.core().renderDebug(bufferSource, poseStack);

			poseStack.popPose();
		});

		GeometryDebugRenderer.render(event, player, bufferSource, camera, poseStack);
		poseStack.popPose();
	}

	public static LiteralArgumentBuilder<CommandSourceStack> registerClientCommands(LiteralArgumentBuilder<CommandSourceStack> cmd) {
		var sub = Commands.literal("mecha")
			.requires(src -> src.hasPermission(Commands.LEVEL_ADMINS))
			.then(Commands.argument("enable", BoolArgumentType.bool())
				.executes(ctx -> {
					enabled = BoolArgumentType.getBool(ctx, "enable");
					return Command.SINGLE_SUCCESS;
				}));
		sub = GeometryDebugRenderer.registerSubCommands(sub);
		return cmd.then(sub);
	}
}
