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

import org.apiguardian.api.API;

@API(status = API.Status.STABLE, since = "2.0.0-milestone.2.1")
public class ImporterTransferStrategyImpl implements ImporterTransferStrategy {
    private final ImporterSource source;
    private final long transferQuota;

    public ImporterTransferStrategyImpl(final ImporterSource source, final long transferQuota) {
        this.source = source;
        this.transferQuota = transferQuota;
    }

    @Override
    public boolean transfer(final Filter filter, final Actor actor, final Network network) {
        final RootStorage rootStorage = network.getComponent(StorageNetworkComponent.class);
        return transfer(filter, actor, rootStorage);
    }

    private boolean transfer(final Filter filter, final Actor actor, final RootStorage rootStorage) {
        long totalTransferred = 0;
        ResourceKey workingResource = null;
        final Iterator<ResourceKey> iterator = source.getResources();
        while (iterator.hasNext() && totalTransferred < transferQuota) {
            final ResourceKey resource = iterator.next();
            if (workingResource != null) {
                totalTransferred += performTransfer(rootStorage, actor, totalTransferred, workingResource, resource);
            } else if (filter.isAllowed(resource)) {
                final long transferred = performTransfer(rootStorage, actor, totalTransferred, resource);
                if (transferred > 0) {
                    workingResource = resource;
                }
                totalTransferred += transferred;
            }
        }
        return totalTransferred > 0;
    }

    private long performTransfer(final RootStorage rootStorage,
                                 final Actor actor,
                                 final long totalTransferred,
                                 final ResourceKey workingResource,
                                 final ResourceKey resource) {
        if (Objects.equals(workingResource, resource)) {
            return performTransfer(rootStorage, actor, totalTransferred, resource);
        }
        return 0L;
    }

    private long performTransfer(final RootStorage rootStorage,
                                 final Actor actor,
                                 final long totalTransferred,
                                 final ResourceKey resource) {
        return TransferHelper.transfer(
            resource,
            transferQuota - totalTransferred,
            actor,
            source,
            rootStorage,
            source
        );
    }
}
