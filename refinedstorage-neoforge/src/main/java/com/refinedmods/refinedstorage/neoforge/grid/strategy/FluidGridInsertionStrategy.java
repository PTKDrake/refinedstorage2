package com.refinedmods.refinedstorage.neoforge.grid.strategy;

import com.refinedmods.refinedstorage.api.core.Action;
import com.refinedmods.refinedstorage.api.network.node.grid.GridInsertMode;
import com.refinedmods.refinedstorage.api.network.node.grid.GridOperations;
import com.refinedmods.refinedstorage.common.api.grid.Grid;
import com.refinedmods.refinedstorage.common.api.grid.strategy.GridInsertionStrategy;
import com.refinedmods.refinedstorage.common.support.resource.FluidResource;
import com.refinedmods.refinedstorage.common.support.resource.ResourceTypes;

import javax.annotation.Nullable;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;

import static com.refinedmods.refinedstorage.neoforge.support.resource.VariantUtil.ofFluidStack;
import static com.refinedmods.refinedstorage.neoforge.support.resource.VariantUtil.toFluidAction;
import static com.refinedmods.refinedstorage.neoforge.support.resource.VariantUtil.toFluidStack;

public class FluidGridInsertionStrategy implements GridInsertionStrategy {
    private final AbstractContainerMenu menu;
    private final GridOperations gridOperations;

    public FluidGridInsertionStrategy(final AbstractContainerMenu menu, final ServerPlayer player, final Grid grid) {
        this.menu = menu;
        this.gridOperations = grid.createOperations(ResourceTypes.FLUID, player);
    }

    @Override
    public boolean onInsert(final GridInsertMode insertMode, final boolean tryAlternatives) {
        final IFluidHandlerItem cursorStorage = getFluidCursorStorage();
        if (cursorStorage == null) {
            return false;
        }
        final FluidStack extractableResource = cursorStorage.getFluidInTank(0);
        if (extractableResource.isEmpty()) {
            return false;
        }
        final FluidResource fluidResource = ofFluidStack(extractableResource);
        gridOperations.insert(fluidResource, insertMode, (resource, amount, action, source) -> {
            if (!(resource instanceof FluidResource fluidResource2)) {
                return 0;
            }
            final FluidStack toDrain = toFluidStack(
                fluidResource2,
                amount == Long.MAX_VALUE ? Integer.MAX_VALUE : amount
            );
            final FluidStack drained = cursorStorage.drain(toDrain, toFluidAction(action));
            if (action == Action.EXECUTE) {
                menu.setCarried(cursorStorage.getContainer());
            }
            return drained.getAmount();
        });
        return true;
    }

    @Nullable
    private IFluidHandlerItem getFluidCursorStorage() {
        return getFluidStorage(menu.getCarried());
    }

    @Nullable
    private IFluidHandlerItem getFluidStorage(final ItemStack stack) {
        return stack.getCapability(Capabilities.FluidHandler.ITEM);
    }

    @Override
    public boolean onTransfer(final int slotIndex) {
        throw new UnsupportedOperationException();
    }
}
