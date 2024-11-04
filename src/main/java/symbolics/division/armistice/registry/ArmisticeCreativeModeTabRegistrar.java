package symbolics.division.armistice.registry;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import symbolics.division.armistice.Armistice;
import symbolics.division.armistice.component.ArmorSchematicComponent;
import symbolics.division.armistice.component.ChassisSchematicComponent;
import symbolics.division.armistice.component.HullSchematicComponent;
import symbolics.division.armistice.component.OrdnanceSchematicComponent;
import symbolics.division.armistice.mecha.MechaSkin;
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
			RegistryAccess access = Minecraft.getInstance().getConnection().registryAccess();

			List<ArmorSchematic> armorSchematics = access.registryOrThrow(ArmisticeRegistries.ARMOR_KEY).stream().toList();
			List<HullSchematic> hullSchematics = access.registryOrThrow(ArmisticeRegistries.HULL_KEY).stream().toList();
			List<ChassisSchematic> chassisSchematics = access.registryOrThrow(ArmisticeRegistries.CHASSIS_KEY).stream().toList();
			List<OrdnanceSchematic> ordnanceSchematics = ArmisticeRegistries.ORDNANCE.stream().toList();
			List<MechaSkin> skins = access.registryOrThrow(ArmisticeRegistries.SKIN_KEY).stream()
				.filter(skin -> !skin.id().equals(Armistice.id("default")))
				.toList();

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

			skins.forEach(skin -> output.accept(
				new ItemStack(
					Holder.direct(ArmisticeItemRegistrar.MECHA_SKIN),
					1,
					DataComponentPatch.builder()
						.set(ArmisticeDataComponentTypeRegistrar.SKIN, skin)
						.build()
				)
			));
		})
		.build();

	public static final CreativeModeTab ARMISTICE_DECORATION = CreativeModeTab.builder()
		.title(Component.translatable("itemGroup.armistice.decoration"))
		.icon(() -> ArmisticeBlockRegistrar.CORRUGATED_ARMISTEEL.asItem().getDefaultInstance())
		.displayItems((parameters, output) -> {
			output.accept(ArmisticeBlockRegistrar.ARMISTEEL_GRATE);
			output.accept(ArmisticeBlockRegistrar.ARMISTEEL_PLATING);
			output.accept(ArmisticeBlockRegistrar.CORRUGATED_ARMISTEEL);
			output.accept(ArmisticeBlockRegistrar.ARMISTEEL_CHAIN);
			output.accept(ArmisticeBlockRegistrar.ARMISTEEL_MESH);
			output.accept(ArmisticeBlockRegistrar.ARMISTEEL_BLOCK);
			output.accept(ArmisticeBlockRegistrar.ARMISTEEL_PIPING);
			output.accept(ArmisticeBlockRegistrar.ARMISTEEL_VENT);
			output.accept(ArmisticeBlockRegistrar.RIGIDIZED_ARMISTEEL);
			output.accept(ArmisticeBlockRegistrar.ARMISTEEL_BULB);
			output.accept(ArmisticeBlockRegistrar.ARMISTEEL_BARS);
			output.accept(ArmisticeBlockRegistrar.ARMISTEEL_DOOR);
			output.accept(ArmisticeBlockRegistrar.ARMISTEEL_TRAPDOOR);
		})
		.build();
}
