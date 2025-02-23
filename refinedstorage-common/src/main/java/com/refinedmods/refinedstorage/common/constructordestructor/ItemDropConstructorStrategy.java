package com.refinedmods.refinedstorage.common.constructordestructor;

import com.refinedmods.refinedstorage.api.storage.Actor;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

class ItemDropConstructorStrategy extends AbstractItemConstructorStrategy {
    private final long amount;

    ItemDropConstructorStrategy(final ServerLevel level,
                                final BlockPos pos,
                                final Direction direction,
                                final boolean stackUpgrade) {
        super(level, pos, direction);
        this.amount = stackUpgrade ? 64 : 1;
    }

    @Override
    protected long getTransferAmount() {
        return amount;
    }

    @Override
    protected boolean apply(final ItemStack itemStack, final Actor actor, final Player actingPlayer) {
        final Vec3 position = new Vec3(
            getDispensePositionX(),
            getDispensePositionY(),
            getDispensePositionZ()
        );
        DefaultDispenseItemBehavior.spawnItem(level, itemStack, 6, direction, position);
        return true;
    }

    @Override
    protected boolean hasWork() {
        return true;
    }
}
