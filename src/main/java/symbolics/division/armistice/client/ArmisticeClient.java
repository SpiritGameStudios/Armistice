package symbolics.division.armistice.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import symbolics.division.armistice.Armistice;
import symbolics.division.armistice.client.render.MechaEntityRenderer;
import symbolics.division.armistice.registry.ArmisticeEntityTypeRegistrar;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
@Mod(value = Armistice.MODID, dist = Dist.CLIENT)
public class ArmisticeClient {
	public static boolean renderVanillaHUD = true;

	@SubscribeEvent
	public static void handleRegisterEntityRenderEvent(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(ArmisticeEntityTypeRegistrar.MECHA, MechaEntityRenderer::new);
	}

	@SubscribeEvent
	public static void handleRegisterAdditionalModelsEvent(ModelEvent.RegisterAdditional event) {
//		TestModel.loadTestModel(event);
	}
}
