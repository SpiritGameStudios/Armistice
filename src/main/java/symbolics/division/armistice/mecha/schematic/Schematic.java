package symbolics.division.armistice.mecha.schematic;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

/**
 * A schematic for an object that can be constructed.
 * The schematic can also be encoded and decoded.
 */
public interface Schematic<S extends Schematic<S, P>, P> {
	P make();

	Codec<S> registryCodec(RegistryAccess access);

	/**
	 * Until ModFest is over, just let this use the default.
	 * It's inefficient, but it's easy to refactor later.
	 */
	default StreamCodec<ByteBuf, S> streamCodec(RegistryAccess access) {
		return ByteBufCodecs.fromCodec(registryCodec(access));
	}
}
