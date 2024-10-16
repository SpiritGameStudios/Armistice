package symbolics.division.armistice.mecha;

public interface Part {
	// maybe codec, ill leave this in for now
//    public Codec<T> codec() { throw new NotImplementedException(); }

	/**
	 * Guaranteed to not be called unless an entity is loaded in the world
	 * and associated with `core`.
	 * Called on both client and server inside their respective tick methods.
	 *
	 * @param core
	 */
	default void tick(MechaCore core) {
	}


	default void serverTick(MechaCore core) {
		tick(core);
	}

	default void clientTick(MechaCore core, float tickDelta) {
		tick(core);
	}
}
