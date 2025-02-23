package com.refinedmods.refinedstorage.api.network.impl.node.importer;

import com.refinedmods.refinedstorage.api.network.Network;
import com.refinedmods.refinedstorage.api.network.node.importer.ImporterTransferStrategy;
import com.refinedmods.refinedstorage.api.network.storage.StorageNetworkComponent;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.api.resource.filter.Filter;
import com.refinedmods.refinedstorage.api.storage.Actor;
import com.refinedmods.refinedstorage.api.storage.TransferHelper;
import com.refinedmods.refinedstorage.api.storage.root.RootStorage;

import java.util.Iterator;
import java.util.Objects;
import java.util.function.ToLongFunction;

import org.apiguardian.api.API;

@API(status = API.Status.STABLE, since = "2.0.0-milestone.2.1")
public class ImporterTransferStrategyImpl implements ImporterTransferStrategy {
    private final ImporterSource source;
    private final ToLongFunction<ResourceKey> transferQuotaProvider;

    public ImporterTransferStrategyImpl(final ImporterSource source,
                                        final ToLongFunction<ResourceKey> transferQuotaProvider) {
        this.source = source;
        this.transferQuotaProvider = transferQuotaProvider;
    }

    @Override
    public boolean transfer(final Filter filter, final Actor actor, final Network network) {
        final RootStorage rootStorage = network.getComponent(StorageNetworkComponent.class);
        return transfer(filter, actor, rootStorage);
    }

    private boolean transfer(final Filter filter, final Actor actor, final RootStorage rootStorage) {
        long total = 0;
        long transferQuota = 0;
        ResourceKey workingResource = null;
        final Iterator<ResourceKey> iterator = source.getResources();
        while (iterator.hasNext() && (total < transferQuota || transferQuota == 0)) {
            final ResourceKey resource = iterator.next();
            if (workingResource != null) {
                total += transfer(rootStorage, actor, transferQuota, total, workingResource, resource);
            } else if (filter.isAllowed(resource)) {
                transferQuota = transferQuotaProvider.applyAsLong(resource);
                final long transferred = transferQuota > 0
                    ? transfer(rootStorage, actor, transferQuota, total, resource)
                    : 0;
                if (transferred > 0) {
                    workingResource = resource;
                }
                total += transferred;
            }
        }
        return total > 0;
    }

    private long transfer(final RootStorage rootStorage,
                          final Actor actor,
                          final long transferQuota,
                          final long total,
                          final ResourceKey workingResource,
                          final ResourceKey resource) {
        return Objects.equals(workingResource, resource)
            ? transfer(rootStorage, actor, transferQuota, total, resource)
            : 0L;
    }

    private long transfer(final RootStorage rootStorage,
                          final Actor actor,
                          final long transferQuota,
                          final long total,
                          final ResourceKey resource) {
        return TransferHelper.transfer(
            resource,
            transferQuota - total,
            actor,
            source,
            rootStorage,
            source
        );
    }
}
