package symbolics.division.armistice.network.outliner;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;
import symbolics.division.armistice.Armistice;

public record OutlinerTaskFinishedC2SPayload() implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<OutlinerTaskFinishedC2SPayload> TYPE = new CustomPacketPayload.Type<>(Armistice.id("outliner_task_finished"));

	public static final StreamCodec<ByteBuf, OutlinerTaskFinishedC2SPayload> STREAM_CODEC = StreamCodec.unit(new OutlinerTaskFinishedC2SPayload());

	@NotNull
	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void receive(OutlinerTaskFinishedC2SPayload payload, IPayloadContext context) {
		context.finishCurrentTask(OutlinerSyncConfigurationTask.TYPE);
	}
}
