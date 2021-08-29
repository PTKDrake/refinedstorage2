package com.refinedmods.refinedstorage2.api.stack.test;

import com.refinedmods.refinedstorage2.api.stack.item.Rs2Item;

public record ItemStub(int id, int maxAmount, String identifier) implements Rs2Item {
    @Override
    public int getMaxAmount() {
        return maxAmount;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String toString() {
        return identifier;
    }
}
