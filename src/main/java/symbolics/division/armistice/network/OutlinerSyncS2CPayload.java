package symbolics.division.armistice.network;

import com.google.common.collect.ImmutableMap;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;
import symbolics.division.armistice.Armistice;
import symbolics.division.armistice.client.render.model.HullModel;
import symbolics.division.armistice.model.*;

import java.util.List;
import java.util.Map;

public record OutlinerSyncS2CPayload(Map<ResourceLocation, List<OutlinerNode>> nodes) implements CustomPacketPayload {
	public static final Type<OutlinerSyncS2CPayload> TYPE = new Type<>(Armistice.id("outliner_sync"));
	public static final StreamCodec<ByteBuf, OutlinerSyncS2CPayload> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.map(
			Object2ObjectOpenHashMap::new,
			ResourceLocation.STREAM_CODEC,
			OutlinerNode.STREAM_CODEC.apply(ByteBufCodecs.list())
		),
		OutlinerSyncS2CPayload::nodes,
		OutlinerSyncS2CPayload::new
	);

	private static Map<ResourceLocation, BBModelTree> models;

	public static Map<ResourceLocation, BBModelTree> models() {
		return models;
	}

	@OnlyIn(Dist.CLIENT)
	public static void receive(OutlinerSyncS2CPayload payload, IPayloadContext context) {
		ImmutableMap.Builder<ResourceLocation, BBModelTree> builder = ImmutableMap.builder();

		payload.nodes().forEach((id, outliner) -> {
			List<Element> elements = ModelElementReloadListener.getModel(id);
			if (elements == null) throw new IllegalArgumentException("Unknown model %s".formatted(id));

			builder.put(id, new BBModelTree(new BBModelData(elements, outliner)));
		});

		models = builder.build();

		HullModel.compileModels();
	}

	@SubscribeEvent
	private static void onDatapackSync(OnDatapackSyncEvent event) {
		OutlinerSyncS2CPayload payload = new OutlinerSyncS2CPayload(ModelOutlinerReloadListener.getNodes());

		event.getRelevantPlayers().forEach(player -> PacketDistributor.sendToPlayer(player, payload));
	}

	@Override
	@NotNull
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
