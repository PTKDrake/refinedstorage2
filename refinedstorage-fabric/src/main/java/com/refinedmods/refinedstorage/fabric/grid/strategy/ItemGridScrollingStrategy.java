package com.refinedmods.refinedstorage.fabric.grid.strategy;

import com.refinedmods.refinedstorage.api.core.Action;
import com.refinedmods.refinedstorage.api.network.node.grid.GridExtractMode;
import com.refinedmods.refinedstorage.api.network.node.grid.GridInsertMode;
import com.refinedmods.refinedstorage.api.network.node.grid.GridOperations;
import com.refinedmods.refinedstorage.common.api.grid.Grid;
import com.refinedmods.refinedstorage.common.api.grid.GridScrollMode;
import com.refinedmods.refinedstorage.common.api.grid.strategy.GridScrollingStrategy;
import com.refinedmods.refinedstorage.common.api.support.resource.PlatformResourceKey;
import com.refinedmods.refinedstorage.common.support.resource.ItemResource;
import com.refinedmods.refinedstorage.common.support.resource.ResourceTypes;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.PlayerInventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;

import static com.refinedmods.refinedstorage.fabric.support.resource.VariantUtil.toItemVariant;

public class ItemGridScrollingStrategy implements GridScrollingStrategy {
    private final GridOperations gridOperations;
    private final PlayerInventoryStorage playerInventoryStorage;
    private final SingleSlotStorage<ItemVariant> playerCursorStorage;

    public ItemGridScrollingStrategy(final AbstractContainerMenu containerMenu,
                                     final ServerPlayer player,
                                     final Grid grid) {
        this.gridOperations = grid.createOperations(ResourceTypes.ITEM, player);
        this.playerInventoryStorage = PlayerInventoryStorage.of(player.getInventory());
        this.playerCursorStorage = PlayerInventoryStorage.getCursorStorage(containerMenu);
    }

    @Override
    public boolean onScroll(final PlatformResourceKey resource, final GridScrollMode scrollMode, final int slotIndex) {
        if (resource instanceof ItemResource itemResource) {
            final Storage<ItemVariant> playerStorage = slotIndex >= 0
                ? playerInventoryStorage.getSlot(slotIndex)
                : playerInventoryStorage;
            switch (scrollMode) {
                case GRID_TO_INVENTORY -> handleGridToInventoryScroll(itemResource, playerStorage);
                case INVENTORY_TO_GRID -> handleInventoryToGridScroll(itemResource, playerStorage);
                case GRID_TO_CURSOR -> handleGridToInventoryScroll(itemResource, playerCursorStorage);
            }
            return true;
        }
        return false;
    }

    private void handleInventoryToGridScroll(final ItemResource itemResource,
                                             final Storage<ItemVariant> sourceStorage) {
        gridOperations.insert(itemResource, GridInsertMode.SINGLE_RESOURCE, (resource, amount, action, source) -> {
            if (!(resource instanceof ItemResource itemResource2)) {
                return 0;
            }
            try (Transaction tx = Transaction.openOuter()) {
                final ItemVariant itemVariant = toItemVariant(itemResource2);
                final long extracted = sourceStorage.extract(itemVariant, amount, tx);
                if (action == Action.EXECUTE) {
                    tx.commit();
                }
                return extracted;
            }
        });
    }

    private void handleGridToInventoryScroll(final ItemResource itemResource,
                                             final Storage<ItemVariant> destinationStorage) {
        gridOperations.extract(itemResource, GridExtractMode.SINGLE_RESOURCE, (resource, amount, action, source) -> {
            if (!(resource instanceof ItemResource itemResource2)) {
                return 0;
            }
            final ItemVariant itemVariant = toItemVariant(itemResource2);
            try (Transaction tx = Transaction.openOuter()) {
                final long inserted = destinationStorage.insert(itemVariant, amount, tx);
                if (action == Action.EXECUTE) {
                    tx.commit();
                }
                return inserted;
            }
        });
    }
}
