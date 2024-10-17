package symbolics.division.armistice.mecha.schematic;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;
import symbolics.division.armistice.mecha.OrdnancePart;
import symbolics.division.armistice.registry.ArmisticeRegistries;

public record OrdnanceSchematic(
	int size
) implements Schematic<OrdnanceSchematic, OrdnancePart> {
	public static final Codec<OrdnanceSchematic> REGISTRY_CODEC = ArmisticeRegistries.ORDNANCE.byNameCodec();

	@Override
	public OrdnancePart make() {
		return new OrdnancePart(this);
	}

	@Override
	public Codec<OrdnanceSchematic> codec() {
		return ArmisticeRegistries.ORDNANCE.byNameCodec();
	}

	public ResourceLocation id() {
		return ArmisticeRegistries.ORDNANCE.getKey(this);
	}
}
