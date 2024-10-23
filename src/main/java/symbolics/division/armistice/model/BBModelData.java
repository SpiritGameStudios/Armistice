package symbolics.division.armistice.model;

import com.google.common.base.Splitter;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2ReferenceLinkedOpenHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public record BBModelData(
	List<Element> elements,
	List<OutlinerNode> outliner
) {

	private static final Pattern p = Pattern.compile("(.+):(.+)=([0-9]+.?[0-9]*)");
	// node, property, value
	public static final Codec<Map<String, Map<String, Double>>> OUTLINER_PROPERTIES = Codec.STRING.comapFlatMap(
		(String s) -> {
			try {
				Map<String, Map<String, Double>> result = new Object2ReferenceLinkedOpenHashMap<>();
				Splitter.on('\n').trimResults().splitToList(s).forEach(
					line -> {
						var match = p.matcher(line);
						if (match.groupCount() < 3) throw new IllegalArgumentException("invalid property format: " + line);
						var entry = result.get(match.group(1));
						if (entry == null) entry = result.put(match.group(1), new Object2ReferenceLinkedOpenHashMap<>());
						entry.put(match.group(2), Double.valueOf(match.group(3)));
					}
				);
				return DataResult.success(result);
			} catch (IllegalArgumentException e) {
				return DataResult.error(() -> "Failed to parse variables into attribute tuples: " + s);
			}
		},
		props -> props.entrySet().parallelStream()
			.flatMap(node -> node.getValue().entrySet().parallelStream()
				.map(entry -> node.getKey()+":"+entry.getKey()+"="+entry.getValue()))
			.collect(Collectors.joining("\n"))
	);


	static MapCodec<List<OutlinerNode>> PARAMETERIZED_NODES = OutlinerNode.CODEC.listOf().fieldOf("outliner").dependent(
		OUTLINER_PROPERTIES.fieldOf("variable_placeholders"),
		nodes -> Pair.of(
			nodes.stream().collect(Collectors.toMap(OutlinerNode::name, OutlinerNode::parameters)),
			OUTLINER_PROPERTIES.fieldOf("properties")
		),
		(outliners, parameters) -> {
			for (OutlinerNode node : outliners) {
				var params = parameters.get(node.name());
				if (params != null) {
					node.parameters().putAll(params);
				}
			}
			return outliners;
		}
	);

	public static final Codec<BBModelData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Element.CODEC.listOf().fieldOf("elements").forGetter(BBModelData::elements),
		PARAMETERIZED_NODES.forGetter(BBModelData::outliner)
	).apply(instance, BBModelData::new));

//	Codec.mapPair(Codec.LONG.fieldOf("most_sig_bits"), Codec.LONG.fieldOf("least_sig_bits")).xmap(
//		pair -> new UUID(pair.getFirst(), pair.getSecond()),
//		uuid -> new Pair<>(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits())
//	);

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
