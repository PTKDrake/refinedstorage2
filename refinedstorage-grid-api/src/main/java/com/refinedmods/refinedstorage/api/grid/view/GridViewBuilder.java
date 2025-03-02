package com.refinedmods.refinedstorage.api.grid.view;

import com.refinedmods.refinedstorage.api.resource.ResourceKey;

import org.apiguardian.api.API;

/**
 * Constructs a grid view, based on an initial set of resources.
 */
@API(status = API.Status.STABLE, since = "2.0.0-milestone.2.4")
public interface GridViewBuilder<T> {
    /**
     * Adds a resource in the backing and view list.
     *
     * @param resource the resource
     * @param amount   the amount
     * @return this builder
     */
    GridViewBuilder<T> withResource(ResourceKey resource, long amount);

    /**
     * Adds a resource into the view and marks it as autocraftable.
     *
     * @param resource the resource
     * @return this builder
     */
    GridViewBuilder<T> withAutocraftableResource(ResourceKey resource);

    /**
     * @return a {@link GridView} with the specified resources
     */
    GridView<T> build();
}
