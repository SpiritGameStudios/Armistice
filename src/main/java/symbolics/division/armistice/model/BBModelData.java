package symbolics.division.armistice.model;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;

public record BBModelData(
	List<Element> elements,
	List<OutlinerNode> outliner
) {
	public static final Codec<BBModelData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Element.CODEC.listOf().fieldOf("elements").forGetter(BBModelData::elements),
		OutlinerNode.CODEC.listOf().lenientOptionalFieldOf("outliner", List.of()).forGetter(BBModelData::outliner)
	).apply(instance, BBModelData::new));
}
