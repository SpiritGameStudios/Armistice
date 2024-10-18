package symbolics.division.armistice.model;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector4d;
import symbolics.division.armistice.util.CodecHelper;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public record Element(
	String name,
	boolean boxUv,
	boolean rescale,
	boolean locked,
	int lightEmission,
	RenderOrder renderOrder,
	boolean allowMirrorModeling,
	Vec3 from,
	Vec3 to,
	int autoUv,
	int color,
	Vec3 origin,
	Map<Direction, Face> faces,
	UUID uuid
) {
	public static final Codec<Element> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.STRING.fieldOf("name").forGetter(Element::name),
		Codec.BOOL.fieldOf("box_uv").forGetter(Element::boxUv),
		Codec.BOOL.fieldOf("rescale").forGetter(Element::rescale),
		Codec.BOOL.fieldOf("locked").forGetter(Element::locked),
		Codec.INT.fieldOf("light_emission").forGetter(Element::lightEmission),
		RenderOrder.CODEC.fieldOf("render_order").forGetter(Element::renderOrder),
		Codec.BOOL.fieldOf("allow_mirror_modeling").forGetter(Element::allowMirrorModeling),
		Vec3.CODEC.fieldOf("from").forGetter(Element::from),
		Vec3.CODEC.fieldOf("to").forGetter(Element::to),
		Codec.INT.fieldOf("autouv").forGetter(Element::autoUv),
		Codec.INT.fieldOf("color").forGetter(Element::color),
		Vec3.CODEC.fieldOf("origin").forGetter(Element::origin),
		Codec.unboundedMap(Direction.CODEC, Face.CODEC).fieldOf("faces").forGetter(Element::faces),
		CodecHelper.UUID.fieldOf("uuid").forGetter(Element::uuid)
	).apply(instance, Element::new));

	public enum RenderOrder {
		DEFAULT,
		BEHIND,
		IN_FRONT;

		public static final Codec<RenderOrder> CODEC = Codec.stringResolver(renderOrder -> renderOrder.name().toLowerCase(), string -> Enum.valueOf(RenderOrder.class, string.toUpperCase()));
	}

	public record Face(
		Vector4d uv,
		Optional<Integer> texture,
		Optional<Integer> rotation,
		Optional<Integer> tintIndex,
		Optional<Direction> cullFace
	) {
		public static final Codec<Face> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			CodecHelper.VECTOR4D.fieldOf("uv").forGetter(Face::uv),
			Codec.INT.lenientOptionalFieldOf("texture").forGetter(Face::texture),
			Codec.INT.lenientOptionalFieldOf("rotation").forGetter(Face::rotation),
			Codec.INT.lenientOptionalFieldOf("tintindex").forGetter(Face::tintIndex),
			Direction.CODEC.lenientOptionalFieldOf("cullface").forGetter(Face::cullFace)
		).apply(instance, Face::new));
	}
}
