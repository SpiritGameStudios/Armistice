package symbolics.division.armistice.mecha.schematic;

import com.mojang.serialization.Codec;

/**
 * A schematic for an object that can be constructed.
 * The schematic can also be encoded and decoded.
 */
public interface Schematic<S extends Schematic<S, P>, P> {
	P make();
	Codec<S> codec();
	// Would a PacketCodec/StreamCodec also be useful here?
}
