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
public class MechaSchematic {
    public MechaCore make() { throw new NotImplementedException(); }
    public Codec<MechaSchematic> codec() { throw new NotImplementedException(); }

	public HullSchematic hull() { throw new NotImplementedException(); }
	// you can figure out the best way to set up ordnance w/ empty slots
	public List<OrdnanceSchematic> ordnance() { throw new NotImplementedException(); }
	public ChassisSchematic chassis() { throw new NotImplementedException(); }
	public ArmorPart armor() { throw new NotImplementedException(); }

	@Override
	public boolean equals(Object obj) {
		throw new NotImplementedException();
	}
}
