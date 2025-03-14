package com.refinedmods.refinedstorage.common.api.grid.strategy;

import com.refinedmods.refinedstorage.api.network.node.grid.GridExtractMode;
import com.refinedmods.refinedstorage.common.api.support.resource.PlatformResourceKey;

import org.apiguardian.api.API;

@API(status = API.Status.STABLE, since = "2.0.0-milestone.2.6")
@FunctionalInterface
public interface GridExtractionStrategy {
    boolean onExtract(PlatformResourceKey resource, GridExtractMode extractMode, boolean cursor);
}
