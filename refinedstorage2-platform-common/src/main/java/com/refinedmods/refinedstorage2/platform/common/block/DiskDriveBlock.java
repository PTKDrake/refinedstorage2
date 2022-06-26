package com.refinedmods.refinedstorage2.platform.common.block;

import com.refinedmods.refinedstorage2.platform.common.block.entity.diskdrive.DiskDriveBlockEntity;
import com.refinedmods.refinedstorage2.platform.common.content.BlockEntities;

import java.util.function.BiFunction;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class DiskDriveBlock extends NetworkNodeContainerBlock {
    private final BiFunction<BlockPos, BlockState, DiskDriveBlockEntity> blockEntityFactory;

    public DiskDriveBlock(BiFunction<BlockPos, BlockState, DiskDriveBlockEntity> blockEntityFactory) {
        super(BlockConstants.STONE_PROPERTIES);
        this.blockEntityFactory = blockEntityFactory;
    }

    @Override
    protected boolean hasBiDirection() {
        return true;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return blockEntityFactory.apply(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return type == BlockEntities.INSTANCE.getDiskDrive() && !level.isClientSide ? (level2, pos, state2, blockEntity) -> DiskDriveBlockEntity.serverTick(state2, (DiskDriveBlockEntity) blockEntity) : null;
    }
}
