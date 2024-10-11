package symbolics.division.armistice.mecha.schematic;

import com.mojang.serialization.Codec;
import org.apache.commons.lang3.NotImplementedException;
import symbolics.division.armistice.mecha.MechaPart;

public abstract class PartSchematic<P extends MechaPart, S extends PartSchematic<P, S>> {
    public /* abstract */ P make() { throw new NotImplementedException(); }
    public /* abstract */ Codec<S> codec() { throw new NotImplementedException(); }
	@Override
	public boolean equals(Object obj) {
		throw new NotImplementedException();
	}
}
