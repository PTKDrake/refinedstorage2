package com.refinedmods.refinedstorage.common.constructordestructor;

import com.refinedmods.refinedstorage.api.core.Action;
import com.refinedmods.refinedstorage.api.network.Network;
import com.refinedmods.refinedstorage.api.network.storage.StorageNetworkComponent;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.api.storage.Actor;
import com.refinedmods.refinedstorage.api.storage.root.RootStorage;
import com.refinedmods.refinedstorage.common.api.constructordestructor.ConstructorStrategy;
import com.refinedmods.refinedstorage.common.support.resource.ItemResource;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

abstract class AbstractItemConstructorStrategy implements ConstructorStrategy {
    protected final ServerLevel level;
    protected final BlockPos pos;
    protected final Direction direction;

    AbstractItemConstructorStrategy(final ServerLevel level, final BlockPos pos, final Direction direction) {
        this.level = level;
        this.pos = pos;
        this.direction = direction;
    }

    protected long getTransferAmount() {
        return 1;
    }

    @Override
    public final Result apply(
        final ResourceKey resource,
        final Actor actor,
        final Player player,
        final Network network
    ) {
        if (!level.isLoaded(pos)) {
            return Result.SKIPPED;
        }
        if (!(resource instanceof ItemResource itemResource)) {
            return Result.SKIPPED;
        }
        final RootStorage rootStorage = network.getComponent(StorageNetworkComponent.class);
        final long amount = getTransferAmount();
        final long extractedAmount = rootStorage.extract(itemResource, amount, Action.SIMULATE, actor);
        if (extractedAmount == 0) {
            return Result.RESOURCE_MISSING;
        }
        final ItemStack itemStack = itemResource.toItemStack(extractedAmount);
        final boolean success = apply(itemStack, actor, player);
        if (success) {
            rootStorage.extract(itemResource, extractedAmount, Action.EXECUTE, actor);
        }
        return success ? Result.SUCCESS : Result.SKIPPED;
    }

    protected abstract boolean apply(ItemStack itemStack, Actor actor, Player actingPlayer);

    protected double getDispensePositionX() {
        return pos.getX() + 0.5D;
    }

    protected double getDispensePositionY() {
        return pos.getY() + (direction == Direction.DOWN ? 0.45D : 0.5D);
    }

    protected double getDispensePositionZ() {
        return pos.getZ() + 0.5D;
    }
}
