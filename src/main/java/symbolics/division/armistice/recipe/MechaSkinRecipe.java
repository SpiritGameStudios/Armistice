package symbolics.division.armistice.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import symbolics.division.armistice.component.MechaSchematicComponent;
import symbolics.division.armistice.component.SkinComponent;
import symbolics.division.armistice.mecha.MechaSkin;
import symbolics.division.armistice.registry.ArmisticeDataComponentTypeRegistrar;

import java.util.List;
import java.util.Objects;

public class MechaSkinRecipe extends CustomRecipe {
	public static final RecipeSerializer<MechaSkinRecipe> SERIALIZER = new SimpleCraftingRecipeSerializer<>(MechaSkinRecipe::new);

	public MechaSkinRecipe(CraftingBookCategory category) {
		super(category);
	}

	@Override
	public boolean matches(@NotNull CraftingInput input, @NotNull Level level) {
		List<MechaSchematicComponent> schematics = input.items().stream()
			.map(stack -> stack.get(ArmisticeDataComponentTypeRegistrar.MECHA_SCHEMATIC))
			.filter(Objects::nonNull).toList();

		if (schematics.size() != 1) return false;

		List<MechaSkin> skins = input.items().stream()
			.map(stack -> stack.get(ArmisticeDataComponentTypeRegistrar.SKIN))
			.filter(Objects::nonNull).map(SkinComponent::skin).toList();

		return skins.size() == 1;
	}

	@NotNull
	@Override
	public ItemStack assemble(@NotNull CraftingInput input, @NotNull HolderLookup.Provider registries) {
		ItemStack schematic = input.items().stream()
			.filter(stack -> stack.has(ArmisticeDataComponentTypeRegistrar.MECHA_SCHEMATIC)).toList().getFirst();

		MechaSkin skin = input.items().stream()
			.map(stack -> stack.get(ArmisticeDataComponentTypeRegistrar.SKIN))
			.filter(Objects::nonNull).map(SkinComponent::skin).toList().getFirst();

		ItemStack result = schematic.copy();
		result.set(ArmisticeDataComponentTypeRegistrar.SKIN, new SkinComponent(skin));
		return result;

	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width * height >= 2;
	}

	@NotNull
	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}
}
