package symbolics.division.armistice.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class ArmisteelChainBlock extends RotatedPillarBlock implements SimpleWaterloggedBlock {
	public static final MapCodec<ArmisteelChainBlock> CODEC = simpleCodec(ArmisteelChainBlock::new);

	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	protected static final float AABB_MIN = 3.5F;
	protected static final float AABB_MAX = 12.5F;

	protected static final VoxelShape Y_AXIS_AABB = Block.box(AABB_MIN, 0.0, AABB_MIN, AABB_MAX, 16.0, AABB_MAX);
	protected static final VoxelShape Z_AXIS_AABB = Block.box(AABB_MIN, AABB_MIN, 0.0, AABB_MAX, AABB_MAX, 16.0);
	protected static final VoxelShape X_AXIS_AABB = Block.box(0.0, AABB_MIN, AABB_MIN, 16.0, AABB_MAX, AABB_MAX);

	@NotNull
	@Override
	public MapCodec<ArmisteelChainBlock> codec() {
		return CODEC;
	}

	public ArmisteelChainBlock(BlockBehaviour.Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any()
			.setValue(WATERLOGGED, false)
			.setValue(AXIS, Direction.Axis.Y));
	}

	@NotNull
	@Override
	protected VoxelShape getShape(BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
		return switch (state.getValue(AXIS)) {
			case X -> X_AXIS_AABB;
			case Z -> Z_AXIS_AABB;
			case Y -> Y_AXIS_AABB;
		};
	}

	@Override
	public @NotNull BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
		BlockState defaultValue = super.getStateForPlacement(context);

		return defaultValue.setValue(
			WATERLOGGED,
			context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER
		);
	}


	@NotNull
	@Override
	protected BlockState updateShape(BlockState state, @NotNull Direction facing, @NotNull BlockState facingState, @NotNull LevelAccessor level, @NotNull BlockPos currentPos, @NotNull BlockPos facingPos) {
		if (state.getValue(WATERLOGGED)) level.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
		return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(WATERLOGGED).add(AXIS);
	}

	@NotNull
	@Override
	protected FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Override
	protected boolean isPathfindable(@NotNull BlockState state, @NotNull PathComputationType pathComputationType) {
		return false;
	}
}
