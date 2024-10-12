package symbolics.division.armistice.mecha.schematic;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Range;
import symbolics.division.armistice.mecha.ArmorPart;
import symbolics.division.armistice.registry.ArmisticeRegistries;

public record ArmorSchematic(
	@Range(from = 1, to = 9) int size,
	int platingAmount
) implements Schematic<ArmorSchematic, ArmorPart> {
	public static final Codec<ArmorSchematic> REGISTRY_CODEC = ArmisticeRegistries.ARMOR.byNameCodec();

	@Override
	public ArmorPart make() {
		return null;
	}

	@Override
	public Codec<ArmorSchematic> codec() {
		return ArmisticeRegistries.ARMOR.byNameCodec();
	}

	public ResourceLocation id() {
		return ArmisticeRegistries.ARMOR.getKey(this);
	}
}
