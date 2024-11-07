package symbolics.division.armistice.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import org.joml.Quaternionf;
import symbolics.division.armistice.Armistice;
import symbolics.division.armistice.client.render.MechaEntityRenderer;
import symbolics.division.armistice.client.render.debug.ArmisticeClientDebugValues;
import symbolics.division.armistice.client.render.debug.MechaDebugRenderer;
import symbolics.division.armistice.client.render.hud.MechaHudRenderer;
import symbolics.division.armistice.client.render.hud.MechaOverlayRenderer;
import symbolics.division.armistice.client.render.model.OrdnanceRenderer;
import symbolics.division.armistice.mecha.ordnance.HitscanGunOrdnance;
import symbolics.division.armistice.model.MechaModelData;
import symbolics.division.armistice.network.outliner.OutlinerSyncS2CPayload;
import symbolics.division.armistice.registry.ArmisticeEntityTypeRegistrar;

import java.util.OptionalDouble;

@EventBusSubscriber(value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
@Mod(value = Armistice.MODID, dist = Dist.CLIENT)
public class ArmisticeClient {
	public static boolean renderVanillaHUD = true;

	public ArmisticeClient(IEventBus modEventBus, ModContainer modContainer) {
		modEventBus.register(MechaHudRenderer.class);
		modEventBus.register(MechaOverlayRenderer.class);

		NeoForge.EVENT_BUS.register(ArmisticeClientDebugValues.class);
		NeoForge.EVENT_BUS.register(MechaDebugRenderer.class);
		initOrdnanceRenderers();
	}


	@SubscribeEvent
	public static void handleRegisterEntityRenderEvent(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(ArmisticeEntityTypeRegistrar.MECHA, MechaEntityRenderer::new);
		event.registerEntityRenderer(ArmisticeEntityTypeRegistrar.ARTILLERY_SHELL, NoopRenderer::new);
		event.registerEntityRenderer(ArmisticeEntityTypeRegistrar.MISSILE, NoopRenderer::new);
	}

	@SubscribeEvent
	private static void onRegisterPayloadHandlers(RegisterPayloadHandlersEvent event) {
		OutlinerSyncS2CPayload.initHandler();
	}

	public static final RenderType LASER = RenderType.create(
		"laser",
		DefaultVertexFormat.POSITION_COLOR,
		VertexFormat.Mode.LINE_STRIP,
		1536,
		RenderType.CompositeState.builder()
			.setShaderState(RenderType.RENDERTYPE_LINES_SHADER)
			.setLineState(new RenderStateShard.LineStateShard(OptionalDouble.of(1)))
			.setCullState(RenderType.NO_CULL)
			.createCompositeState(false)
	);

	private static void initOrdnanceRenderers() {
		OrdnanceRenderer.addRenderer(
			Armistice.id("laser"),
			(mecha, ordnance, tickDelta, pose, bufferSource, color, packedLight, packedOverlay) -> {
				if (!(ordnance instanceof HitscanGunOrdnance hitscan)) return;

				MechaModelData.OrdnanceInfo info = mecha.core().model().ordnanceInfo(ordnance, mecha.core());

				var barrelLength = hitscan.barrelMarker().origin().with(Direction.Axis.Y, 0).length();
				var baseRotation = info.mountPoint().rotationInfo().bbRotation().scale(Mth.DEG_TO_RAD);

				Vec3 evilBodyOffsetPleaseUpdateModelData = info.body().origin();

				Vec3 absBody = new Vec3(ordnance.rel2Abs(
					new Quaternionf().rotateZYX(
						(float) baseRotation.z, (float) baseRotation.y, (float) baseRotation.x
					).transform(evilBodyOffsetPleaseUpdateModelData.toVector3f())
				));

				Vec3 currentDirection = ordnance.currentDirection();

				VertexConsumer vc = bufferSource.getBuffer(LASER);

				vc.addVertex(pose.last(), absBody.add(currentDirection.scale(barrelLength)).toVector3f()).setColor(1, 1f, 1f, 1f);

				vc.addVertex(pose.last(), absBody.add(currentDirection.scale(barrelLength + hitscan.maxDistance())).toVector3f()).setColor(1, 1f, 1f, 1f);
			}
		);
	}
}
