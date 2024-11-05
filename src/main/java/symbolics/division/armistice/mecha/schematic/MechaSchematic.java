package symbolics.division.armistice.mecha.schematic;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import symbolics.division.armistice.mecha.MechaCore;
import symbolics.division.armistice.mecha.MechaSkin;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * The MechaSchematic is functionally an immutable blueprint for a
 * mecha. It can be encoded as an item data component, or passed into
 * the constructor for a MechaEntity to provide the core for it.
 */
public record MechaSchematic(
	HullSchematic hull,
	List<OrdnanceSchematic> ordnance,
	ChassisSchematic chassis,
	ArmorSchematic armor
) implements Schematic<MechaSchematic, MechaCore> {
	public static final Codec<MechaSchematic> CODEC = com.mojang.serialization.codecs.RecordCodecBuilder.create(instance -> instance.group(
		HullSchematic.CODEC.fieldOf("hull").forGetter(MechaSchematic::hull),
		OrdnanceSchematic.REGISTRY_CODEC.listOf().fieldOf("ordnance").forGetter(MechaSchematic::ordnance),
		ChassisSchematic.CODEC.fieldOf("chassis").forGetter(MechaSchematic::chassis),
		ArmorSchematic.CODEC.fieldOf("armor").forGetter(MechaSchematic::armor)
	).apply(instance, MechaSchematic::new));

	public static final Function<RegistryAccess, Codec<MechaSchematic>> REGISTRY_CODEC = access ->
		RecordCodecBuilder.create(instance -> instance.group(
			HullSchematic.REGISTRY_CODEC.apply(access).fieldOf("hull").forGetter(MechaSchematic::hull),
			OrdnanceSchematic.REGISTRY_CODEC.listOf().fieldOf("ordnance").forGetter(MechaSchematic::ordnance),
			ChassisSchematic.REGISTRY_CODEC.apply(access).fieldOf("chassis").forGetter(MechaSchematic::chassis),
			ArmorSchematic.REGISTRY_CODEC.apply(access).fieldOf("armor").forGetter(MechaSchematic::armor)
		).apply(instance, MechaSchematic::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, MechaSchematic> STREAM_CODEC = StreamCodec.composite(
		HullSchematic.STREAM_CODEC,
		MechaSchematic::hull,
		OrdnanceSchematic.REGISTRY_STREAM_CODEC.apply(ByteBufCodecs.list()),
		MechaSchematic::ordnance,
		ChassisSchematic.STREAM_CODEC,
		MechaSchematic::chassis,
		ArmorSchematic.STREAM_CODEC,
		MechaSchematic::armor,
		MechaSchematic::new
	);

	@Override
	public MechaCore make() {
		if (!verify())
			throw new IllegalStateException("Mecha schematic is invalid");

		return new MechaCore(this, null);
	}

	public MechaCore make(MechaSkin skin) {
		if (!verify())
			throw new IllegalStateException("Mecha schematic is invalid");

		return new MechaCore(this, skin);
	}

	@Override
	public Codec<MechaSchematic> registryCodec(RegistryAccess access) {
		return REGISTRY_CODEC.apply(access);
	}

	@Override
	public StreamCodec<? extends ByteBuf, MechaSchematic> streamCodec() {
		return STREAM_CODEC;
	}

	public boolean verify() {
		if (hull().tier() != chassis().tier())
			return false;

		if (hull().slots().size() < ordnance().size())
			return false;

		if (armor().size() > chassis().maxArmorLevel() || armor().size() < chassis().minArmorLevel())
			return false;

		List<Integer> hullSlots = new ArrayList<>(hull().slots());
		List<Integer> ordnanceSizes = ordnance().stream().map(OrdnanceSchematic::size).toList();
		for (Integer size : ordnanceSizes) {
			if (!hullSlots.contains(size)) return false;
			hullSlots.remove(size);
		}

		return true;
	}
}
