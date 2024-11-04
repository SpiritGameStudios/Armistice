package symbolics.division.armistice.mecha.schematic;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Range;
import symbolics.division.armistice.mecha.ArmorPart;
import symbolics.division.armistice.registry.ArmisticeRegistries;

public record ArmorSchematic(
	@Range(from = 1, to = 9) int size,
	double plating,
	ResourceLocation id
) implements Schematic<ArmorSchematic, ArmorPart> {
	public static final Codec<ArmorSchematic> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.INT.fieldOf("size").forGetter(ArmorSchematic::size),
		Codec.DOUBLE.fieldOf("plating").forGetter(ArmorSchematic::plating),
		ResourceLocation.CODEC.fieldOf("id").forGetter(ArmorSchematic::id)
	).apply(instance, ArmorSchematic::new));

	@Override
	public ArmorPart make() {
		return new ArmorPart(this);
	}

	@Override
	public Codec<ArmorSchematic> registryCodec(RegistryAccess access) {
		return access.registryOrThrow(ArmisticeRegistries.ARMOR_KEY).byNameCodec();
	}

	public static Codec<ArmorSchematic> getCodec(RegistryAccess access) {
		return access.registryOrThrow(ArmisticeRegistries.ARMOR_KEY).byNameCodec();
	}
}
