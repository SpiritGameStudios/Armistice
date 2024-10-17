package symbolics.division.armistice.util;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.Util;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector4d;

import java.util.List;
import java.util.Map;

public record BBModelData(
	List<Element> elements,
	List<Outline> outliner
) {
	public static final Codec<BBModelData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Element.CODEC.listOf().fieldOf("elements").forGetter(BBModelData::elements),
		Outline.CODEC.listOf().lenientOptionalFieldOf("outliner", List.of()).forGetter(BBModelData::outliner)
	).apply(instance, BBModelData::new));

	public record Outline(
		String name,
		Vec3 origin,
		Vec3 rotation,
		int color,
		boolean export,
		boolean mirrorUv,
		boolean isOpen,
		boolean locked,
		boolean visibility,
		int autoUv,
		List<Either<Outline, String>> children,
		String uuid
	) {
		public static final Codec<Outline> CODEC =
			Codec.recursive(
				Outline.class.getSimpleName(),
				outlineCodec -> RecordCodecBuilder.create(instance -> instance.group(
					Codec.STRING.fieldOf("name").forGetter(Outline::name),
					Vec3.CODEC.fieldOf("origin").forGetter(Outline::origin),
					Vec3.CODEC.lenientOptionalFieldOf("rotation", Vec3.ZERO).forGetter(Outline::origin),
					Codec.INT.fieldOf("color").forGetter(Outline::color),
					Codec.BOOL.fieldOf("export").forGetter(Outline::export),
					Codec.BOOL.fieldOf("mirror_uv").forGetter(Outline::mirrorUv),
					Codec.BOOL.fieldOf("isOpen").forGetter(Outline::isOpen),
					Codec.BOOL.fieldOf("locked").forGetter(Outline::locked),
					Codec.BOOL.fieldOf("visibility").forGetter(Outline::visibility),
					Codec.INT.fieldOf("autouv").forGetter(Outline::autoUv),
					Codec.either(outlineCodec, Codec.STRING).listOf().lenientOptionalFieldOf("children", List.of()).forGetter(Outline::children),
					Codec.STRING.fieldOf("uuid").forGetter(Outline::uuid)
				).apply(instance, Outline::new))
			);
	}

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
		String uuid
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
			Codec.STRING.fieldOf("uuid").forGetter(Element::uuid)
		).apply(instance, Element::new));

		public enum RenderOrder {
			DEFAULT,
			BEHIND,
			IN_FRONT;

			public static final Codec<RenderOrder> CODEC = Codec.stringResolver(renderOrder -> renderOrder.name().toLowerCase(), string -> Enum.valueOf(RenderOrder.class, string.toUpperCase()));
		}

		public record Face(
			Vector4d uv,
			int texture,
			double rotation
		) {
			public static final Codec<Face> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				Codec.DOUBLE
					.listOf()
					.comapFlatMap(
						array -> Util.fixedSize(array, 4).map(vec -> new Vector4d(vec.getFirst(), vec.get(1), vec.get(2), vec.get(3))),
						vec -> List.of(vec.x(), vec.y(), vec.z(), vec.w())
					).fieldOf("texture").forGetter(Face::uv),
				Codec.INT.lenientOptionalFieldOf("texture", -1).forGetter(Face::texture),
				Codec.DOUBLE.lenientOptionalFieldOf("rotation", -1.0).forGetter(Face::rotation)
			).apply(instance, Face::new));
		}
	}
}
