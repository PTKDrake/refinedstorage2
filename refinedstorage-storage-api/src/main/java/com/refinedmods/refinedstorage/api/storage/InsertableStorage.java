package com.refinedmods.refinedstorage.api.storage;

import com.refinedmods.refinedstorage.api.core.Action;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;

import org.apiguardian.api.API;

/**
 * Represents a storage that can be inserted into.
 */
@API(status = API.Status.STABLE, since = "2.0.0-milestone.1.2")
public interface InsertableStorage {
    /**
     * Inserts a resource into a storage.
     *
     * @param resource the resource, may not be null
     * @param amount   the amount, must be larger than 0
     * @param action   the mode of insertion
     * @param actor    the source
     * @return the amount inserted
     */
    long insert(ResourceKey resource, long amount, Action action, Actor actor);
}
