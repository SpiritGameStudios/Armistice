package symbolics.division.armistice.model;

import com.mojang.datafixers.util.Either;
import symbolics.division.armistice.Armistice;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ModelPartData {
	/*
	 treat core as root.
	 each part has a
	 - name
	 - root [v3]
	 - list of segments, each with
	 	- offset from root
	 	- direction [v3]
	 	- maxAngle
	 	- minAngle

	 	// models are rotated as zyx.
	 */

	// bbmodel data transformed in tree representation
	public static class BBModelTree {
		public final OutlinerNode node;
		private final BBModelData source;
		public final Map<String, Element> elements = new HashMap<>();
		public final Map<String, BBModelTree> bones = new HashMap<>();

		public BBModelTree(BBModelData source, String root) {
			this(source, source.nodeByName(root));
		}

		public BBModelTree(BBModelData source, OutlinerNode node) {
			this.node = node;
			this.source = source;
			for (Either<OutlinerNode, String> child : node.children()) {
				child.ifLeft(n -> bones.put(n.name(), new BBModelTree(source, n)))
					.ifRight(e -> elements.put(e, source.elementByUuid(e)));
			}
		}

		public Element children(String uuid) {
			return notNull(elements.get(uuid), BBModelData.BBModelConstructionError::new);
		}

		public Collection<BBModelTree> bones() {
			return bones.values();
		}

		public BBModelTree bone(String name) {
			return notNull(bones.get(name), BBModelData.BBModelConstructionError::new);
		}
	}

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
