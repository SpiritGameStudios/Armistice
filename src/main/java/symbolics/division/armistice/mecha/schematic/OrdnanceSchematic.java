package symbolics.division.armistice.mecha.schematic;

import com.mojang.serialization.Codec;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import symbolics.division.armistice.mecha.OrdnancePart;
import symbolics.division.armistice.registry.ArmisticeRegistries;

import java.util.function.Supplier;

public record OrdnanceSchematic(
	int size,
	Supplier<OrdnancePart> supplier
) implements Schematic<OrdnanceSchematic, OrdnancePart> {
	public static final Codec<OrdnanceSchematic> REGISTRY_CODEC = ArmisticeRegistries.ORDNANCE.byNameCodec();

	@Override
	public OrdnancePart make() {
		OrdnancePart part = supplier.get();
		part.setId(id());

		return part;
	}

	@Override
	public Codec<OrdnanceSchematic> registryCodec(RegistryAccess access) {
		return ArmisticeRegistries.ORDNANCE.byNameCodec();
	}

	public ResourceLocation id() {
		return ArmisticeRegistries.ORDNANCE.getKey(this);
	}
}
