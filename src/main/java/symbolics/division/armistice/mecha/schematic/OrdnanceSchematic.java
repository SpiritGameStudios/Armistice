package symbolics.division.armistice.mecha.schematic;

import com.mojang.serialization.Codec;
import symbolics.division.armistice.mecha.OrdnancePart;
import symbolics.division.armistice.registry.ArmisticeRegistries;

public record OrdnanceSchematic(
	int size
) implements Schematic<OrdnanceSchematic, OrdnancePart> {
	public static final Codec<OrdnanceSchematic> REGISTRY_CODEC = ArmisticeRegistries.ORDNANCE.byNameCodec();
	
	@Override
	public OrdnancePart make() {
		return null;
	}

	@Override
	public Codec<OrdnanceSchematic> codec() {
		return ArmisticeRegistries.ORDNANCE.byNameCodec();
	}
}
