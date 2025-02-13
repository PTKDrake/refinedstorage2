package com.refinedmods.refinedstorage.common.constructordestructor;

import com.refinedmods.refinedstorage.api.core.Action;
import com.refinedmods.refinedstorage.api.network.Network;
import com.refinedmods.refinedstorage.api.network.storage.StorageNetworkComponent;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.api.storage.Actor;
import com.refinedmods.refinedstorage.api.storage.root.RootStorage;
import com.refinedmods.refinedstorage.common.Platform;
import com.refinedmods.refinedstorage.common.api.constructordestructor.ConstructorStrategy;
import com.refinedmods.refinedstorage.common.support.resource.FluidResource;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;

public class PlaceFluidConstructorStrategy implements ConstructorStrategy {
    protected final ServerLevel level;
    protected final BlockPos pos;
    protected final Direction direction;

    public PlaceFluidConstructorStrategy(final ServerLevel level, final BlockPos pos, final Direction direction) {
        this.level = level;
        this.pos = pos;
        this.direction = direction;
    }

    @Override
    public Result apply(
        final ResourceKey resource,
        final Actor actor,
        final Player player,
        final Network network
    ) {
        if (!level.isLoaded(pos)) {
            return Result.SKIPPED;
        }
        if (!(resource instanceof FluidResource fluidResource)) {
            return Result.SKIPPED;
        }
        final RootStorage rootStorage = network.getComponent(StorageNetworkComponent.class);
        final long bucketAmount = Platform.INSTANCE.getBucketAmount();
        final long extractedAmount = rootStorage.extract(
            fluidResource,
            bucketAmount,
            Action.SIMULATE,
            actor
        );
        if (bucketAmount != extractedAmount) {
            return Result.RESOURCE_MISSING;
        }
        final boolean success = Platform.INSTANCE.placeFluid(level, pos, direction, player, fluidResource);
        if (success) {
            rootStorage.extract(fluidResource, bucketAmount, Action.EXECUTE, actor);
        }
        return success ? Result.SUCCESS : Result.SKIPPED;
    }
}
