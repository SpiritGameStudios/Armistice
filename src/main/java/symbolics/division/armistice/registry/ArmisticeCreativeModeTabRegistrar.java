package symbolics.division.armistice.registry;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import symbolics.division.armistice.component.ArmorSchematicComponent;
import symbolics.division.armistice.component.ChassisSchematicComponent;
import symbolics.division.armistice.component.HullSchematicComponent;
import symbolics.division.armistice.component.OrdnanceSchematicComponent;
import symbolics.division.armistice.mecha.schematic.ArmorSchematic;
import symbolics.division.armistice.mecha.schematic.ChassisSchematic;
import symbolics.division.armistice.mecha.schematic.HullSchematic;
import symbolics.division.armistice.mecha.schematic.OrdnanceSchematic;
import symbolics.division.armistice.util.registrar.CreativeModeTabRegistrar;

import java.util.List;

@SuppressWarnings("unused")
public final class ArmisticeCreativeModeTabRegistrar implements CreativeModeTabRegistrar {
	public static final CreativeModeTab SCHEMATICS = CreativeModeTab.builder()
		.title(Component.translatable("itemGroup.armistice.schematics"))
		.icon(ArmisticeItemRegistrar.MECHA_SCHEMATIC::getDefaultInstance)
		.displayItems((parameters, output) -> {
			List<ArmorSchematic> armorSchematics = ArmisticeRegistries.ARMOR.stream().toList();
			List<HullSchematic> hullSchematics = ArmisticeRegistries.HULL.stream().toList();
			List<ChassisSchematic> chassisSchematics = ArmisticeRegistries.CHASSIS.stream().toList();
			List<OrdnanceSchematic> ordnanceSchematics = ArmisticeRegistries.ORDNANCE.stream().toList();

			armorSchematics.forEach(schematic -> output.accept(
				new ItemStack(
					Holder.direct(ArmisticeItemRegistrar.ARMOR_SCHEMATIC),
					1,
					DataComponentPatch.builder()
						.set(ArmisticeDataComponentTypeRegistrar.ARMOR_SCHEMATIC, new ArmorSchematicComponent(schematic))
						.build()
				)
			));

			hullSchematics.forEach(schematic -> output.accept(
				new ItemStack(
					Holder.direct(ArmisticeItemRegistrar.HULL_SCHEMATIC),
					1,
					DataComponentPatch.builder()
						.set(ArmisticeDataComponentTypeRegistrar.HULL_SCHEMATIC, new HullSchematicComponent(schematic))
						.build()
				)
			));

			chassisSchematics.forEach(schematic -> output.accept(
				new ItemStack(
					Holder.direct(ArmisticeItemRegistrar.CHASSIS_SCHEMATIC),
					1,
					DataComponentPatch.builder()
						.set(ArmisticeDataComponentTypeRegistrar.CHASSIS_SCHEMATIC, new ChassisSchematicComponent(schematic))
						.build()
				)
			));

			ordnanceSchematics.forEach(schematic -> output.accept(
				new ItemStack(
					Holder.direct(ArmisticeItemRegistrar.ORDNANCE_SCHEMATIC),
					1,
					DataComponentPatch.builder()
						.set(ArmisticeDataComponentTypeRegistrar.ORDNANCE_SCHEMATIC, new OrdnanceSchematicComponent(schematic))
						.build()
				)
			));
		})
		.build();

	public static final CreativeModeTab ARMISTICE_DECORATION = CreativeModeTab.builder()
		.title(Component.translatable("itemGroup.armistice.decoration"))
		.icon(() -> ArmisticeBlockRegistrar.CORRUGATED_ARMISTEEL.asItem().getDefaultInstance())
		.displayItems((parameters, output) -> {
			output.accept(ArmisticeBlockRegistrar.ARMISTEEL_PLATING);
			output.accept(ArmisticeBlockRegistrar.ARMISTEEL_GRATE);
			output.accept(ArmisticeBlockRegistrar.CORRUGATED_ARMISTEEL);
			output.accept(ArmisticeBlockRegistrar.ARMISTEEL_CHAIN);
			output.accept(ArmisticeBlockRegistrar.ARMISTEEL_BLOCK);
			output.accept(ArmisticeBlockRegistrar.ARMISTEEL_MESH);
			output.accept(ArmisticeBlockRegistrar.RIGIDIZED_ARMISTEEL);
			output.accept(ArmisticeBlockRegistrar.ARMISTEEL_PIPING);
			output.accept(ArmisticeBlockRegistrar.ARMISTEEL_VENT);
		})
		.build();
}
