package symbolics.division.armistice.model;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.objects.Object2ReferenceLinkedOpenHashMap;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;
import symbolics.division.armistice.serialization.ExtraCodecs;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public record OutlinerNode(
	String name,
	UUID uuid,
	Vec3 origin,
	Vec3 rotation,
	boolean export,
	boolean mirrorUv,
	boolean visibility,
	int autoUv,
	List<Either<OutlinerNode, String>> children,
	Map<String, Double> parameters
) {
	public static final float BASE_SCALE_FACTOR = 1f / 4;

	public static final Codec<OutlinerNode> CODEC =
		Codec.recursive(
			OutlinerNode.class.getSimpleName(),
			outlineCodec -> RecordCodecBuilder.create(instance -> instance.group(
				Codec.STRING.fieldOf("name").forGetter(OutlinerNode::name),
				ExtraCodecs.UUID.fieldOf("uuid").forGetter(OutlinerNode::uuid),
				Vec3.CODEC.xmap(
					vec3 -> vec3.scale(BASE_SCALE_FACTOR),
					vec3 -> vec3.scale(1 / BASE_SCALE_FACTOR)
				).fieldOf("origin").forGetter(OutlinerNode::origin),
				Vec3.CODEC.lenientOptionalFieldOf("rotation", Vec3.ZERO).forGetter(OutlinerNode::rotation),
				Codec.BOOL.fieldOf("export").forGetter(OutlinerNode::export),
				Codec.BOOL.fieldOf("mirror_uv").forGetter(OutlinerNode::mirrorUv),
				Codec.BOOL.fieldOf("visibility").forGetter(OutlinerNode::visibility),
				Codec.INT.fieldOf("autouv").forGetter(OutlinerNode::autoUv),
				Codec.either(outlineCodec, Codec.STRING).listOf().lenientOptionalFieldOf("children", List.of()).forGetter(OutlinerNode::children),
				Codec.unboundedMap(Codec.STRING, Codec.DOUBLE).lenientOptionalFieldOf("parameters", Map.of()).xmap(
					m -> (Map<String, Double>) new Object2ReferenceLinkedOpenHashMap<>(m, Hash.DEFAULT_LOAD_FACTOR),
					m -> m
				).forGetter(OutlinerNode::parameters)
			).apply(instance, OutlinerNode::new))
		);

	public static final StreamCodec<ByteBuf, OutlinerNode> STREAM_CODEC = ByteBufCodecs.fromCodec(CODEC);

	public Optional<OutlinerNode> getChild(String id) {
		return this.children().stream()
			.filter(c -> c.left().map(n -> n.name().equals(id)).orElse(false))
			.map(c -> c.left())
			.findFirst().orElse(Optional.empty());
	}
}
