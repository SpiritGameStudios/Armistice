package symbolics.division.armistice.client.render.model;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Quaternionf;
import org.joml.Vector2fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import symbolics.division.armistice.client.render.MechaEntityRenderer;
import symbolics.division.armistice.client.render.debug.ArmisticeClientDebugValues;
import symbolics.division.armistice.client.render.hud.MechaHudRenderer;
import symbolics.division.armistice.client.render.hud.MechaOverlayRenderer;
import symbolics.division.armistice.mecha.MechaEntity;
import symbolics.division.armistice.mecha.OrdnancePart;
import symbolics.division.armistice.mecha.ordnance.HitscanGunOrdnance;
import symbolics.division.armistice.model.BBModelTree;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@OnlyIn(value = Dist.CLIENT)
public class OrdnanceRenderer {
	private static final Map<ResourceLocation, Renderer> RENDERERS = new Object2ObjectOpenHashMap<>();
	private static final OrdnanceRenderer MISSING = new OrdnanceRenderer();

	private static final Vector3f[] holoTips = {
		new Vector3f(-2f, -1.5f, 0).mul(0.2f),
		new Vector3f(2f, -1.5f, 0).mul(0.2f),
		new Vector3f(0f, 1.5f, 0).mul(0.2f),
	};

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
		boolean riding = MechaOverlayRenderer.shouldProcessMechaOverlay() && mecha.hasPassenger(Objects.requireNonNull(Minecraft.getInstance().player));
		pose.pushPose();
		{
			var baseRotation = mecha.core().model().ordnanceInfo(ordnance, mecha.core()).mountPoint().rotationInfo().bbRotation()
				.scale(Mth.DEG_TO_RAD);

			Quaternionf baseRot = new Quaternionf().rotateZYX((float) baseRotation.z, (float) baseRotation.y, (float) baseRotation.x);
			pose.mulPose(baseRot);
			if (!riding) {
				PartRenderer.renderQuads(quads, texture, pose.last(), bufferSource, color, packedLight, packedOverlay);
			}
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

				if (riding) {
					pose.mulPose(new Quaternionf().rotateZ((float) mecha.tickCount / 30));
					drawHoloOrdnance(pose, bufferSource, ordnance, mecha);
//					drawHoloSegment(new Vector3f(), new Vector3f(0, 0, barrelLength), pose, bufferSource, mecha.getRandom());
				} else {
					pose.translate(-bodyPos.x, -bodyPos.y, -bodyPos.z);
					Vector3fc absPos = ordnance.absPos();
					BlockPos pos = new BlockPos(
						Mth.floor(absPos.x()),
						Mth.floor(absPos.y()),
						Mth.floor(absPos.z())
					);

					int light = LightTexture.pack(
						mecha.level().getBrightness(
							LightLayer.BLOCK,
							pos
						),
						mecha.level().getBrightness(
							LightLayer.SKY,
							pos
						)
					);

					PartRenderer.renderQuads(bodyQuads, texture, pose.last(), bufferSource, color, light, packedOverlay);
				}
			}
		}
		pose.popPose();
	}

	@FunctionalInterface
	public interface Renderer {
		void render(MechaEntity mecha, OrdnancePart ordnance, float tickDelta, PoseStack pose, MultiBufferSource bufferSource, int color, int packedLight, int packedOverlay);
	}

	private static void drawHoloOrdnance(PoseStack poseStack, MultiBufferSource bufferSource, OrdnancePart ordnance, MechaEntity mecha) {
		float barrelLength = (float) ordnance.modelInfo().markers().get(1).origin().z();
		Vector3f tipPos = new Vector3f(0, 0, barrelLength);
		drawHoloSegment(holoTips[0], holoTips[1], poseStack, bufferSource, mecha.getRandom());
		drawHoloSegment(holoTips[1], holoTips[2], poseStack, bufferSource, mecha.getRandom());
		drawHoloSegment(holoTips[2], holoTips[0], poseStack, bufferSource, mecha.getRandom());
		drawHoloSegment(holoTips[0], tipPos, poseStack, bufferSource, mecha.getRandom());
		drawHoloSegment(holoTips[1], tipPos, poseStack, bufferSource, mecha.getRandom());
		drawHoloSegment(holoTips[2], tipPos, poseStack, bufferSource, mecha.getRandom());
		if (ordnance instanceof HitscanGunOrdnance) {
			drawHoloSegment(tipPos, tipPos.mul(1000, new Vector3f()), poseStack, bufferSource, mecha.getRandom());
		}
	}

	private static void drawHoloSegment(Vector3f from, Vector3f to, PoseStack poseStack, MultiBufferSource bufferSource, RandomSource random) {
		MechaEntityRenderer.renderHologramFlicker(
			(pos, col) -> MechaEntityRenderer.drawSeg(
				from, to, poseStack, bufferSource, col
			), MechaHudRenderer.lightbulbColor(), random
		);
	}
}
