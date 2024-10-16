package symbolics.division.armistice.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.Util;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector4d;

import java.util.List;
import java.util.Map;

public record BBModelData(
	List<Element> elements
) {
	public static final Codec<BBModelData> CODEC = Element.CODEC.listOf().xmap(BBModelData::new, BBModelData::elements);

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
		Map<Direction, Face> faces
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
			Codec.unboundedMap(Direction.CODEC, Face.CODEC).fieldOf("faces").forGetter(Element::faces)
		).apply(instance, Element::new));

		public enum RenderOrder {
			DEFAULT,
			BEHIND,
			IN_FRONT;

			public static final Codec<RenderOrder> CODEC = Codec.stringResolver(renderOrder -> renderOrder.name().toLowerCase(), string -> Enum.valueOf(RenderOrder.class, string.toUpperCase()));
		}

		public record Face(
			Vector4d uv
		) {
			public static final Codec<Face> CODEC = Codec.DOUBLE
				.listOf()
				.comapFlatMap(
					array -> Util.fixedSize(array, 4).map(vec -> new Vector4d(vec.getFirst(), vec.get(1), vec.get(2), vec.get(3))),
					vec -> List.of(vec.x(), vec.y(), vec.z(), vec.w())
				).xmap(Face::new, Face::uv);
		}
	}
}
