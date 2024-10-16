package symbolics.division.armistice.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import symbolics.division.armistice.Armistice;
import symbolics.division.armistice.client.render.MechaEntityRenderer;
import symbolics.division.armistice.registry.ArmisticeEntityTypeRegistrar;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
@Mod(value = Armistice.MODID, dist = Dist.CLIENT)
public class ArmisticeClient {
	@SubscribeEvent
	public static void handleRegisterEntityRenderEvent(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(ArmisticeEntityTypeRegistrar.MECHA, MechaEntityRenderer::new);
	}
}