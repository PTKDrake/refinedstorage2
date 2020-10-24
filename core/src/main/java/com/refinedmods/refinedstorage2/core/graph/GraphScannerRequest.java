package com.refinedmods.refinedstorage2.core.graph;

import com.refinedmods.refinedstorage2.core.adapter.WorldAdapter;
import net.minecraft.util.math.BlockPos;

class GraphScannerRequest {
    private final WorldAdapter worldAdapter;
    private final BlockPos pos;

    GraphScannerRequest(WorldAdapter worldAdapter, BlockPos pos) {
        this.worldAdapter = worldAdapter;
        this.pos = pos;
    }

    public WorldAdapter getWorldAdapter() {
        return worldAdapter;
    }

    public BlockPos getPos() {
        return pos;
    }
}
