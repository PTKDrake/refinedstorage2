package com.refinedmods.refinedstorage.api.network.impl.node.importer;

import com.refinedmods.refinedstorage.api.network.Network;
import com.refinedmods.refinedstorage.api.network.node.importer.ImporterTransferStrategy;
import com.refinedmods.refinedstorage.api.resource.filter.Filter;
import com.refinedmods.refinedstorage.api.storage.Actor;

import java.util.List;

public class CompositeImporterTransferStrategy implements ImporterTransferStrategy {
    private final List<ImporterTransferStrategy> strategies;

    public CompositeImporterTransferStrategy(final List<ImporterTransferStrategy> strategies) {
        this.strategies = strategies;
    }

    @Override
    public boolean transfer(final Filter filter, final Actor actor, final Network network) {
        for (final ImporterTransferStrategy strategy : strategies) {
            if (strategy.transfer(filter, actor, network)) {
                return true;
            }
        }
        return false;
    }
}
