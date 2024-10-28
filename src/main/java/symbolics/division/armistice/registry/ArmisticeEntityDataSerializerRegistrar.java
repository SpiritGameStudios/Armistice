package symbolics.division.armistice.registry;

import net.minecraft.core.Registry;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.joml.Vector3f;
import symbolics.division.armistice.util.registrar.Registrar;

import java.util.List;

public final class ArmisticeEntityDataSerializerRegistrar implements Registrar<EntityDataSerializer<?>> {
	public static final EntityDataSerializer<List<Vector3f>> VEC3_LIST =
		EntityDataSerializer.forValueType(ByteBufCodecs.VECTOR3F.apply(ByteBufCodecs.list()));


	@Override
	public Class<EntityDataSerializer<?>> getObjectType() {
		return Registrar.fixGenerics(EntityDataSerializer.class);
	}

	@Override
	public Registry<EntityDataSerializer<?>> getRegistry() {
		return NeoForgeRegistries.ENTITY_DATA_SERIALIZERS;
	}
}
