package com.refinedmods.refinedstorage.common.api.grid.strategy;

import com.refinedmods.refinedstorage.api.network.node.grid.GridInsertMode;

import org.apiguardian.api.API;

@API(status = API.Status.STABLE, since = "2.0.0-milestone.2.6")
public interface GridInsertionStrategy {
    boolean onInsert(GridInsertMode insertMode, boolean tryAlternatives);

    boolean onTransfer(int slotIndex);
}
