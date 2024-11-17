package symbolics.division.armistice.registry;

import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.MapColor;
import symbolics.division.armistice.block.ArmisteelChainBlock;
import symbolics.division.armistice.util.registrar.BlockRegistrar;

import java.util.function.Function;

@SuppressWarnings("unused")
public final class ArmisticeBlockRegistrar implements BlockRegistrar {
	public static final Block ARMISTEEL_GRATE = new WaterloggedTransparentBlock(
		BlockBehaviour.Properties.of()
			.strength(3.0F, 6.0F)
			.sound(SoundType.COPPER_GRATE)
			.mapColor(MapColor.METAL)
			.requiresCorrectToolForDrops()
			.isValidSpawn(Blocks::never)
			.isRedstoneConductor((state, level, pos) -> false)
			.isViewBlocking((state, level, pos) -> false)
			.isSuffocating((state, level, pos) -> false)
			.isViewBlocking((state, level, pos) -> false)
			.noOcclusion()
	);

	public static final Block ARMISTEEL_PLATING = new Block(
		BlockBehaviour.Properties.of()
			.strength(3.0F, 6.0F)
			.sound(ArmisticeSoundEventRegistrar.Types.ARMISTEEL)
			.mapColor(MapColor.METAL)
			.requiresCorrectToolForDrops()
	);

	public static final Block CORRUGATED_ARMISTEEL = new Block(
		BlockBehaviour.Properties.of()
			.strength(3.0F, 6.0F)
			.sound(ArmisticeSoundEventRegistrar.Types.ARMISTEEL)
			.mapColor(MapColor.METAL)
			.requiresCorrectToolForDrops()
	);

	public static final Block ARMISTEEL_PIPING = new Block(
		BlockBehaviour.Properties.of()
			.strength(3.0F, 6.0F)
			.sound(ArmisticeSoundEventRegistrar.Types.ARMISTEEL)
			.mapColor(MapColor.METAL)
			.requiresCorrectToolForDrops()
	);

	public static final Block ARMISTEEL_VENT = new Block(
		BlockBehaviour.Properties.of()
			.strength(3.0F, 6.0F)
			.sound(ArmisticeSoundEventRegistrar.Types.ARMISTEEL)
			.mapColor(MapColor.METAL)
			.requiresCorrectToolForDrops()
	);

	public static final Block ARMISTEEL_MESH = new Block(
		BlockBehaviour.Properties.of()
			.strength(3.0F, 6.0F)
			.sound(ArmisticeSoundEventRegistrar.Types.ARMISTEEL)
			.mapColor(MapColor.METAL)
			.requiresCorrectToolForDrops()
	);

	public static final Block RIGIDIZED_ARMISTEEL = new Block(
		BlockBehaviour.Properties.of()
			.strength(3.0F, 6.0F)
			.sound(ArmisticeSoundEventRegistrar.Types.ARMISTEEL)
			.mapColor(MapColor.METAL)
			.requiresCorrectToolForDrops()
	);

	public static final Block ARMISTEEL_BLOCK = new Block(
		BlockBehaviour.Properties.of()
			.strength(3.0F, 6.0F)
			.sound(ArmisticeSoundEventRegistrar.Types.ARMISTEEL)
			.mapColor(MapColor.METAL)
			.requiresCorrectToolForDrops()
	);

	public static final Block ARMISTEEL_BULB = new CopperBulbBlock(
		BlockBehaviour.Properties.of()
			.strength(3.0F, 6.0F)
			.sound(ArmisticeSoundEventRegistrar.Types.ARMISTEEL)
			.mapColor(MapColor.METAL)
			.requiresCorrectToolForDrops()
			.isRedstoneConductor((state, level, pos) -> false)
			.lightLevel(state -> state.getValue(BlockStateProperties.LIT) ? 15 : 0)
	);

	public static final ArmisteelChainBlock ARMISTEEL_CHAIN = new ArmisteelChainBlock(
		BlockBehaviour.Properties.of()
			.forceSolidOn()
			.requiresCorrectToolForDrops()
			.strength(5.0F, 6.0F)
			.sound(SoundType.CHAIN)
			.noOcclusion()
	);

	public static final IronBarsBlock ARMISTEEL_BARS = new IronBarsBlock(
		BlockBehaviour.Properties.of().
			requiresCorrectToolForDrops()
			.strength(5.0F, 6.0F)
			.sound(SoundType.CHAIN)
			.noOcclusion()
	);

	public static final TrapDoorBlock ARMISTEEL_TRAPDOOR = new TrapDoorBlock(
		BlockSetType.IRON,
		BlockBehaviour.Properties.of()
			.strength(3.0F, 6.0F)
			.sound(ArmisticeSoundEventRegistrar.Types.ARMISTEEL)
			.mapColor(MapColor.METAL)
			.requiresCorrectToolForDrops()
	);

	public static final DoorBlock ARMISTEEL_DOOR = new DoorBlock(
		BlockSetType.IRON,
		BlockBehaviour.Properties.of()
			.strength(3.0F, 6.0F)
			.sound(ArmisticeSoundEventRegistrar.Types.ARMISTEEL)
			.mapColor(MapColor.METAL)
			.requiresCorrectToolForDrops()
	);

	public static final Block IONIZED_ARMISTEEL_MESH = copyOf(ARMISTEEL_MESH);
	public static final Block IONIZED_ARMISTEEL_PIPING = copyOf(ARMISTEEL_PIPING);
	public static final Block IONIZED_ARMISTEEL_VENT = copyOf(ARMISTEEL_VENT);
	public static final Block IONIZED_RIGIDIZED_ARMISTEEL = copyOf(RIGIDIZED_ARMISTEEL);

	public static final Block IONIZED_ARMISTEEL_BULB = copyOf(ARMISTEEL_BULB, CopperBulbBlock::new);
	public static final Block RUSTED_ARMISTEEL_BULB = copyOf(ARMISTEEL_BULB, CopperBulbBlock::new);
	public static final Block CORRODED_ARMISTEEL_BULB = copyOf(ARMISTEEL_BULB, CopperBulbBlock::new);
	public static final Block SCORCHED_ARMISTEEL_BULB = copyOf(ARMISTEEL_BULB, CopperBulbBlock::new);

	private static Block copyOf(BlockBehaviour referenceBlock) {
		return copyOf(referenceBlock, Block::new);
	}

	private static <T extends Block> T copyOf(BlockBehaviour referenceBlock, Function<BlockBehaviour.Properties, T> constructor) {
		return constructor.apply(BlockBehaviour.Properties.ofFullCopy(referenceBlock));
	}
}
