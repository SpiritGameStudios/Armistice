package symbolics.division.armistice.registry;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.WaterloggedTransparentBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import symbolics.division.armistice.block.ArmisteelChainBlock;
import symbolics.division.armistice.util.registrar.BlockRegistrar;

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
			.sound(SoundType.NETHERITE_BLOCK)
			.mapColor(MapColor.METAL)
			.requiresCorrectToolForDrops()
	);

	public static final Block CORRUGATED_ARMISTEEL = new Block(
		BlockBehaviour.Properties.of()
			.strength(3.0F, 6.0F)
			.sound(SoundType.NETHERITE_BLOCK)
			.mapColor(MapColor.METAL)
			.requiresCorrectToolForDrops()
	);

	public static final ArmisteelChainBlock ARMISTEEL_CHAIN = new ArmisteelChainBlock(
		BlockBehaviour.Properties.of()
			.forceSolidOn()
			.requiresCorrectToolForDrops()
			.strength(5.0F, 6.0F)
			.sound(SoundType.CHAIN)
			.noOcclusion()
	);
}
