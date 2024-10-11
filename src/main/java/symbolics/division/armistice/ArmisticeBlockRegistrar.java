package symbolics.division.armistice;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import symbolics.division.armistice.util.registry.BlockRegistrar;
import symbolics.division.armistice.util.registry.Registrar;

public class ArmisticeBlockRegistrar implements BlockRegistrar {
    public static final Block TEST_BLOCK = new Block(Block.Properties.ofFullCopy(Blocks.STONE));
}
