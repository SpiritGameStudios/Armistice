package symbolics.division.armistice.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;
import symbolics.division.armistice.Armistice;
import symbolics.division.armistice.recipe.MechaSchematicRecipe;
import symbolics.division.armistice.registry.ArmisticeBlockRegistrar;

import java.util.concurrent.CompletableFuture;

public class ArmisticeRecipeProvider extends RecipeProvider {
	public ArmisticeRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries);
	}

	@Override
	protected void buildRecipes(@NotNull RecipeOutput recipeOutput) {
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ArmisticeBlockRegistrar.IRON_GRATE, 4)
			.define('#', Items.IRON_INGOT)
			.pattern(" # ")
			.pattern("# #")
			.pattern(" # ")
			.unlockedBy(getHasName(ArmisticeBlockRegistrar.IRON_GRATE), has(ArmisticeBlockRegistrar.IRON_GRATE))
			.save(recipeOutput);

		SpecialRecipeBuilder.special(MechaSchematicRecipe::new).save(recipeOutput, Armistice.id("mecha_schematic"));
	}
}
