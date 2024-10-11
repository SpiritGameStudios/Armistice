package symbolics.division.armistice.mecha.schematic;

import com.mojang.serialization.Codec;
import org.apache.commons.lang3.NotImplementedException;
import symbolics.division.armistice.mecha.ArmorPart;
import symbolics.division.armistice.mecha.MechaCore;

import java.util.List;

/**
 * The MechaSchematic is functionally an immutable blueprint for a
 * mecha. It can be encoded as an item data component, or passed into
 * the constructor for a MechaEntity to provide the core for it.
 */
public record MechaSchematic(
	HullSchematic hull,
	List<OrdnanceSchematic> ordnance, // TODO: figure out how to handle empty slots
	ChassisSchematic chassis,
	ArmorSchematic armor
) implements Schematic<MechaSchematic, MechaCore> {
	@Override
	public MechaCore make() { throw new NotImplementedException(); }

	@Override
	public Codec<MechaSchematic> codec() { throw new NotImplementedException(); }
}
