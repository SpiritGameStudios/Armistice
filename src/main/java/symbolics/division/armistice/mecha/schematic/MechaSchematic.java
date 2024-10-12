package symbolics.division.armistice.mecha.schematic;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.apache.commons.lang3.NotImplementedException;
import symbolics.division.armistice.mecha.MechaCore;

import java.util.List;

/**
 * The MechaSchematic is functionally an immutable blueprint for a
 * mecha. It can be encoded as an item data component, or passed into
 * the constructor for a MechaEntity to provide the core for it.
 */
public record MechaSchematic(
	HullSchematic hull,
	List<OrdnanceSchematic> ordnance, // TODO: figure out how to handle empty slots, Potentially use a DefaultedList?
	ChassisSchematic chassis,
	ArmorSchematic armor
) implements Schematic<MechaSchematic, MechaCore> {
	public static final Codec<MechaSchematic> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		HullSchematic.REGISTRY_CODEC.fieldOf("hull").forGetter(MechaSchematic::hull),
		OrdnanceSchematic.REGISTRY_CODEC.listOf().fieldOf("ordnance").forGetter(MechaSchematic::ordnance),
		ChassisSchematic.REGISTRY_CODEC.fieldOf("chassis").forGetter(MechaSchematic::chassis),
		ArmorSchematic.REGISTRY_CODEC.fieldOf("armor").forGetter(MechaSchematic::armor)
	).apply(instance, MechaSchematic::new));

	public MechaSchematic {
		if (hull().tier() != chassis().tier())
			throw new IllegalArgumentException("Hull and chassis must be of the same tier");

		ordnance().stream()
			.map(OrdnanceSchematic::size)
			.forEach(size -> {
				if (!hull().slots().contains(size))
					throw new IllegalArgumentException("Ordnance size " + size + " is not supported by the chassis");
			});
	}

	@Override
	public MechaCore make() {
		throw new NotImplementedException();
	}

	@Override
	public Codec<MechaSchematic> codec() {
		return CODEC;
	}
}
