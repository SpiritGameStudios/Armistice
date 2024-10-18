package symbolics.division.armistice.model;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.phys.Vec3;
import symbolics.division.armistice.util.CodecHelper;

import java.util.List;
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
	List<Either<OutlinerNode, String>> children
) {
	public static final Codec<OutlinerNode> CODEC =
		Codec.recursive(
			OutlinerNode.class.getSimpleName(),
			outlineCodec -> RecordCodecBuilder.create(instance -> instance.group(
				Codec.STRING.fieldOf("name").forGetter(OutlinerNode::name),
				CodecHelper.UUID.fieldOf("uuid").forGetter(OutlinerNode::uuid),
				Vec3.CODEC.fieldOf("origin").forGetter(OutlinerNode::origin),
				Vec3.CODEC.lenientOptionalFieldOf("rotation", Vec3.ZERO).forGetter(OutlinerNode::origin),
				Codec.BOOL.fieldOf("export").forGetter(OutlinerNode::export),
				Codec.BOOL.fieldOf("mirror_uv").forGetter(OutlinerNode::mirrorUv),
				Codec.BOOL.fieldOf("visibility").forGetter(OutlinerNode::visibility),
				Codec.INT.fieldOf("autouv").forGetter(OutlinerNode::autoUv),
				Codec.either(outlineCodec, Codec.STRING).listOf().lenientOptionalFieldOf("children", List.of()).forGetter(OutlinerNode::children)
			).apply(instance, OutlinerNode::new))
		);
}
