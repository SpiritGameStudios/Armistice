package symbolics.division.armistice.mecha;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import symbolics.division.armistice.Armistice;

public record MechaSkin(ResourceLocation id) {
	public static final Codec<MechaSkin> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		ResourceLocation.CODEC.fieldOf("id").forGetter(MechaSkin::id)
	).apply(instance, MechaSkin::new));

	public static final MechaSkin DEFAULT = new MechaSkin(Armistice.id("default"));

	public static final StreamCodec<ByteBuf, MechaSkin> STREAM_CODEC = StreamCodec.composite(
		ResourceLocation.STREAM_CODEC,
		MechaSkin::id,
		MechaSkin::new
	);
}
