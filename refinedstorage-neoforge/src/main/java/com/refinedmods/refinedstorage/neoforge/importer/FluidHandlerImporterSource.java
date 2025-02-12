package com.refinedmods.refinedstorage.neoforge.importer;

import com.refinedmods.refinedstorage.api.core.Action;
import com.refinedmods.refinedstorage.api.network.impl.node.importer.ImporterSource;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.api.storage.Actor;
import com.refinedmods.refinedstorage.neoforge.storage.CapabilityCache;
import com.refinedmods.refinedstorage.neoforge.storage.FluidHandlerExtractableStorage;
import com.refinedmods.refinedstorage.neoforge.storage.FluidHandlerInsertableStorage;

import java.util.Iterator;

class FluidHandlerImporterSource implements ImporterSource {
    private final CapabilityCache capabilityCache;
    private final FluidHandlerInsertableStorage insertTarget;
    private final FluidHandlerExtractableStorage extractTarget;

    FluidHandlerImporterSource(final CapabilityCache capabilityCache) {
        this.capabilityCache = capabilityCache;
        this.insertTarget = new FluidHandlerInsertableStorage(capabilityCache);
        this.extractTarget = new FluidHandlerExtractableStorage(capabilityCache);
    }

    public long getAmount(final ResourceKey resource) {
        return extractTarget.getAmount(resource);
    }

    @Override
    public Iterator<ResourceKey> getResources() {
        return capabilityCache.getFluidIterator();
    }

    @Override
    public long extract(final ResourceKey resource, final long amount, final Action action, final Actor actor) {
        return extractTarget.extract(resource, amount, action, actor);
    }

    @Override
    public long insert(final ResourceKey resource, final long amount, final Action action, final Actor actor) {
        return insertTarget.insert(resource, amount, action, actor);
    }
}
