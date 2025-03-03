package com.refinedmods.refinedstorage.common.grid.strategy;

import com.refinedmods.refinedstorage.api.network.node.grid.GridExtractMode;
import com.refinedmods.refinedstorage.common.api.grid.strategy.GridExtractionStrategy;
import com.refinedmods.refinedstorage.common.api.support.resource.PlatformResourceKey;

import java.util.Collections;
import java.util.List;

public class CompositeGridExtractionStrategy implements GridExtractionStrategy {
    private final List<GridExtractionStrategy> strategies;

    public CompositeGridExtractionStrategy(final List<GridExtractionStrategy> strategies) {
        this.strategies = Collections.unmodifiableList(strategies);
    }

    @Override
    public boolean onExtract(final PlatformResourceKey resource,
                             final GridExtractMode extractMode,
                             final boolean cursor) {
        for (final GridExtractionStrategy strategy : strategies) {
            if (strategy.onExtract(resource, extractMode, cursor)) {
                return true;
            }
        }
        return false;
    }
}
