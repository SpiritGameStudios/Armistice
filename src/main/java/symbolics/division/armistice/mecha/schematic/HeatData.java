package symbolics.division.armistice.mecha.schematic;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record HeatData(
	int max,
	int delay,
	int decay
) {
	public static final Codec<HeatData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.INT.fieldOf("max").forGetter(HeatData::max),
		Codec.INT.fieldOf("delay").forGetter(HeatData::delay),
		Codec.INT.fieldOf("decay").forGetter(HeatData::decay)
	).apply(instance, HeatData::new));

	public static final StreamCodec<ByteBuf, HeatData> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.VAR_INT,
		HeatData::max,
		ByteBufCodecs.VAR_INT,
		HeatData::delay,
		ByteBufCodecs.VAR_INT,
		HeatData::decay,
		HeatData::new
	);
}
