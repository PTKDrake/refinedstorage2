package com.refinedmods.refinedstorage.neoforge.storage;

import com.refinedmods.refinedstorage.api.core.Action;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.api.storage.Actor;
import com.refinedmods.refinedstorage.api.storage.InsertableStorage;
import com.refinedmods.refinedstorage.common.support.resource.ItemResource;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;

public class ItemHandlerInsertableStorage implements InsertableStorage {
    private final CapabilityCache capabilityCache;

    public ItemHandlerInsertableStorage(final CapabilityCache capabilityCache) {
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
    public long insert(final ResourceKey resource, final long amount, final Action action, final Actor actor) {
        if (!(resource instanceof ItemResource itemResource)) {
            return 0L;
        }
        return capabilityCache.getItemHandler()
            .map(itemHandler -> insert(itemResource, amount, action, itemHandler))
            .orElse(0L);
    }

    private long insert(final ItemResource resource,
                        final long amount,
                        final Action action,
                        final IItemHandler itemHandler) {
        final ItemStack stack = resource.toItemStack(amount);
        final ItemStack remainder = ItemHandlerHelper.insertItem(
            itemHandler,
            stack,
            action == Action.SIMULATE
        );
        return amount - remainder.getCount();
    }
}
