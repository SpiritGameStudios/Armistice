package symbolics.division.armistice.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import symbolics.division.armistice.mecha.MechaEntity;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class ClientGameEventsListener {
	@SubscribeEvent
	public static void prePlayerRender(net.neoforged.neoforge.client.event.RenderPlayerEvent.Pre event) {
		if (event.getEntity().getVehicle() instanceof MechaEntity) event.setCanceled(true);
	}
}
