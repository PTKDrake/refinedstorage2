package com.refinedmods.refinedstorage.api.storage.composite;

import com.refinedmods.refinedstorage.api.storage.Storage;
import com.refinedmods.refinedstorage.api.storage.tracked.TrackedStorage;

import java.util.List;

import org.apiguardian.api.API;

/**
 * This represents a single storage that can be backed by multiple storages.
 */
@API(status = API.Status.STABLE, since = "2.0.0-milestone.1.0")
public interface CompositeStorage extends Storage, TrackedStorage {
    /**
     * Sorts storages that implement {@link PriorityProvider}.
     */
    void sortSources();

    /**
     * Adds a source and resorts them.
     *
     * @param source the source
     */
    void addSource(Storage source);

    /**
     * Removes a source and resorts them.
     *
     * @param source the source
     */
    void removeSource(Storage source);

    /**
     * @return an unmodifiable source list
     */
    List<Storage> getSources();

    /**
     * Clears all sources.
     */
    void clearSources();
}
