package com.refinedmods.refinedstorage2.platform.common.containermenu.grid;

import com.refinedmods.refinedstorage2.api.resource.list.ResourceList;
import com.refinedmods.refinedstorage2.platform.api.resource.ItemResource;
import com.refinedmods.refinedstorage2.platform.common.block.entity.grid.CraftingGridBlockEntity;
import com.refinedmods.refinedstorage2.platform.common.content.Menus;

import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nullable;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class CraftingGridContainerMenu extends AbstractGridContainerMenu {
    private static final int Y_OFFSET_BETWEEN_PLAYER_INVENTORY_AND_FIRST_CRAFTING_MATRIX_SLOT = 69;

    private final Player player;
    private final CraftingGridSource source;

    @Nullable
    private Consumer<Boolean> activenessListener;

    public CraftingGridContainerMenu(final int syncId, final Inventory playerInventory, final FriendlyByteBuf buf) {
        super(Menus.INSTANCE.getCraftingGrid(), syncId, playerInventory, buf);
        this.source = new ClientCraftingGridSource();
        this.player = playerInventory.player;
        addSlots(0);
    }

    public CraftingGridContainerMenu(final int syncId,
                                     final Inventory playerInventory,
                                     final CraftingGridBlockEntity grid) {
        super(Menus.INSTANCE.getCraftingGrid(), syncId, playerInventory, grid);
        this.source = new CraftingGridSourceImpl(grid);
        this.player = playerInventory.player;
        addSlots(0);
    }

    public void setActivenessListener(@Nullable final Consumer<Boolean> activenessListener) {
        this.activenessListener = activenessListener;
    }

    @Override
    public void onActiveChanged(final boolean newActive) {
        super.onActiveChanged(newActive);
        if (activenessListener != null) {
            activenessListener.accept(newActive);
        }
    }

    @Override
    public boolean canTakeItemForPickAll(final ItemStack stack, final Slot slot) {
        return !(slot instanceof CraftingGridResultSlot);
    }

    @Override
    public ItemStack quickMoveStack(final Player actor, final int slotIndex) {
        final Slot slot = getSlot(slotIndex);
        if (!actor.getLevel().isClientSide() && slot instanceof CraftingGridResultSlot resultSlot) {
            final ItemStack craftedStack = resultSlot.onQuickCraft(actor);
            source.acceptQuickCraft(actor, craftedStack);
            return ItemStack.EMPTY;
        }
        return super.quickMoveStack(actor, slotIndex);
    }

    @Override
    public void addSlots(final int playerInventoryY) {
        super.addSlots(playerInventoryY);
        for (int y = 0; y < 3; ++y) {
            for (int x = 0; x < 3; ++x) {
                final int slotX = 26 + ((x % 3) * 18);
                final int slotY = playerInventoryY
                    - Y_OFFSET_BETWEEN_PLAYER_INVENTORY_AND_FIRST_CRAFTING_MATRIX_SLOT
                    + ((y % 3) * 18);
                addSlot(new Slot(source.getCraftingMatrix(), x + y * 3, slotX, slotY));
            }
        }
        addSlot(new CraftingGridResultSlot(
            player,
            source,
            130 + 4,
            playerInventoryY - Y_OFFSET_BETWEEN_PLAYER_INVENTORY_AND_FIRST_CRAFTING_MATRIX_SLOT + 18
        ));
    }

    public void clear(final boolean toPlayerInventory) {
        source.clearMatrix(player, toPlayerInventory);
    }

    public ResourceList<Object> getAvailableListForRecipeTransfer() {
        final ResourceList<Object> available = getView().copyBackingList();
        addContainerToList(source.getCraftingMatrix(), available);
        addContainerToList(player.getInventory(), available);
        return available;
    }

    private void addContainerToList(final Container container, final ResourceList<Object> available) {
        for (int i = 0; i < container.getContainerSize(); ++i) {
            final ItemStack stack = container.getItem(i);
            if (stack.isEmpty()) {
                continue;
            }
            available.add(ItemResource.ofItemStack(stack), stack.getCount());
        }
    }

    public void transferRecipe(final List<List<ItemResource>> recipe) {
        source.transferRecipe(player, recipe);
    }
}
