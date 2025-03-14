package com.refinedmods.refinedstorage.common.constructordestructor;

import com.refinedmods.refinedstorage.api.storage.Actor;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.FireworkRocketItem;
import net.minecraft.world.item.ItemStack;

public class PlaceFireworksConstructorStrategy extends AbstractItemConstructorStrategy {
    public PlaceFireworksConstructorStrategy(
        final ServerLevel level,
        final BlockPos pos,
        final Direction direction
    ) {
        super(level, pos, direction);
    }

    @Override
    protected boolean apply(final ItemStack itemStack, final Actor actor, final Player actingPlayer) {
        if (!(itemStack.getItem() instanceof FireworkRocketItem)) {
            return false;
        }
        level.addFreshEntity(new FireworkRocketEntity(
            level,
            getDispensePositionX(),
            getDispensePositionY(),
            getDispensePositionZ(),
            itemStack
        ));
        return true;
    }

    @Override
    protected boolean hasWork() {
        return true;
    }
}
