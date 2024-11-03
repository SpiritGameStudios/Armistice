package symbolics.division.armistice.mecha.schematic;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import symbolics.division.armistice.mecha.MechaCore;
import symbolics.division.armistice.mecha.MechaSkin;

import java.util.ArrayList;
import java.util.List;

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
	public static final Codec<MechaSchematic> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		HullSchematic.REGISTRY_CODEC.fieldOf("hull").forGetter(MechaSchematic::hull),
		OrdnanceSchematic.REGISTRY_CODEC.listOf().fieldOf("ordnance").forGetter(MechaSchematic::ordnance),
		ChassisSchematic.REGISTRY_CODEC.fieldOf("chassis").forGetter(MechaSchematic::chassis),
		ArmorSchematic.REGISTRY_CODEC.fieldOf("armor").forGetter(MechaSchematic::armor)
	).apply(instance, MechaSchematic::new));

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
	public Codec<MechaSchematic> codec() {
		return CODEC;
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
