package com.refinedmods.refinedstorage.neoforge.storage;

import com.refinedmods.refinedstorage.api.core.Action;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.api.storage.Actor;
import com.refinedmods.refinedstorage.api.storage.ExtractableStorage;
import com.refinedmods.refinedstorage.common.support.resource.ItemResource;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;

public class ItemHandlerExtractableStorage implements ExtractableStorage {
    private final CapabilityCache capabilityCache;

    public ItemHandlerExtractableStorage(final CapabilityCache capabilityCache) {
        this.capabilityCache = capabilityCache;
    }

    public long getAmount(final ResourceKey resource) {
        if (!(resource instanceof ItemResource itemResource)) {
            return 0;
        }
        return capabilityCache.getItemHandler()
            .map(itemHandler -> ForgeHandlerUtil.getCurrentAmount(itemHandler, itemResource.toItemStack()))
            .orElse(0L);
    }

    @Override
    public long extract(final ResourceKey resource, final long amount, final Action action, final Actor actor) {
        if (!(resource instanceof ItemResource itemResource)) {
            return 0L;
        }
        return capabilityCache.getItemHandler()
            .map(itemHandler -> extract(amount, action, itemHandler, itemResource.toItemStack(amount)))
            .orElse(0L);
    }

    private long extract(final long amount,
                         final Action action,
                         final IItemHandler itemHandler,
                         final ItemStack stack) {
        long extracted = 0;
        for (int slot = 0; slot < itemHandler.getSlots(); ++slot) {
            final boolean relevant = ItemStack.isSameItemSameComponents(
                itemHandler.getStackInSlot(slot),
                stack
            );
            if (!relevant) {
                continue;
            }
            final long toExtract = amount - extracted;
            extracted += itemHandler.extractItem(slot, (int) toExtract, action == Action.SIMULATE).getCount();
            if (extracted >= amount) {
                break;
            }
        }
        return extracted;
    }
}
