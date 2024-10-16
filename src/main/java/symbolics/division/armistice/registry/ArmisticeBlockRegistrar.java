package symbolics.division.armistice.registry;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.WaterloggedTransparentBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import symbolics.division.armistice.util.registrar.BlockRegistrar;

@SuppressWarnings("unused")
public final class ArmisticeBlockRegistrar implements BlockRegistrar {
	public static final Block IRON_GRATE = new WaterloggedTransparentBlock(
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
}
