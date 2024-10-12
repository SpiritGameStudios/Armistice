package symbolics.division.armistice.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import symbolics.division.armistice.registry.ArmisticeBlockRegistrar;

import java.util.concurrent.CompletableFuture;

import static symbolics.division.armistice.Armistice.MODID;

public class ArmisticeBlockTagsProvider extends BlockTagsProvider {
	public ArmisticeBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
		super(output, lookupProvider, MODID, existingFileHelper);
	}

	@Override
	protected void addTags(@NotNull HolderLookup.Provider provider) {
		tag(BlockTags.NEEDS_STONE_TOOL)
			.add(ArmisticeBlockRegistrar.IRON_GRATE);

		tag(BlockTags.MINEABLE_WITH_PICKAXE)
			.add(ArmisticeBlockRegistrar.IRON_GRATE);
	}
}