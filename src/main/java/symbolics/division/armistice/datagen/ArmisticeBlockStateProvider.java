package symbolics.division.armistice.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.generators.*;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import symbolics.division.armistice.Armistice;
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
			ArmisticeBlockRegistrar.ARMISTEEL_GRATE,
			cubeAll("armisteel_grate", modLoc("block/armisteel_grate")).renderType("cutout")
		);

		cubeAllWithItem(ArmisticeBlockRegistrar.ARMISTEEL_PLATING);
		cubeAllWithItem(ArmisticeBlockRegistrar.CORRUGATED_ARMISTEEL);
		cubeAllWithItem(ArmisticeBlockRegistrar.RIGIDIZED_ARMISTEEL);
		cubeAllWithItem(ArmisticeBlockRegistrar.ARMISTEEL_MESH);
		cubeAllWithItem(ArmisticeBlockRegistrar.ARMISTEEL_BLOCK);
		cubeAllWithItem(ArmisticeBlockRegistrar.ARMISTEEL_PIPING);
		cubeAllWithItem(ArmisticeBlockRegistrar.ARMISTEEL_VENT);

		cubeAllWithItem(ArmisticeBlockRegistrar.IONIZED_ARMISTEEL_MESH);
		cubeAllWithItem(ArmisticeBlockRegistrar.IONIZED_ARMISTEEL_PIPING);
		cubeAllWithItem(ArmisticeBlockRegistrar.IONIZED_ARMISTEEL_VENT);
		cubeAllWithItem(ArmisticeBlockRegistrar.IONIZED_RIGIDIZED_ARMISTEEL);

		axisBlock(
			ArmisticeBlockRegistrar.ARMISTEEL_CHAIN,
			models().cross("armisteel_chain", modLoc("block/armisteel_chain")).renderType("cutout"),
			models().cross("armisteel_chain", modLoc("block/armisteel_chain")).renderType("cutout")
		);

		bulb(ArmisticeBlockRegistrar.ARMISTEEL_BULB, "armisteel_bulb");
		bulb(ArmisticeBlockRegistrar.IONIZED_ARMISTEEL_BULB, "ionized_armisteel_bulb");
		bulb(ArmisticeBlockRegistrar.RUSTED_ARMISTEEL_BULB, "rusted_armisteel_bulb");
		bulb(ArmisticeBlockRegistrar.CORRODED_ARMISTEEL_BULB, "corroded_armisteel_bulb");
		bulb(ArmisticeBlockRegistrar.SCORCHED_ARMISTEEL_BULB, "scorched_armisteel_bulb");

		ModelFile post = bars("post");
		ModelFile postEnds = bars("post_ends");
		ModelFile side = bars("side");
		ModelFile sideAlt = bars("side_alt");
		ModelFile cap = bars("cap");
		ModelFile capAlt = bars("cap_alt");

		MultiPartBlockStateBuilder multipartBuilder = getMultipartBuilder(ArmisticeBlockRegistrar.ARMISTEEL_BARS);

		multipartBuilder.part()
			.modelFile(postEnds)
			.addModel()
			.end();

		multipartBuilder.part()
			.modelFile(post)
			.addModel()
			.condition(BlockStateProperties.NORTH, false)
			.condition(BlockStateProperties.EAST, false)
			.condition(BlockStateProperties.SOUTH, false)
			.condition(BlockStateProperties.WEST, false)
			.end();


		multipartBuilder.part()
			.modelFile(cap)
			.addModel()
			.condition(BlockStateProperties.NORTH, true)
			.condition(BlockStateProperties.EAST, false)
			.condition(BlockStateProperties.SOUTH, false)
			.condition(BlockStateProperties.WEST, false)
			.end();

		multipartBuilder.part()
			.modelFile(cap)
			.rotationY(90)
			.addModel()
			.condition(BlockStateProperties.NORTH, false)
			.condition(BlockStateProperties.EAST, true)
			.condition(BlockStateProperties.SOUTH, false)
			.condition(BlockStateProperties.WEST, false)
			.end();

		multipartBuilder.part()
			.modelFile(capAlt)
			.addModel()
			.condition(BlockStateProperties.NORTH, false)
			.condition(BlockStateProperties.EAST, false)
			.condition(BlockStateProperties.SOUTH, true)
			.condition(BlockStateProperties.WEST, false)
			.end();

		multipartBuilder.part()
			.modelFile(capAlt)
			.rotationY(90)
			.addModel()
			.condition(BlockStateProperties.NORTH, false)
			.condition(BlockStateProperties.EAST, false)
			.condition(BlockStateProperties.SOUTH, false)
			.condition(BlockStateProperties.WEST, true)
			.end();

		multipartBuilder.part()
			.modelFile(side)
			.addModel()
			.condition(BlockStateProperties.NORTH, true)
			.end();

		multipartBuilder.part()
			.modelFile(side)
			.rotationY(90)
			.addModel()
			.condition(BlockStateProperties.EAST, true)
			.end();

		multipartBuilder.part()
			.modelFile(sideAlt)
			.addModel()
			.condition(BlockStateProperties.SOUTH, true)
			.end();

		multipartBuilder.part()
			.modelFile(sideAlt)
			.rotationY(90)
			.addModel()
			.condition(BlockStateProperties.WEST, true)
			.end();


