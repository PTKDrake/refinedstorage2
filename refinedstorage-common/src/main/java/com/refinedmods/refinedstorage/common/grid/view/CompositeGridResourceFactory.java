package com.refinedmods.refinedstorage.common.grid.view;

import com.refinedmods.refinedstorage.api.grid.view.GridResource;
import com.refinedmods.refinedstorage.api.grid.view.GridResourceFactory;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.common.support.resource.FluidResource;
import com.refinedmods.refinedstorage.common.support.resource.ItemResource;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

import static java.util.Objects.requireNonNull;

public class CompositeGridResourceFactory implements GridResourceFactory {
    private final Map<Class<? extends ResourceKey>, GridResourceFactory> strategies = new HashMap<>();
    @Nullable
    private GridResourceFactory itemFactory;
    @Nullable
    private GridResourceFactory fluidFactory;

    public void addFactory(final Class<? extends ResourceKey> resourceClass, final GridResourceFactory factory) {
        if (resourceClass == ItemResource.class) {
            this.itemFactory = factory;
        } else if (resourceClass == FluidResource.class) {
            this.fluidFactory = factory;
        } else {
            this.strategies.put(resourceClass, factory);
        }
    }

    @Override
    public GridResource apply(final ResourceKey resource, final boolean autocraftable) {
        final Class<? extends ResourceKey> resourceClass = resource.getClass();
        if (resourceClass == ItemResource.class && itemFactory != null) {
            return itemFactory.apply(resource, autocraftable);
        } else if (resourceClass == FluidResource.class && fluidFactory != null) {
            return fluidFactory.apply(resource, autocraftable);
        }
        final GridResourceFactory factory = requireNonNull(
            strategies.get(resourceClass),
            "No factory for " + resourceClass
        );
        return factory.apply(resource, autocraftable);
    }
}
