package symbolics.division.armistice.model;

import com.mojang.datafixers.util.Either;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

// bbmodel data transformed in tree representation
public class BBModelTree {
	public final OutlinerNode node;
	public final Map<String, Element> elements = new HashMap<>();
	public final Map<String, BBModelTree> bones = new HashMap<>();

	public BBModelTree(BBModelData source, String root) {
		this(source, source.nodeByName(root));
	}

	public BBModelTree(BBModelData source, OutlinerNode node) {
		this.node = node;
		for (Either<OutlinerNode, String> child : node.children()) {
			child.ifLeft(n -> bones.put(n.name(), new BBModelTree(source, n)))
				.ifRight(e -> elements.put(e, source.elementByUuid(e)));
		}
	}

	public Collection<Element> children() {
		return elements.values();
	}

	public Element child(String uuid) {
		return MechaModelData.notNull(elements.get(uuid), BBModelData.BBModelConstructionError::new);
	}

	public Collection<BBModelTree> bones() {
		return bones.values();
	}

	public BBModelTree bone(String name) {
		return MechaModelData.notNull(bones.get(name), BBModelData.BBModelConstructionError::new);
	}
}