//		paneBlockWithRenderType(
//			ArmisticeBlockRegistrar.ARMISTEEL_BARS,
//			modLoc("block/armisteel_bars"),
//			modLoc("block/armisteel_bars_edge"),
//			"minecraft:cutout"
//		);

		itemModels().getBuilder(ArmisticeBlockRegistrar.ARMISTEEL_BARS.asItem().toString())
			.parent(new ModelFile.UncheckedModelFile("item/generated"))
			.texture("layer0", Armistice.id("block/armisteel_bars"));

		itemModels().getBuilder(ArmisticeBlockRegistrar.ARMISTEEL_CHAIN.asItem().toString())
			.parent(new ModelFile.UncheckedModelFile("item/generated"))
			.texture("layer0", Armistice.id("block/armisteel_chain"));

		trapdoorBlock(ArmisticeBlockRegistrar.ARMISTEEL_TRAPDOOR, Armistice.id("block/armisteel_trapdoor"), false);

		doorBlock(ArmisticeBlockRegistrar.ARMISTEEL_DOOR, Armistice.id("block/armisteel_door_top"), Armistice.id("block/armisteel_door_bottom"));

		itemModels().basicItem(ArmisticeBlockRegistrar.ARMISTEEL_DOOR.asItem());
	}

	public BlockModelBuilder cubeAll(String name, ResourceLocation texture) {
		return models().singleTexture(name, ResourceLocation.withDefaultNamespace(BLOCK_FOLDER + "/cube_all"), "all", texture);
	}

	public void cubeAllWithItem(Block block) {
		simpleBlockWithItem(block, cubeAll(block));
	}

	private ModelFile bars(String name) {
		return models()
			.withExistingParent("armisteel_bars_" + name, "block/iron_bars_" + name)
			.texture("bars", Armistice.id("block/armisteel_bars"))
			.texture("edge", Armistice.id("block/armisteel_bars_edge"))
			.texture("particle", Armistice.id("block/armisteel_bars"))
			.renderType("minecraft:cutout");
	}

	private void bulb(Block block, String id) {
		VariantBlockStateBuilder variantBuilder = getVariantBuilder(block);
		VariantBlockStateBuilder.PartialBlockstate litState = variantBuilder.partialState()
			.with(BlockStateProperties.LIT, true);

		VariantBlockStateBuilder.PartialBlockstate unlitState = variantBuilder.partialState()
			.with(BlockStateProperties.LIT, false);

		variantBuilder.addModels(unlitState,
			unlitState.modelForState()
				.modelFile(cubeAll(id, Armistice.id("block/" + id)))
				.build());

		variantBuilder.addModels(litState,
			litState.modelForState()
				.modelFile(cubeAll(id + "_on", Armistice.id("block/" + id + "_on")))
				.build());

		simpleBlockItem(block, cubeAll(id, Armistice.id("block/" + id)));
	}
}
