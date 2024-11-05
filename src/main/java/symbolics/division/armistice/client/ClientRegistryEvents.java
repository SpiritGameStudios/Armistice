package symbolics.division.armistice.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import symbolics.division.armistice.network.ExtendedParticlePacket;
import symbolics.division.armistice.network.OutlinerSyncS2CPayload;

@OnlyIn(Dist.CLIENT)
public class ClientRegistryEvents {
	@SubscribeEvent
	private static void onRegisterClientPayloadHandlers(RegisterPayloadHandlersEvent event) {
		PayloadRegistrar registrar = event.registrar("1");
		registrar.playToClient(
			OutlinerSyncS2CPayload.TYPE,
			OutlinerSyncS2CPayload.STREAM_CODEC,
			OutlinerSyncS2CPayload::receive
		);

		registrar.playToClient(
			ExtendedParticlePacket.TYPE,
			ExtendedParticlePacket.STREAM_CODEC,
			ExtendedParticlePacket::receive
		);
	}
}
