package symbolics.division.armistice.model;

import com.google.common.base.Splitter;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2ReferenceLinkedOpenHashMap;

import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public record BBModelData(
	List<Element> elements,
	List<OutlinerNode> outliner
) {
	public static final float BASE_SCALE_FACTOR = 1f / 16;
	private static final Pattern NPV = Pattern.compile("(.+):(.+)=([0-9]+.?[0-9]*)");
	public static final Codec<Map<String, Map<String, Double>>> OUTLINER_PROPERTIES = Codec.STRING.comapFlatMap(
		string -> {
			Map<String, Map<String, Double>> result = new Object2ReferenceLinkedOpenHashMap<>();
			Splitter.on('\n').trimResults().splitToList(string).forEach(line -> {
				Matcher match = NPV.matcher(line);
				if (match.groupCount() < 3) throw new IllegalArgumentException("invalid property format: " + line);
				Map<String, Double> entry = result.get(match.group(1));
				if (entry == null)
					entry = result.put(match.group(1), new Object2ReferenceLinkedOpenHashMap<>());
				Objects.requireNonNull(entry);

				entry.put(match.group(2), Double.valueOf(match.group(3)));
			});
			return DataResult.success(result);
		},
		properties -> properties.entrySet().parallelStream()
			.flatMap(node -> node.getValue().entrySet().parallelStream().map(entry ->
				node.getKey() + ":" + entry.getKey() + "=" + entry.getValue()
			)).collect(Collectors.joining("\n"))
	);
	public static final MapCodec<List<OutlinerNode>> PARAMETERIZED_NODES = OutlinerNode.CODEC.listOf().fieldOf("outliner").dependent(
		OUTLINER_PROPERTIES.fieldOf("variable_placeholders"),
		nodes -> Pair.of(
			nodes.stream().collect(Collectors.toMap(OutlinerNode::name, OutlinerNode::parameters)),
			OUTLINER_PROPERTIES.optionalFieldOf("properties", Map.of())
		),
		(nodes, parameters) -> {
			List<OutlinerNode> flattened = new ArrayList<>();
			nodes.forEach(node -> {
				walk(flattened::add, node);
			});

			flattened.forEach(node -> {
				Map<String, Double> params = parameters.get(node.name());
				if (params != null) node.parameters().putAll(params);
			});

			return nodes;
		}
	);
	public static final Codec<BBModelData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Element.CODEC.listOf().fieldOf("elements").forGetter(BBModelData::elements),
		PARAMETERIZED_NODES.forGetter(BBModelData::outliner)
	).apply(instance, BBModelData::new));

	static void walk(Consumer<OutlinerNode> nodeConsumer, OutlinerNode node) {
		nodeConsumer.accept(node);
		node.children().forEach(child -> child.left().ifPresent(childNode -> walk(nodeConsumer, childNode)));
	}

	public Element elementByUuid(String uuid) {
		return elements.stream().filter(e -> e.uuid().equals(UUID.fromString(uuid))).findAny().orElseThrow(BBModelConstructionError::new);
	}

	public OutlinerNode nodeByUuid(String uuid) {
		return outliner.stream().filter(n -> n.uuid().equals(UUID.fromString(uuid))).findAny().orElseThrow(BBModelConstructionError::new);
	}

	public OutlinerNode nodeByName(String name) {
		return outliner.stream().filter(n -> n.name().equals(name)).findAny().orElseThrow(BBModelConstructionError::new);
	}

	public static class BBModelConstructionError extends RuntimeException {
	}
}
