package com.refinedmods.refinedstorage.fabric.grid.strategy;

import com.refinedmods.refinedstorage.api.core.Action;
import com.refinedmods.refinedstorage.api.network.node.grid.GridInsertMode;
import com.refinedmods.refinedstorage.api.network.node.grid.GridOperations;
import com.refinedmods.refinedstorage.common.api.grid.Grid;
import com.refinedmods.refinedstorage.common.api.grid.strategy.GridInsertionStrategy;
import com.refinedmods.refinedstorage.common.support.resource.FluidResource;
import com.refinedmods.refinedstorage.common.support.resource.ResourceTypes;

import javax.annotation.Nullable;

import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.PlayerInventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

import static com.refinedmods.refinedstorage.fabric.support.resource.VariantUtil.ofFluidVariant;
import static com.refinedmods.refinedstorage.fabric.support.resource.VariantUtil.toFluidVariant;

public class FluidGridInsertionStrategy implements GridInsertionStrategy {
    private final AbstractContainerMenu containerMenu;
    private final GridOperations gridOperations;
    private final Player player;
    private final PlayerInventoryStorage playerInventoryStorage;

    public FluidGridInsertionStrategy(final AbstractContainerMenu containerMenu,
                                      final ServerPlayer player,
                                      final Grid grid) {
        this.containerMenu = containerMenu;
        this.gridOperations = grid.createOperations(ResourceTypes.FLUID, player);
        this.player = player;
        this.playerInventoryStorage = PlayerInventoryStorage.of(player.getInventory());
    }

    @Override
    public boolean onInsert(final GridInsertMode insertMode, final boolean tryAlternatives) {
        final Storage<FluidVariant> cursorStorage = getFluidCursorStorage();
        if (cursorStorage == null) {
            return false;
        }
        final FluidVariant extractableResource = StorageUtil.findExtractableResource(cursorStorage, null);
        if (extractableResource == null) {
            return false;
        }
        final FluidResource fluidResource = ofFluidVariant(extractableResource);
        gridOperations.insert(fluidResource, insertMode, (resource, amount, action, source) -> {
            if (!(resource instanceof FluidResource fluidResource2)) {
                return 0;
            }
            final FluidVariant fluidVariant = toFluidVariant(fluidResource2);
            try (Transaction tx = Transaction.openOuter()) {
                final long extracted = cursorStorage.extract(fluidVariant, amount, tx);
                if (action == Action.EXECUTE) {
                    tx.commit();
                }
                return extracted;
            }
        });
        return true;
    }

    @Nullable
    private Storage<FluidVariant> getFluidCursorStorage() {
        return FluidStorage.ITEM.find(
            containerMenu.getCarried(),
            ContainerItemContext.ofPlayerCursor(player, containerMenu)
        );
    }

    @Override
    public boolean onTransfer(final int slotIndex) {
        final SingleSlotStorage<ItemVariant> itemSlotStorage = playerInventoryStorage.getSlot(slotIndex);
        if (itemSlotStorage == null) {
            return false;
        }
        final Storage<FluidVariant> fluidSlotStorage = FluidStorage.ITEM.find(
            itemSlotStorage.getResource().toStack(),
            ContainerItemContext.ofPlayerSlot(player, itemSlotStorage)
        );
        if (fluidSlotStorage == null) {
            return false;
        }
        final FluidVariant extractableResource = StorageUtil.findExtractableResource(fluidSlotStorage, null);
        if (extractableResource == null) {
            return false;
        }
        final FluidResource fluidResource = ofFluidVariant(extractableResource);
        gridOperations.insert(fluidResource, GridInsertMode.ENTIRE_RESOURCE, (resource, amount, action, source) -> {
            if (!(resource instanceof FluidResource fluidResource2)) {
                return 0;
            }
            final FluidVariant fluidVariant = toFluidVariant(fluidResource2);
            try (Transaction tx = Transaction.openOuter()) {
                final long extracted = fluidSlotStorage.extract(fluidVariant, amount, tx);
                if (action == Action.EXECUTE) {
                    tx.commit();
                }
                return extracted;
            }
        });
        return true;
    }
}
