package symbolics.division.armistice.client.render.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

public class GeometryDebugRenderer {
	public static final Vec3 FORWARD = new Vec3(0, 0, 1);
	public static Vec3 b1 = FORWARD;
	public static Vec3 b2 = FORWARD;
	public static Vec3 b3 = FORWARD;
	public static Vec3 norm = new Vec3(-1, 0, 0);

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
	}
}
