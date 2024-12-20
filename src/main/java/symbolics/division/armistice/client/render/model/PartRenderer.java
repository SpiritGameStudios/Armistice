package symbolics.division.armistice.client.render.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Vector3f;
import symbolics.division.armistice.Armistice;
import symbolics.division.armistice.mecha.MechaEntity;
import symbolics.division.armistice.mecha.OrdnancePart;
import symbolics.division.armistice.mecha.ordnance.NullOrdnancePart;
import symbolics.division.armistice.model.BBModelTree;

import java.util.Map;

@OnlyIn(value = Dist.CLIENT)
public class PartRenderer {
	protected static final Map<ResourceLocation, ChassisRenderer> chassis = new Object2ObjectLinkedOpenHashMap<>();
	protected static final Map<ResourceLocation, HullRenderer> hull = new Object2ObjectLinkedOpenHashMap<>();
	protected static final Map<ResourceLocation, OrdnanceRenderer> ordnance = new Object2ObjectLinkedOpenHashMap<>();

	public static void bakeModels(Map<ResourceLocation, BBModelTree> models) {
		chassis.clear();
		hull.clear();
		ordnance.clear();

		for (Map.Entry<ResourceLocation, BBModelTree> entry : models.entrySet()) {
			String[] path = entry.getKey().getPath().split("/", 2);
			if (path.length < 2) {
				Armistice.LOGGER.error("No part type give for mecha model path {}", entry.getKey());
				continue;
			}

			ResourceLocation id = ResourceLocation.fromNamespaceAndPath(entry.getKey().getNamespace(), path[1]);

			switch (path[0]) {
				case "chassis" -> chassis.put(id, new ChassisRenderer(entry.getValue(), id));
				case "hull" -> hull.put(id, new HullRenderer(entry.getValue(), id));
				case "ordnance" -> ordnance.put(id, new OrdnanceRenderer(entry.getValue(), id));
				default -> Armistice.LOGGER.error("invalid part type: {}", entry.getKey());
			}
		}
	}

	public static void renderPilotParts(MechaEntity mecha, float tickDelta, PoseStack pose, MultiBufferSource bufferSource, int color, int packedLight, int packedOverlay) {
		pose.pushPose();
		{
			// temp: just render ordnance only, ordnance renderer detects and draws polyhedra
			pose.translate(-mecha.core().position().x(), -mecha.core().position().y(), -mecha.core().position().z());
			for (int i = 0; i < mecha.core().ordnance().size(); i++) {
				OrdnancePart part = mecha.core().ordnance().get(i);
				if (part instanceof NullOrdnancePart) continue;
				OrdnanceRenderer.dispatch(mecha, part, tickDelta, pose, bufferSource, color, packedLight, packedOverlay);
			}
		}
		pose.popPose();
	}

	public static void renderParts(MechaEntity mecha, float tickDelta, PoseStack pose, MultiBufferSource bufferSource, int color, int packedLight, int packedOverlay) {
		// render in absolute space to ensure we match internal representation
		pose.pushPose();
		{
			pose.translate(-mecha.core().position().x(), -mecha.core().position().y(), -mecha.core().position().z());

			ChassisRenderer.dispatch(mecha, pose, bufferSource, color, packedLight, packedOverlay);
			HullRenderer.dispatch(mecha, pose, bufferSource, color, packedLight, packedOverlay);
			for (int i = 0; i < mecha.core().ordnance().size(); i++) {
				OrdnancePart part = mecha.core().ordnance().get(i);
				if (part instanceof NullOrdnancePart) continue;
				OrdnanceRenderer.dispatch(mecha, part, tickDelta, pose, bufferSource, color, packedLight, packedOverlay);
			}
		}
		pose.popPose();
	}

	public static void renderQuads(ModelBaker.Quad[] quads, ResourceLocation texture, PoseStack.Pose pose, MultiBufferSource bufferSource, int color, int packedLight, int packedOverlay) {
		VertexConsumer vc = bufferSource.getBuffer(RenderType.entityTranslucent(texture));
		Vector3f norm = new Vector3f();
		Vector3f pos = new Vector3f();

		for (ModelBaker.Quad face : quads) {
			pose.transformNormal(face.nx(), face.ny(), face.nz(), norm);
			// todo: winding order compiled backwards, should be cw not ccw
			for (int i = face.vertices().length - 1; i >= 0; i--) {
				ModelBaker.Vertex v = face.vertices()[i];
				pose.pose().transformPosition(v.x(), v.y(), v.z(), pos);
				vc.addVertex(pos.x, pos.y, pos.z, color, v.u(), v.v(), packedOverlay, packedLight, norm.x, norm.y, norm.z);
			}
		}
	}
}
