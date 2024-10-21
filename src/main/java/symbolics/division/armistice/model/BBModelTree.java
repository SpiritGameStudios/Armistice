package symbolics.division.armistice.model;

import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

// bbmodel data transformed in tree representation
@OnlyIn(Dist.CLIENT)
public class BBModelTree {
	public final OutlinerNode node;
	protected final Map<String, Element> elements = new Object2ObjectOpenHashMap<>();
	protected final Map<String, BBModelTree> children = new Object2ObjectOpenHashMap<>();

	public BBModelTree(BBModelData source) {
		this(source, "root");
	}

	public BBModelTree(BBModelData source, String root) {
		this(source, source.nodeByName(root));
	}

	public BBModelTree(BBModelData source, OutlinerNode node) {
		this.node = node;
		for (Either<OutlinerNode, String> child : node.children()) {
			child.ifLeft(n -> children.put(n.name(), new BBModelTree(source, n)))
				.ifRight(e -> elements.put(e, source.elementByUuid(e)));
		}
	}

	public Collection<Element> elements() {
		return elements.values();
	}

	public Element elements(String uuid) {
		return elements.get(uuid);
	}

	public Collection<BBModelTree> children() {
		return children.values();
	}

	public BBModelTree child(String name) {
		return children.get(name);
	}

	public <T> Stream<T> walk(Function<OutlinerNode, T> func) {
		return Stream.concat(
			Stream.of(func.apply(node)),
			children.values().stream().flatMap(c -> c.walk(func))
		);
	}
}
