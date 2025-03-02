package com.refinedmods.refinedstorage.api.grid.view;

import com.refinedmods.refinedstorage.api.resource.ResourceKey;

import org.apiguardian.api.API;

/**
 * Transforms resources into T.
 */
@FunctionalInterface
@API(status = API.Status.STABLE, since = "2.0.0-milestone.2.6")
public interface GridResourceFactory<T> {
    /**
     * Transforms a {@link com.refinedmods.refinedstorage.api.resource.ResourceKey} into a {@link T}.
     *
     * @param resource the resource
     * @return the grid resource
     */
    T apply(ResourceKey resource);
}
