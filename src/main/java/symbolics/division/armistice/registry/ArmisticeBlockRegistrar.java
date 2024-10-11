package symbolics.division.armistice.registry;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import symbolics.division.armistice.util.registrar.BlockRegistrar;

public class ArmisticeBlockRegistrar implements BlockRegistrar {
    public static final Block TEST_BLOCK = new Block(Block.Properties.ofFullCopy(Blocks.STONE));
}
