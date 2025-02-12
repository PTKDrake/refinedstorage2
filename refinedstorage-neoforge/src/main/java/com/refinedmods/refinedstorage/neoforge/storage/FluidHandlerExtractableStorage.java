package com.refinedmods.refinedstorage.neoforge.storage;

import com.refinedmods.refinedstorage.api.core.Action;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.api.storage.Actor;
import com.refinedmods.refinedstorage.api.storage.ExtractableStorage;
import com.refinedmods.refinedstorage.common.support.resource.FluidResource;

import net.neoforged.neoforge.fluids.FluidStack;

import static com.refinedmods.refinedstorage.neoforge.support.resource.VariantUtil.toFluidAction;
import static com.refinedmods.refinedstorage.neoforge.support.resource.VariantUtil.toFluidStack;

public class FluidHandlerExtractableStorage implements ExtractableStorage {
    private final CapabilityCache capabilityCache;

    public FluidHandlerExtractableStorage(final CapabilityCache capabilityCache) {
        this.capabilityCache = capabilityCache;
    }

    public long getAmount(final ResourceKey resource) {
        if (!(resource instanceof FluidResource fluidResource)) {
            return 0;
        }
        return capabilityCache.getFluidHandler()
            .map(fluidHandler -> ForgeHandlerUtil.getCurrentAmount(fluidHandler, fluidResource))
            .orElse(0L);
    }

    @Override
    public long extract(final ResourceKey resource, final long amount, final Action action, final Actor actor) {
        if (!(resource instanceof FluidResource fluidResource)) {
            return 0;
        }
        return capabilityCache.getFluidHandler().map(fluidHandler -> {
            final FluidStack stack = toFluidStack(fluidResource, amount);
            return (long) fluidHandler.drain(stack, toFluidAction(action)).getAmount();
        }).orElse(0L);
    }
}
