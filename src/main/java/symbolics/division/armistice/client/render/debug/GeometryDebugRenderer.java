package symbolics.division.armistice.client.render.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import symbolics.division.armistice.mecha.movement.KinematicsSolver;

import java.util.function.Consumer;

public class GeometryDebugRenderer {
	public static final Vec3 FORWARD = new Vec3(0, 0, 1);
	public static Vec3 b1 = FORWARD;
	public static Vec3 b2 = FORWARD;
	public static Vec3 b3 = FORWARD;
	public static Vec3 norm = new Vec3(-1, 0, 0);
	public static double max = Math.PI / 4;
	public static double min = -Math.PI / 4;

	public static void render(RenderLevelStageEvent event, LocalPlayer player, MultiBufferSource buf, Vec3 camera, PoseStack matrices) {
		matrices.pushPose();
		matrices.translate(-camera.x, -camera.y, -camera.z);

		var vc = buf.getBuffer(RenderType.debugLineStrip(4));
		vertex(vc, matrices, b1, 1, 1, 1);
		vertex(vc, matrices, b2, 1, 1, 1);
		vertex(vc, matrices, b3, 1, 1, 1);

		var vcNorm = buf.getBuffer(RenderType.debugLineStrip(4));
		vertex(vcNorm, matrices, b1, 0, 1, 1);
		vertex(vcNorm, matrices, b1.add(norm), 0, 1, 1);

		var vcNorm2 = buf.getBuffer(RenderType.debugLineStrip(4));
		vertex(vcNorm2, matrices, b2, 0, 1, 1);
		vertex(vcNorm2, matrices, b2.add(norm), 0, 1, 1);
		matrices.popPose();
	}

	public static void setAll(Vec3 pos) {
		b1 = pos;
		b2 = b1.add(FORWARD);
		b3 = b2.add(FORWARD);
	}

	public static void vertex(VertexConsumer vc, PoseStack pose, Vec3 p, float r, float g, float b) {
		vc.addVertex(pose.last(), p.toVector3f()).setColor(r, g, b, 1.0f);
	}

	public static void rotate(double r) {
		var v = FORWARD.toVector3f().rotateAxis((float) (Math.PI * r), -1, 0, 0);
		b3 = b2.add(new Vec3(v));
	}

	public static void update() {
		var s2dir = b3.subtract(b2);
		var s1dir = b2.subtract(b1);
		b3 = b2.add(KinematicsSolver.clampPlanarAngle(s2dir, s1dir, norm, min, max));
	}

	public static LiteralArgumentBuilder<CommandSourceStack> registerSubCommands(LiteralArgumentBuilder<CommandSourceStack> cmd) {
		return cmd.then(Commands.literal("leg")
			.requires(src -> src.hasPermission(Commands.LEVEL_ADMINS))
			.executes(ctx -> {
				Vec3 p = ctx.getSource().getPosition();
				setAll(p);
				return Command.SINGLE_SUCCESS;
			}).then(
				addCmd("max", v -> max = v * Math.PI)
			).then(
				addCmd("min", v -> min = v * Math.PI)
			).then(addCmd("rotate", GeometryDebugRenderer::rotate))
			.then(addCmd("update", v -> update())));
	}

	public static LiteralArgumentBuilder<CommandSourceStack> addCmd(String name, Consumer<Double> c) {
		return Commands.literal(name).then(
			Commands.argument(name, DoubleArgumentType.doubleArg())
				.executes(ctx -> {
					c.accept(DoubleArgumentType.getDouble(ctx, name));
					return Command.SINGLE_SUCCESS;
				}));
	}
}
