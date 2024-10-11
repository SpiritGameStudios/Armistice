package symbolics.division.armistice.mecha.schematic;

import com.mojang.serialization.Codec;
import symbolics.division.armistice.mecha.OrdnancePart;

public record OrdnanceSchematic(
	int size
) implements Schematic<OrdnanceSchematic, OrdnancePart> {
	@Override
	public OrdnancePart make() {
		return null;
	}

	@Override
	public Codec<OrdnanceSchematic> codec() {
		return null;
	}
}
