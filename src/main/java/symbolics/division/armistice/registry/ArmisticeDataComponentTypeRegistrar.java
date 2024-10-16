package symbolics.division.armistice.registry;

import net.minecraft.core.component.DataComponentType;
import symbolics.division.armistice.component.*;
import symbolics.division.armistice.util.registrar.DataComponentTypeRegistrar;

@SuppressWarnings("unused")
public final class ArmisticeDataComponentTypeRegistrar implements DataComponentTypeRegistrar {
	public static final DataComponentType<ArmorSchematicComponent> ARMOR_SCHEMATIC = DataComponentType.<ArmorSchematicComponent>builder()
		.persistent(ArmorSchematicComponent.CODEC)
		.networkSynchronized(ArmorSchematicComponent.STREAM_CODEC)
		.build();

	public static final DataComponentType<ChassisSchematicComponent> CHASSIS_SCHEMATIC = DataComponentType.<ChassisSchematicComponent>builder()
		.persistent(ChassisSchematicComponent.CODEC)
		.networkSynchronized(ChassisSchematicComponent.STREAM_CODEC)
		.build();

	public static final DataComponentType<HullSchematicComponent> HULL_SCHEMATIC = DataComponentType.<HullSchematicComponent>builder()
		.persistent(HullSchematicComponent.CODEC)
		.networkSynchronized(HullSchematicComponent.STREAM_CODEC)
		.build();

	public static final DataComponentType<OrdnanceSchematicComponent> ORDNANCE_SCHEMATIC = DataComponentType.<OrdnanceSchematicComponent>builder()
		.persistent(OrdnanceSchematicComponent.CODEC)
		.networkSynchronized(OrdnanceSchematicComponent.STREAM_CODEC)
		.build();

	public static final DataComponentType<MechaSchematicComponent> MECHA_SCHEMATIC = DataComponentType.<MechaSchematicComponent>builder()
		.persistent(MechaSchematicComponent.CODEC)
		.networkSynchronized(MechaSchematicComponent.STREAM_CODEC)
		.build();
}
