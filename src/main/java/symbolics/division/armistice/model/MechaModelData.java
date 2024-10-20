package symbolics.division.armistice.model;

import symbolics.division.armistice.Armistice;

import java.util.function.Supplier;

public class MechaModelData {

	public static boolean of(BBModelData bbModel) {
		Armistice.LOGGER.info("attempting model load");
		try {
			var q = new BBModelTree(bbModel, "root");
			Armistice.LOGGER.info("model load success");
			return true;
		} catch (BBModelData.BBModelConstructionError e) {
			Armistice.LOGGER.error("Failed to construct model", e);
		}
		return false;
	}

	public static <T, E extends RuntimeException> T notNull(T v, Supplier<E> exceptionSupplier) {
		if (v != null) return v;
		throw exceptionSupplier.get();
	}
}
