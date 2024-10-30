package symbolics.division.armistice.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SpecialRecipeBuilder;
import org.jetbrains.annotations.NotNull;
import symbolics.division.armistice.Armistice;
import symbolics.division.armistice.recipe.MechaSchematicRecipe;

import java.util.concurrent.CompletableFuture;

public class ArmisticeRecipeProvider extends RecipeProvider {
	public ArmisticeRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries);
	}

	@Override
	protected void buildRecipes(@NotNull RecipeOutput recipeOutput) {
		SpecialRecipeBuilder.special(MechaSchematicRecipe::new).save(recipeOutput, Armistice.id("mecha_schematic"));
	}
}
