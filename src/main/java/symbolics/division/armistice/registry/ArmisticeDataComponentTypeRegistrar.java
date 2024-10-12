package symbolics.division.armistice.registry;

import net.minecraft.core.component.DataComponentType;
import symbolics.division.armistice.component.ArmorSchematicComponent;
import symbolics.division.armistice.component.ChassisSchematicComponent;
import symbolics.division.armistice.component.HullSchematicComponent;
import symbolics.division.armistice.component.OrdnanceSchematicComponent;
import symbolics.division.armistice.util.registrar.DataComponentTypeRegistrar;

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
}
