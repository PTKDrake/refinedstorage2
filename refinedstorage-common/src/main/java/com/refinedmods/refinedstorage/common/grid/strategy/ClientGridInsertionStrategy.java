package com.refinedmods.refinedstorage.common.grid.strategy;

import com.refinedmods.refinedstorage.api.network.node.grid.GridInsertMode;
import com.refinedmods.refinedstorage.common.api.grid.strategy.GridInsertionStrategy;
import com.refinedmods.refinedstorage.common.support.packet.c2s.C2SPackets;

public class ClientGridInsertionStrategy implements GridInsertionStrategy {
    @Override
    public boolean onInsert(final GridInsertMode insertMode, final boolean tryAlternatives) {
        C2SPackets.sendGridInsert(insertMode, tryAlternatives);
        return true;
    }

    @Override
    public boolean onTransfer(final int slotIndex) {
        throw new UnsupportedOperationException();
    }
}
