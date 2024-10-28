package symbolics.division.armistice.registry;

import net.minecraft.core.Registry;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import symbolics.division.armistice.mecha.MechaEntity;
import symbolics.division.armistice.util.registrar.Registrar;

import java.util.List;

public final class ArmisticeEntityDataSerializerRegistrar implements Registrar<EntityDataSerializer<?>> {
	public static final EntityDataAccessor<List<Vector3f>> LEG_TICK_TARGETS = SynchedEntityData.defineId(
		MechaEntity.class,
		EntityDataSerializer.forValueType(ByteBufCodecs.VECTOR3F.apply(ByteBufCodecs.list()))
	);

	public static final EntityDataAccessor<Vector3f> ABS_POS = SynchedEntityData.defineId(
		MechaEntity.class,
		EntityDataSerializers.VECTOR3
	);

	public static final EntityDataAccessor<Quaternionf> ABS_ROT = SynchedEntityData.defineId(
		MechaEntity.class,
		EntityDataSerializer.forValueType(ByteBufCodecs.QUATERNIONF)
	);

	@Override
	public Class<EntityDataSerializer<?>> getObjectType() {
		return Registrar.fixGenerics(EntityDataSerializer.class);
	}

	@Override
	public Registry<EntityDataSerializer<?>> getRegistry() {
		return NeoForgeRegistries.ENTITY_DATA_SERIALIZERS;
	}
}
