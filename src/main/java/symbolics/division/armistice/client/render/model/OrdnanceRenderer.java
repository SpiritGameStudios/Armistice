package symbolics.division.armistice.client.render.model;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector2fc;
import symbolics.division.armistice.Armistice;
import symbolics.division.armistice.client.render.debug.ArmisticeClientDebugValues;
import symbolics.division.armistice.mecha.MechaEntity;
import symbolics.division.armistice.mecha.OrdnancePart;
import symbolics.division.armistice.model.BBModelTree;

public class OrdnanceRenderer {
	private static final ResourceLocation TEST_TEXTURE = Armistice.id("textures/mecha/skin/ordnance_skin_test.png");
	private static final OrdnanceRenderer MISSING = new OrdnanceRenderer();

	private final ResourceLocation texture;
	private final ModelBaker.Quad[] quads;
	private final ModelBaker.Quad[] bodyQuads;
	private final Vec3 bodyPos;

	public OrdnanceRenderer(BBModelTree tree) {
		quads = ModelBaker.bake(tree, child -> !child.node.name().equals("body")).toArray(ModelBaker.Quad[]::new);
		BBModelTree body = tree.child("body");
		if (body != null) {
			bodyPos = body.node.origin();
			bodyQuads = ModelBaker.bake(
				body,
				n -> true
			).toArray(ModelBaker.Quad[]::new);
		} else {
			bodyQuads = new ModelBaker.Quad[0];
			bodyPos = Vec3.ZERO;
		}
		texture = TEST_TEXTURE;
	}

	private OrdnanceRenderer() {
		this.quads = ModelBaker.DEBUG_QUADS.toArray(new ModelBaker.Quad[0]);
		this.bodyQuads = new ModelBaker.Quad[0];
		texture = TEST_TEXTURE;
		bodyPos = Vec3.ZERO;
	}

	public static void dispatch(MechaEntity mecha, int ordnance, float tickDelta, PoseStack poseStack, MultiBufferSource bufferSource, int color, int packedLight, int packedOverlay) {
		poseStack.pushPose();
		mecha.core().ordnanceEuclidean(ordnance)
			.transformAbsolute(poseStack);

		PartRenderer.ordnance.getOrDefault(mecha.core().schematic().ordnance().get(ordnance).id(), MISSING)
			.render(mecha, ordnance, tickDelta, poseStack, bufferSource, color, packedLight, packedOverlay);
		poseStack.popPose();
	}

	public void render(MechaEntity mecha, int ordnance, float tickDelta, PoseStack pose, MultiBufferSource bufferSource, int color, int packedLight, int packedOverlay) {
		if (!ArmisticeClientDebugValues.showOrdnance) return;
		pose.pushPose();
		{
			var baseRotation = mecha.core().model().ordnanceInfo(ordnance).mountPoint().rotationInfo().bbRotation()
				.scale(Mth.DEG_TO_RAD);

			pose.mulPose(new Quaternionf().rotateZYX((float) baseRotation.z, (float) baseRotation.y, (float) baseRotation.x));
			PartRenderer.renderQuads(quads, texture, pose.last(), bufferSource, color, packedLight, packedOverlay);
			if (bodyQuads.length > 0) {
				Vector2fc rot = mecha.core().ordnanceBarrelRotation(ordnance);
				pose.translate(bodyPos.x, bodyPos.y, bodyPos.z);
				float x = -rot.x() * Mth.DEG_TO_RAD;
				float y = -rot.y() * Mth.DEG_TO_RAD;

				// correct for backwards facing
				float z = 0;
				if (Mth.abs(rot.x()) >= 90) {
					z = Mth.PI;
					y = -y;
				}

				// at this point we should pass these into the renders so we don't need to
				// play games with access
				OrdnancePart part = mecha.core().ordnance().get(ordnance);
				Quaternionf newRot = new Quaternionf().rotateYXZ(y, x, z);
				Quaternionf lerpedRot = part.lastRenderRotation.nlerp(newRot, tickDelta);

				pose.mulPose(lerpedRot);
				pose.translate(-bodyPos.x, -bodyPos.y, -bodyPos.z);
				PartRenderer.renderQuads(bodyQuads, texture, pose.last(), bufferSource, color, packedLight, packedOverlay);

				part.lastRenderRotation = lerpedRot;
			}
		}
		pose.popPose();
	}
}
