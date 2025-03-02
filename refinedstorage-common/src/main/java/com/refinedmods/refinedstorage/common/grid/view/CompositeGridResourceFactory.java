package com.refinedmods.refinedstorage.common.grid.view;

import com.refinedmods.refinedstorage.api.grid.view.GridResourceFactory;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.common.api.grid.view.PlatformGridResource;
import com.refinedmods.refinedstorage.common.support.resource.FluidResource;
import com.refinedmods.refinedstorage.common.support.resource.ItemResource;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

import static java.util.Objects.requireNonNull;

public class CompositeGridResourceFactory implements GridResourceFactory<PlatformGridResource> {
    private final Map<Class<? extends ResourceKey>, GridResourceFactory<PlatformGridResource>> strategies =
        new HashMap<>();
    @Nullable
    private GridResourceFactory<PlatformGridResource> itemFactory;
    @Nullable
    private GridResourceFactory<PlatformGridResource> fluidFactory;

    public void addFactory(final Class<? extends ResourceKey> resourceClass,
                           final GridResourceFactory<PlatformGridResource> factory) {
        if (resourceClass == ItemResource.class) {
            this.itemFactory = factory;
        } else if (resourceClass == FluidResource.class) {
            this.fluidFactory = factory;
        } else {
            this.strategies.put(resourceClass, factory);
        }
    }

    @Override
    public PlatformGridResource apply(final ResourceKey resource) {
        final Class<? extends ResourceKey> resourceClass = resource.getClass();
        if (resourceClass == ItemResource.class && itemFactory != null) {
            return itemFactory.apply(resource);
        } else if (resourceClass == FluidResource.class && fluidFactory != null) {
            return fluidFactory.apply(resource);
        }
        final GridResourceFactory<PlatformGridResource> factory = requireNonNull(
            strategies.get(resourceClass),
            "No factory for " + resourceClass
        );
        return factory.apply(resource);
    }
}
