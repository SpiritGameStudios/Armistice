package symbolics.division.armistice.recipe;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import symbolics.division.armistice.component.*;
import symbolics.division.armistice.mecha.schematic.MechaSchematic;
import symbolics.division.armistice.registry.ArmisticeDataComponentTypeRegistrar;
import symbolics.division.armistice.registry.ArmisticeItemRegistrar;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class MechaSchematicRecipe extends CustomRecipe {
	public static final RecipeSerializer<MechaSchematicRecipe> SERIALIZER = new SimpleCraftingRecipeSerializer<>(MechaSchematicRecipe::new);

	private static final Set<Item> PARTS = Set.of(
		ArmisticeItemRegistrar.ARMOR_SCHEMATIC,
		ArmisticeItemRegistrar.CHASSIS_SCHEMATIC,
		ArmisticeItemRegistrar.HULL_SCHEMATIC,
		ArmisticeItemRegistrar.ORDNANCE_SCHEMATIC
	);

	public MechaSchematicRecipe(CraftingBookCategory category) {
		super(category);
	}

	private static @Nullable MechaSchematic getMechaSchematic(@NotNull CraftingInput input) {
		List<ItemStack> items = new ArrayList<>(input.items());
		items.removeIf(ItemStack::isEmpty);

		if (!PARTS.containsAll(items.stream().map(ItemStack::getItem).toList())) return null;

		List<ArmorSchematicComponent> armorSchematics = items.stream()
			.map(stack -> stack.get(ArmisticeDataComponentTypeRegistrar.ARMOR_SCHEMATIC))
			.filter(Objects::nonNull)
			.toList();

		if (armorSchematics.size() != 1) return null;
		ArmorSchematicComponent armor = armorSchematics.getFirst();

		List<HullSchematicComponent> hullSchematics = items.stream()
			.map(stack -> stack.get(ArmisticeDataComponentTypeRegistrar.HULL_SCHEMATIC))
			.filter(Objects::nonNull)
			.toList();

		if (hullSchematics.size() != 1) return null;
		HullSchematicComponent hull = hullSchematics.getFirst();

		List<ChassisSchematicComponent> chassisSchematics = items.stream()
			.map(stack -> stack.get(ArmisticeDataComponentTypeRegistrar.CHASSIS_SCHEMATIC))
			.filter(Objects::nonNull)
			.toList();

		if (chassisSchematics.size() != 1) return null;
		ChassisSchematicComponent chassis = chassisSchematics.getFirst();

		List<OrdnanceSchematicComponent> ordnance = items.stream()
			.map(stack -> stack.get(ArmisticeDataComponentTypeRegistrar.ORDNANCE_SCHEMATIC))
			.filter(Objects::nonNull)
			.toList();

		return new MechaSchematic(
			hull.schematic(),
			ordnance.stream().map(OrdnanceSchematicComponent::schematic).toList(),
			chassis.schematic(),
			armor.schematic()
		);
	}

	@Override
	public boolean matches(@NotNull CraftingInput input, @NotNull Level level) {
		MechaSchematic mechaSchematic = getMechaSchematic(input);
		if (mechaSchematic == null) return false;

		return mechaSchematic.verify();
	}

	@NotNull
	@Override
	public ItemStack assemble(@NotNull CraftingInput input, HolderLookup.@NotNull Provider registries) {
		MechaSchematic schematic = getMechaSchematic(input);

		return new ItemStack(
			Holder.direct(ArmisticeItemRegistrar.MECHA_SCHEMATIC),
			1,
			DataComponentPatch.builder()
				.set(ArmisticeDataComponentTypeRegistrar.MECHA_SCHEMATIC, new MechaSchematicComponent(schematic))
				.build()
		);
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return (width * height) >= 4;
	}

	@NotNull
	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}
}
