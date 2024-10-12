package symbolics.division.armistice.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.jetbrains.annotations.NotNull;
import symbolics.division.armistice.registry.ArmisticeBlockRegistrar;
import symbolics.division.armistice.util.registrar.Registrar;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class ArmisticeLootTableProvider extends LootTableProvider {
	public ArmisticeLootTableProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
		super(output, Set.of(), List.of(
			new SubProviderEntry(
				BlockSubProvider::new,
				LootContextParamSets.BLOCK
			)
		), registries);
	}

	public static class BlockSubProvider extends BlockLootSubProvider {
		public BlockSubProvider(HolderLookup.Provider lookupProvider) {
			super(Set.of(), FeatureFlags.DEFAULT_FLAGS, lookupProvider);
		}

		@NotNull
		@Override
		protected Iterable<Block> getKnownBlocks() {
			return Registrar.getObjects(ArmisticeBlockRegistrar.class, Block.class);
		}

		@Override
		protected void generate() {
			dropSelf(ArmisticeBlockRegistrar.IRON_GRATE);
		}
	}
}