package symbolics.division.armistice.client.render.model;

import au.edu.federation.caliko.FabrikBone3D;
import au.edu.federation.utils.Vec3f;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import symbolics.division.armistice.Armistice;
import symbolics.division.armistice.client.render.debug.ArmisticeClientDebugValues;
import symbolics.division.armistice.mecha.MechaEntity;
import symbolics.division.armistice.mecha.MechaSkin;
import symbolics.division.armistice.mecha.movement.ChassisLeg;
import symbolics.division.armistice.mecha.movement.IKUtil;
import symbolics.division.armistice.model.BBModelTree;
import symbolics.division.armistice.model.OutlinerNode;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChassisRenderer {
	private static final ChassisRenderer MISSING = new ChassisRenderer();

	private final ModelBaker.Quad[] quads;
	private final LegRenderer[] legRenderers;

	private final class LegRenderer {
		private final List<List<ModelBaker.Quad>> segmentQuads = new ArrayList<>();
		private final List<ModelBaker.Quad[]> quadArrays;
		private final List<OutlinerNode> segmentNodes = new ArrayList<>();
		private final int index;

		public LegRenderer(BBModelTree leg, int index) {
			this.index = index;
			PoseStack matrices = new PoseStack();
			var s = OutlinerNode.BASE_SCALE_FACTOR;
			matrices.scale(s, s, s);
			addSegment(leg, matrices);

			quadArrays = segmentQuads.stream().map(q -> q.toArray(ModelBaker.Quad[]::new)).toList();
		}

		private void addSegment(BBModelTree segment, PoseStack matrices) {
			BBModelTree nextNode = null;
			segmentNodes.add(segment.node);
			for (var child : segment.children()) {
				nextNode = child;
				break;
			}

			if (nextNode != null) {
				String cname = nextNode.node.name();
				matrices.pushPose();
				{
					segmentQuads.add(ModelBaker.bakeNoTransform(new ArrayList<>(), segment, matrices, n -> !n.node.name().equals(cname)));
					addSegment(nextNode, matrices);
				}
				matrices.popPose();
			} else {
				segmentQuads.add(ModelBaker.bakeNoTransform(new ArrayList<>(), segment, matrices, n -> true));
			}
		}

		public void render(MechaEntity mecha, PoseStack matrices, MultiBufferSource bufferSource, int color, int packedLight, int packedOverlay) {
			ChassisLeg leg = mecha.core().leg(index);
			List<FabrikBone3D> bones = leg.getBonesForRender();
			// skip fake base bone that's not present in model
			float parentYaw = Mth.PI - bones.get(1).getGlobalYawDegs();

			MechaSkin skin = mecha.core().skin();
			for (int i = 0; i < quadArrays.size(); i++) {
				if (quadArrays.get(i).length != 0) {
					var bone = bones.get(i + 1);
					float yaw = bone.getGlobalYawDegs() * Mth.DEG_TO_RAD;
					Vec3f base = bone.getStartLocation();
					matrices.pushPose();
					{
						matrices.translate(base.x, base.y, base.z);
						yaw = Mth.PI - yaw;
						float calcPitch = interpretPitch(bone, yaw, parentYaw);

						var seg = segmentNodes.get(i).origin();
						matrices.mulPose(new Quaternionf().rotateZYX(0, yaw, calcPitch + Mth.PI));
						matrices.translate(-seg.x, -seg.y, -seg.z);
						PartRenderer.renderQuads(
							quadArrays.get(i),
							ResourceLocation.fromNamespaceAndPath(skin.id().getNamespace(), "textures/chassis/" + skin.id().getPath() + ".png"),
							matrices.last(),
							bufferSource,
							color,
							packedLight,
							packedOverlay
						);
					}
					matrices.popPose();
				}
			}
		}

		private float interpretPitch(FabrikBone3D bone, float yawRad, float yawParentRad) {
			// global pitch in caliko is weird
			Vec3 start = IKUtil.f2m(bone.getStartLocation());
			Vec3 end = IKUtil.f2m(bone.getEndLocation());
			double y = end.y - start.y;
			double x = end.x - start.x;
			double z = end.z - start.z;
			double r = Math.sqrt(x * x + z * z);
			double sign = Math.abs(yawParentRad - yawRad) > Math.PI / 2 ? -1 : 1;
			return (float) Math.atan2(y, r * sign);
		}
	}

	private ChassisRenderer() {
		quads = ModelBaker.DEBUG_QUADS.toArray(new ModelBaker.Quad[0]);
		legRenderers = new LegRenderer[0];
	}

	Pattern legName = Pattern.compile("^leg([0-9]+)$");

	public ChassisRenderer(BBModelTree tree, ResourceLocation id) {
		quads = ModelBaker.bake(tree, t -> !t.node.name().startsWith("leg")).toArray(ModelBaker.Quad[]::new);
		int count = (int) tree.children().stream().filter(c -> legName.matcher(c.node.name()).find()).count();
		legRenderers = new LegRenderer[count];
		for (var child : tree.children()) {
			Matcher m = legName.matcher(child.node.name());
			if (m.find()) {
				count++;
				int index = Integer.parseInt(m.group(1));
				legRenderers[index - 1] = new LegRenderer(child, index - 1);
			}
		}
	}

	public static void dispatch(MechaEntity mecha, PoseStack poseStack, MultiBufferSource bufferSource, int color, int packedLight, int packedOverlay) {
		// draw self, calls armor render
		if (!ArmisticeClientDebugValues.showChassis) return;
		var chassis = PartRenderer.chassis.getOrDefault(mecha.core().schematic().chassis().id(), MISSING);
		if (chassis != null) {
			poseStack.pushPose();
			mecha.core().chassisEuclidean().transformAbsolute(poseStack);
			chassis.render(poseStack.last(), mecha.core().skin(), bufferSource, color, packedLight, packedOverlay);
			poseStack.popPose();
			for (var leg : chassis.legRenderers) {
				leg.render(mecha, poseStack, bufferSource, color, packedLight, packedOverlay);
			}
		} else if (mecha.tickCount % 20 == 0) {
			Armistice.LOGGER.error("chassis model not found: {}", mecha.core().schematic().chassis().id());
		}
	}

	public void render(PoseStack.Pose pose, MechaSkin skin, MultiBufferSource bufferSource, int color, int packedLight, int packedOverlay) {
		PartRenderer.renderQuads(
			quads,
			ResourceLocation.fromNamespaceAndPath(skin.id().getNamespace(), "textures/chassis/" + skin.id().getPath() + ".png"),
			pose,
			bufferSource,
			color,
			packedLight,
			packedOverlay
		);
	}
}
