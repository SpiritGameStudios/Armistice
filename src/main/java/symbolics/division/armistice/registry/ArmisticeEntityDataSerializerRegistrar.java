package symbolics.division.armistice.registry;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import net.minecraft.core.Registry;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.joml.Vector2f;
import org.joml.Vector3f;
import symbolics.division.armistice.mecha.MechaCore;
import symbolics.division.armistice.util.CodecHelper;
import symbolics.division.armistice.util.registrar.Registrar;

import java.util.List;
import java.util.Map;

public final class ArmisticeEntityDataSerializerRegistrar implements Registrar<EntityDataSerializer<?>> {
	public static final EntityDataSerializer<List<Vector3f>> VEC3_LIST =
		EntityDataSerializer.forValueType(ByteBufCodecs.VECTOR3F.apply(ByteBufCodecs.list()));

	public static final EntityDataSerializer<List<Vector2f>> VEC2_LIST =
		EntityDataSerializer.forValueType(CodecHelper.VECTOR2F.apply(ByteBufCodecs.list()));

	public static final EntityDataSerializer<Map<Integer, Vector2f>> INT_VEC2_MAP =
		EntityDataSerializer.forValueType(
			ByteBufCodecs.map(
				Int2ObjectArrayMap::new,
				ByteBufCodecs.INT,
				CodecHelper.VECTOR2F
			)
		);

	public static final EntityDataSerializer<MechaCore> CORE =
		EntityDataSerializer.forValueType(MechaCore.TO_CLIENT_STREAM_CODEC);


	@Override
	public Class<EntityDataSerializer<?>> getObjectType() {
		return Registrar.fixGenerics(EntityDataSerializer.class);
	}

	@Override
	public Registry<EntityDataSerializer<?>> getRegistry() {
		return NeoForgeRegistries.ENTITY_DATA_SERIALIZERS;
	}
}
