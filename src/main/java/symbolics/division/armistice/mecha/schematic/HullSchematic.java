package symbolics.division.armistice.mecha.schematic;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Range;
import symbolics.division.armistice.mecha.HullPart;
import symbolics.division.armistice.registry.ArmisticeRegistries;

import java.util.List;

public record HullSchematic(
	@Range(from = 1, to = 3) int tier,
	List<Integer> slots,
	HeatData heat,
	ResourceLocation id
) implements Schematic<HullSchematic, HullPart> {
	public static final Codec<HullSchematic> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.INT.fieldOf("tier").forGetter(HullSchematic::tier),
		Codec.INT.listOf().fieldOf("slots").forGetter(HullSchematic::slots),
		HeatData.CODEC.fieldOf("heat").forGetter(HullSchematic::heat),
		ResourceLocation.CODEC.fieldOf("id").forGetter(HullSchematic::id)
	).apply(instance, HullSchematic::new));

	@Override
	public HullPart make() {
		return new HullPart(this);
	}

	@Override
	public Codec<HullSchematic> registryCodec(RegistryAccess access) {
		return getCodec(access);
	}

	public static Codec<HullSchematic> getCodec(RegistryAccess access) {
		return access.registryOrThrow(ArmisticeRegistries.HULL_KEY).byNameCodec();
	}
}
