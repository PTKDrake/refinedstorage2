package com.refinedmods.refinedstorage.api.network.impl.node.exporter;

import com.refinedmods.refinedstorage.api.core.Action;
import com.refinedmods.refinedstorage.api.network.Network;
import com.refinedmods.refinedstorage.api.network.node.exporter.ExporterTransferStrategy;
import com.refinedmods.refinedstorage.api.network.storage.StorageNetworkComponent;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.api.storage.Actor;
import com.refinedmods.refinedstorage.api.storage.InsertableStorage;
import com.refinedmods.refinedstorage.api.storage.TransferHelper;
import com.refinedmods.refinedstorage.api.storage.root.RootStorage;

import java.util.Collection;
import java.util.Collections;
import java.util.function.BiFunction;

public class ExporterTransferStrategyImpl implements ExporterTransferStrategy {
    private final InsertableStorage destination;
    private final long transferQuota;
    private final BiFunction<RootStorage, ResourceKey, Collection<ResourceKey>> expander;

    public ExporterTransferStrategyImpl(final InsertableStorage destination, final long transferQuota) {
        this(destination, transferQuota, (rootStorage, resource) -> Collections.singletonList(resource));
    }

    public ExporterTransferStrategyImpl(final InsertableStorage destination,
                                        final long transferQuota,
                                        final BiFunction<RootStorage, ResourceKey, Collection<ResourceKey>> expander) {
        this.destination = destination;
        this.transferQuota = transferQuota;
        this.expander = expander;
    }

    @Override
    public Result transfer(final ResourceKey resource, final Actor actor, final Network network) {
        final RootStorage rootStorage = network.getComponent(StorageNetworkComponent.class);
        for (final ResourceKey expandedResource : expander.apply(rootStorage, resource)) {
            final long extractedSimulated = rootStorage
                .extract(expandedResource, transferQuota, Action.SIMULATE, actor);
            if (extractedSimulated == 0) {
                continue;
            }
            final long insertedSimulated = destination
                .insert(expandedResource, extractedSimulated, Action.SIMULATE, actor);
            if (insertedSimulated == 0) {
                return Result.DESTINATION_DOES_NOT_ACCEPT;
            }
            final long extracted = rootStorage.extract(expandedResource, insertedSimulated, Action.EXECUTE, actor);
            if (extracted == 0) {
                continue;
            }
            final long inserted = destination.insert(expandedResource, extracted, Action.EXECUTE, actor);
            final long leftover = extracted - inserted;
            if (leftover > 0) {
                TransferHelper.handleLeftover(expandedResource, actor, rootStorage, leftover);
            }
            return Result.EXPORTED;
        }
        return Result.RESOURCE_MISSING;
    }
}
