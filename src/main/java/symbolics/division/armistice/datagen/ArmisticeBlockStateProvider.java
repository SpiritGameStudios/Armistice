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
			cubeAllInnerFaces("iron_grate", modLoc("block/iron_grate")).renderType("cutout")
		);
	}

	public BlockModelBuilder cubeAllInnerFaces(String name, ResourceLocation texture) {
		return models().singleTexture(name, ResourceLocation.withDefaultNamespace(BLOCK_FOLDER + "/cube_all_inner_faces"), "all", texture);
	}
}
