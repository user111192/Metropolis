package team.dovecotmc.old.metropolis.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.mtr.core.tool.Utilities;
import team.dovecotmc.old.metropolis.block.entity.BlockEntityPSDSmallDoorSemiAuto;

/**
 * An inner platform screen door can clear the space behind a neighbouring door
 * that opens in the same direction. Its opening value is expressed in normal
 * door-width units, so it ranges from 0 to 2.
 */
public class BlockPSDSmallDoorInnerSemiAuto extends BlockPSDSmallDoorSemiAuto {
    public static final float MAX_OPEN = 2.0F;

    public BlockPSDSmallDoorInnerSemiAuto(Properties settings) {
        super(settings);
    }

    @Override
    public void setOpenState(boolean open, float doorValue, Level level, BlockPos pos, BlockState state) {
        final float normalOpen = Utilities.clamp(doorValue, 0.0F, 1.0F);
        final Direction openingDirection = state.getValue(FLIPPED) ?
                state.getValue(FACING).getCounterClockWise() : state.getValue(FACING).getClockWise();
        final BlockState neighbourState = level.getBlockState(pos.relative(openingDirection));

        float neighbourOpen = 0.0F;
        if (neighbourState.getBlock() instanceof BlockPSDSmallDoorSemiAuto
                && neighbourState.getValue(FACING) == state.getValue(FACING)
                && neighbourState.getValue(FLIPPED).equals(state.getValue(FLIPPED))
                && level.getBlockEntity(pos.relative(openingDirection)) instanceof BlockEntityPSDSmallDoorSemiAuto neighbour) {
            neighbourOpen = neighbour.open;
        }

        if (level.getBlockEntity(pos) instanceof BlockEntityPSDSmallDoorSemiAuto entity) {
            entity.open = Utilities.clamp(Math.max(normalOpen, 2.0F * neighbourOpen), 0.0F, MAX_OPEN);
        }
    }
}
