package symbolics.division.armistice.client;

import net.minecraft.client.renderer.entity.NoopRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import symbolics.division.armistice.Armistice;
import symbolics.division.armistice.client.render.MechaEntityRenderer;
import symbolics.division.armistice.client.render.debug.ArmisticeClientDebugValues;
import symbolics.division.armistice.client.render.debug.MechaDebugRenderer;
import symbolics.division.armistice.client.render.hud.MechaHudRenderer;
import symbolics.division.armistice.client.render.hud.MechaOverlayRenderer;
import symbolics.division.armistice.client.render.ordnance.HitscanBulletRenderer;
import symbolics.division.armistice.network.outliner.OutlinerSyncS2CPayload;
import symbolics.division.armistice.registry.ArmisticeEntityTypeRegistrar;

@EventBusSubscriber(value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
@Mod(value = Armistice.MODID, dist = Dist.CLIENT)
public class ArmisticeClient {
	public static boolean renderVanillaHUD = true;

	public ArmisticeClient(IEventBus modEventBus, ModContainer modContainer) {
		modEventBus.register(MechaHudRenderer.class);
		modEventBus.register(MechaOverlayRenderer.class);

		NeoForge.EVENT_BUS.register(ArmisticeClientDebugValues.class);
		NeoForge.EVENT_BUS.register(MechaDebugRenderer.class);
	}


	@SubscribeEvent
	public static void handleRegisterEntityRenderEvent(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(ArmisticeEntityTypeRegistrar.MECHA, MechaEntityRenderer::new);
		event.registerEntityRenderer(ArmisticeEntityTypeRegistrar.ARTILLERY_SHELL, NoopRenderer::new);
		event.registerEntityRenderer(ArmisticeEntityTypeRegistrar.MISSILE, NoopRenderer::new);
		event.registerEntityRenderer(ArmisticeEntityTypeRegistrar.HITSCAN_BULLET, HitscanBulletRenderer::new);
	}

	@SubscribeEvent
	private static void onRegisterPayloadHandlers(RegisterPayloadHandlersEvent event) {
		OutlinerSyncS2CPayload.initHandler();
	}
}
