package com.refinedmods.refinedstorage.api.network.node.grid;

import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.api.storage.tracked.TrackedResource;

import javax.annotation.Nullable;

import org.apiguardian.api.API;

/**
 * A grid listener.
 */
@API(status = API.Status.STABLE, since = "2.0.0-milestone.1.0")
public interface GridWatcher {
    /**
     * Called when the activeness state of the grid has changed.
     *
     * @param newActive the new activeness state
     */
    void onActiveChanged(boolean newActive);

    /**
     * Called when a resource is changed.
     *
     * @param resource        the resource
     * @param change          the changed amount
     * @param trackedResource the tracked resource, if present
     */
    void onChanged(ResourceKey resource, long change, @Nullable TrackedResource trackedResource);

    /**
     * Usually called when the grid network has been changed.
     */
    void invalidate();
}
