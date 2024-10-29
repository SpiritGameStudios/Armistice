package symbolics.division.armistice.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import symbolics.division.armistice.registry.ArmisticeBlockRegistrar;

import static net.neoforged.neoforge.client.model.generators.ModelProvider.BLOCK_FOLDER;
import static symbolics.division.armistice.Armistice.MODID;

public class ArmisticeBlockStateProvider extends BlockStateProvider {
	public ArmisticeBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
		super(output, MODID, exFileHelper);
	}

	@Override
	protected void registerStatesAndModels() {
		simpleBlockWithItem(
			ArmisticeBlockRegistrar.IRON_GRATE,
			cubeAll("iron_grate", modLoc("block/iron_grate")).renderType("cutout")
		);

		simpleBlockWithItem(
			ArmisticeBlockRegistrar.ARMISTEEL_GRATE,
			cubeAll("armisteel_grate", modLoc("block/armisteel_grate")).renderType("cutout")
		);

		simpleBlockWithItem(ArmisticeBlockRegistrar.ARMISTEEL_PLATING, cubeAll(ArmisticeBlockRegistrar.ARMISTEEL_PLATING));
		simpleBlockWithItem(ArmisticeBlockRegistrar.CORRUGATED_ARMISTEEL, cubeAll(ArmisticeBlockRegistrar.CORRUGATED_ARMISTEEL));
	}

	public BlockModelBuilder cubeAll(String name, ResourceLocation texture) {
		return models().singleTexture(name, ResourceLocation.withDefaultNamespace(BLOCK_FOLDER + "/cube_all"), "all", texture);
	}
}
