package com.refinedmods.refinedstorage.api.network.impl.node.grid;

import com.refinedmods.refinedstorage.api.network.Network;
import com.refinedmods.refinedstorage.api.network.impl.node.AbstractNetworkNode;
import com.refinedmods.refinedstorage.api.network.node.grid.GridWatcher;
import com.refinedmods.refinedstorage.api.network.storage.StorageNetworkComponent;
import com.refinedmods.refinedstorage.api.storage.Actor;

import javax.annotation.Nullable;

import static java.util.Objects.requireNonNull;

public class GridNetworkNode extends AbstractNetworkNode {
    private final long energyUsage;
    private final GridWatcherManager watchers = new GridWatcherManagerImpl();

    public GridNetworkNode(final long energyUsage) {
        this.energyUsage = energyUsage;
    }

    @Override
    public long getEnergyUsage() {
        return energyUsage;
    }

    public void addWatcher(final GridWatcher watcher, final Class<? extends Actor> actorType) {
        watchers.addWatcher(watcher, actorType, requireNonNull(network).getComponent(StorageNetworkComponent.class));
    }

    public void removeWatcher(final GridWatcher watcher) {
        watchers.removeWatcher(watcher, requireNonNull(network).getComponent(StorageNetworkComponent.class));
    }

    @Override
    protected void onActiveChanged(final boolean newActive) {
        super.onActiveChanged(newActive);
        watchers.activeChanged(newActive);
    }

    @Override
    public void setNetwork(@Nullable final Network network) {
        if (this.network != null) {
            watchers.detachAll(this.network.getComponent(StorageNetworkComponent.class));
        }
        super.setNetwork(network);
        if (this.network != null) {
            watchers.attachAll(this.network.getComponent(StorageNetworkComponent.class));
        }
    }
}
