package com.refinedmods.refinedstorage.api.resource.repository;

import com.refinedmods.refinedstorage.api.resource.ResourceKey;

import org.apiguardian.api.API;

/**
 * Constructs a {@link ResourceRepository}, based on an initial set of resources.
 */
@API(status = API.Status.STABLE, since = "2.0.0-milestone.2.4")
public interface ResourceRepositoryBuilder<T> {
    /**
     * Adds a resource in the backing and view list.
     *
     * @param resource the resource
     * @param amount   the amount
     * @return this builder
     */
    ResourceRepositoryBuilder<T> addResource(ResourceKey resource, long amount);

    /**
     * Adds a resource into the view list and/or marks it as sticky so it's not removed when completely removed
     * from the backing list.
     *
     * @param resource the resource
     * @return this builder
     */
    ResourceRepositoryBuilder<T> addStickyResource(ResourceKey resource);

    /**
     * @return a {@link ResourceRepository} with the specified resources
     */
    ResourceRepository<T> build();
}
