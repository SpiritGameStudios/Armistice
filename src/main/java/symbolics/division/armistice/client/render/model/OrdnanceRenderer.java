package symbolics.division.armistice.client.render.model;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Quaternionf;
import org.joml.Vector2fc;
import symbolics.division.armistice.client.render.debug.ArmisticeClientDebugValues;
import symbolics.division.armistice.mecha.MechaEntity;
import symbolics.division.armistice.mecha.OrdnancePart;
import symbolics.division.armistice.model.BBModelTree;

import java.util.Map;
import java.util.Optional;

@OnlyIn(value = Dist.CLIENT)
public class OrdnanceRenderer {
	private static final Map<ResourceLocation, Renderer> RENDERERS = new Object2ObjectOpenHashMap<>();
	private static final OrdnanceRenderer MISSING = new OrdnanceRenderer();

	private final ResourceLocation texture;
	private final ModelBaker.Quad[] quads;
	private final ModelBaker.Quad[] bodyQuads;
	private final Vec3 bodyPos;

	public OrdnanceRenderer(BBModelTree tree, ResourceLocation id) {
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

		texture = ResourceLocation.fromNamespaceAndPath(
			id.getNamespace(),
			"textures/ordnance/" + id.getPath() + ".png"
		);
	}

	public static void addRenderer(ResourceLocation id, Renderer renderer) {
		RENDERERS.put(id, renderer);
	}

	private OrdnanceRenderer() {
		this.quads = ModelBaker.DEBUG_QUADS.toArray(new ModelBaker.Quad[0]);
		this.bodyQuads = new ModelBaker.Quad[0];
		texture = MissingTextureAtlasSprite.getLocation();
		bodyPos = Vec3.ZERO;
	}

	public static void dispatch(MechaEntity mecha, OrdnancePart ordnance, float tickDelta, PoseStack poseStack, MultiBufferSource bufferSource, int color, int packedLight, int packedOverlay) {
		poseStack.pushPose();
		{
			Optional.ofNullable(RENDERERS.get(ordnance.id()))
				.ifPresent(renderer -> renderer.render(mecha, ordnance, tickDelta, poseStack, bufferSource, color, packedLight, packedOverlay));

			ordnance.transformAbsolute(poseStack);

			PartRenderer.ordnance.getOrDefault(ordnance.id(), MISSING)
				.render(mecha, ordnance, tickDelta, poseStack, bufferSource, color, packedLight, packedOverlay);
		}
		poseStack.popPose();
	}

	public void render(MechaEntity mecha, OrdnancePart ordnance, float tickDelta, PoseStack pose, MultiBufferSource bufferSource, int color, int packedLight, int packedOverlay) {
		if (!ArmisticeClientDebugValues.showOrdnance) return;
		pose.pushPose();
		{
			var baseRotation = mecha.core().model().ordnanceInfo(ordnance, mecha.core()).mountPoint().rotationInfo().bbRotation()
				.scale(Mth.DEG_TO_RAD);

			Quaternionf baseRot = new Quaternionf().rotateZYX((float) baseRotation.z, (float) baseRotation.y, (float) baseRotation.x);
			pose.mulPose(baseRot);
			PartRenderer.renderQuads(quads, texture, pose.last(), bufferSource, color, packedLight, packedOverlay);
			if (bodyQuads.length > 0) {
				Vector2fc rot = mecha.core().ordnanceBarrelRotation(mecha.core().ordnanceIndex(ordnance));
				pose.translate(bodyPos.x, bodyPos.y, bodyPos.z);
				pose.mulPose(baseRot.conjugate());

				/// yaw, pitch -> x is raw y is pitch
				float yaw = rot.x() * Mth.DEG_TO_RAD;
				float pitch = -rot.y() * Mth.DEG_TO_RAD;

				// correct for backwards facing
//				float z = 0;
//				if (Mth.abs(rot.x()) >= 90) {
//					z = Mth.PI;
//					y = -y;
//				}

				// at this point we should pass these into the renders so we don't need to
				// play games with access
				Quaternionf newRot = new Quaternionf().rotateYXZ(yaw, pitch, 0);
				pose.mulPose(newRot);
				pose.translate(-bodyPos.x, -bodyPos.y, -bodyPos.z);
				PartRenderer.renderQuads(bodyQuads, texture, pose.last(), bufferSource, color, packedLight, packedOverlay);
			}
		}
		pose.popPose();
	}

	@FunctionalInterface
	public interface Renderer {
		void render(MechaEntity mecha, OrdnancePart ordnance, float tickDelta, PoseStack pose, MultiBufferSource bufferSource, int color, int packedLight, int packedOverlay);
	}
}
