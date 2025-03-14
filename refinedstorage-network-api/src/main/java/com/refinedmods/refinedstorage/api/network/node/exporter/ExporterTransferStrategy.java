package com.refinedmods.refinedstorage.api.network.node.exporter;

import com.refinedmods.refinedstorage.api.network.Network;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.api.storage.Actor;

import org.apiguardian.api.API;

/**
 * A transfer strategy that transfers a resource from the network to a destination.
 */
@API(status = API.Status.STABLE, since = "2.0.0-milestone.2.4")
@FunctionalInterface
public interface ExporterTransferStrategy {
    Result transfer(ResourceKey resource, Actor actor, Network network);

    enum Result {
        DESTINATION_DOES_NOT_ACCEPT,
        EXPORTED,
        RESOURCE_MISSING,
        AUTOCRAFTING_STARTED,
        AUTOCRAFTING_MISSING_RESOURCES
    }
}
