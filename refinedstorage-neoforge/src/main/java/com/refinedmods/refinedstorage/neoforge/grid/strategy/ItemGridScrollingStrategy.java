package com.refinedmods.refinedstorage.neoforge.grid.strategy;

import com.refinedmods.refinedstorage.api.network.node.grid.GridExtractMode;
import com.refinedmods.refinedstorage.api.network.node.grid.GridInsertMode;
import com.refinedmods.refinedstorage.api.network.node.grid.GridOperations;
import com.refinedmods.refinedstorage.common.api.grid.Grid;
import com.refinedmods.refinedstorage.common.api.grid.GridScrollMode;
import com.refinedmods.refinedstorage.common.api.grid.strategy.GridScrollingStrategy;
import com.refinedmods.refinedstorage.common.api.support.resource.PlatformResourceKey;
import com.refinedmods.refinedstorage.common.support.resource.ItemResource;
import com.refinedmods.refinedstorage.common.support.resource.ResourceTypes;
import com.refinedmods.refinedstorage.neoforge.storage.CapabilityCache;
import com.refinedmods.refinedstorage.neoforge.storage.ItemHandlerExtractableStorage;
import com.refinedmods.refinedstorage.neoforge.storage.ItemHandlerInsertableStorage;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import net.neoforged.neoforge.items.wrapper.PlayerMainInvWrapper;
import net.neoforged.neoforge.items.wrapper.RangedWrapper;

public class ItemGridScrollingStrategy implements GridScrollingStrategy {
    private final GridOperations gridOperations;
    private final Inventory playerInventory;
    private final PlayerMainInvWrapper playerInventoryStorage;
    private final CursorItemHandler playerCursorItemHandler;

    public ItemGridScrollingStrategy(final AbstractContainerMenu containerMenu,
                                     final ServerPlayer player,
                                     final Grid grid) {
        this.gridOperations = grid.createOperations(ResourceTypes.ITEM, player);
        this.playerInventory = player.getInventory();
        this.playerInventoryStorage = new PlayerMainInvWrapper(playerInventory);
        this.playerCursorItemHandler = new CursorItemHandler(containerMenu);
    }

    @Override
    public boolean onScroll(final PlatformResourceKey resource, final GridScrollMode scrollMode, final int slotIndex) {
        if (resource instanceof ItemResource itemResource) {
            final IItemHandler playerStorage = slotIndex >= 0
                ? new RangedWrapper(new InvWrapper(playerInventory), slotIndex, slotIndex + 1)
                : playerInventoryStorage;
            switch (scrollMode) {
                case GRID_TO_INVENTORY -> handleGridToInventoryScroll(itemResource, playerStorage);
                case INVENTORY_TO_GRID -> handleInventoryToGridScroll(itemResource, playerStorage);
                case GRID_TO_CURSOR -> handleGridToInventoryScroll(itemResource, playerCursorItemHandler);
            }
            return true;
        }
        return false;
    }

    private void handleInventoryToGridScroll(final ItemResource itemResource, final IItemHandler sourceStorage) {
        gridOperations.insert(
            itemResource,
            GridInsertMode.SINGLE_RESOURCE,
            new ItemHandlerExtractableStorage(CapabilityCache.ofItemHandler(sourceStorage))
        );
    }

    private void handleGridToInventoryScroll(final ItemResource itemResource, final IItemHandler destinationStorage) {
        gridOperations.extract(
            itemResource,
            GridExtractMode.SINGLE_RESOURCE,
            new ItemHandlerInsertableStorage(CapabilityCache.ofItemHandler(destinationStorage))
        );
    }
}
