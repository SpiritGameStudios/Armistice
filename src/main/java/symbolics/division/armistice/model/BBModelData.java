package symbolics.division.armistice.model;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;
import java.util.UUID;

public record BBModelData(
	List<Element> elements,
	List<OutlinerNode> outliner
) {
	public static final Codec<BBModelData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Element.CODEC.listOf().fieldOf("elements").forGetter(BBModelData::elements),
		OutlinerNode.CODEC.listOf().lenientOptionalFieldOf("outliner", List.of()).forGetter(BBModelData::outliner)
	).apply(instance, BBModelData::new));

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
