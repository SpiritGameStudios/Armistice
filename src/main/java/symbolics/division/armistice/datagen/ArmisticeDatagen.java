package symbolics.division.armistice.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

public final class ArmisticeDatagen {
	@SubscribeEvent
	private static void onGatherData(GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
		PackOutput output = generator.getPackOutput();
		ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
		CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
		generator.addProvider(
			event.includeServer(),
			new ArmisticeBlockStateProvider(output, existingFileHelper)
		);

		generator.addProvider(
			event.includeServer(),
			new ArmisticeLootTableProvider(output, lookupProvider)
		);

		generator.addProvider(
			event.includeServer(),
			new ArmisticeBlockTagsProvider(output, lookupProvider, existingFileHelper)
		);

		generator.addProvider(
			event.includeServer(),
			new ArmisticeRecipeProvider(output, lookupProvider)
		);

		generator.addProvider(
			event.includeClient(),
			new ArmisticeElementsProvider(output, lookupProvider, existingFileHelper)
		);

		generator.addProvider(
			event.includeServer(),
			new ArmisticeOutlinerProvider(output, lookupProvider, existingFileHelper)
		);
	}
}
