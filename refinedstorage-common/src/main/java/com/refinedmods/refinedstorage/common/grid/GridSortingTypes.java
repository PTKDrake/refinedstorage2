package com.refinedmods.refinedstorage.common.grid;

import com.refinedmods.refinedstorage.api.grid.view.GridSortingType;
import com.refinedmods.refinedstorage.api.grid.view.GridView;
import com.refinedmods.refinedstorage.api.storage.tracked.TrackedResource;
import com.refinedmods.refinedstorage.common.api.grid.view.GridResource;

import java.util.Comparator;
import java.util.function.Function;

public enum GridSortingTypes implements GridSortingType<GridResource> {
    QUANTITY(view -> Comparator.comparingLong(value -> value.getAmount(view))),
    NAME(view -> Comparator.comparing(GridResource::getName)),
    ID(view -> (a, b) -> {
        if (a instanceof GridResource aa && b instanceof GridResource bb) {
            return Integer.compare(aa.getRegistryId(), bb.getRegistryId());
        }
        return 0;
    }),
    LAST_MODIFIED(view -> (a, b) -> {
        final long lastModifiedA = a.getTrackedResource(view).map(TrackedResource::getTime).orElse(0L);
        final long lastModifiedB = b.getTrackedResource(view).map(TrackedResource::getTime).orElse(0L);
        return Long.compare(lastModifiedA, lastModifiedB);
    });

    private final Function<GridView<GridResource>, Comparator<GridResource>> comparator;

    GridSortingTypes(final Function<GridView<GridResource>, Comparator<GridResource>> comparator) {
        this.comparator = comparator;
    }

    @Override
    public Comparator<GridResource> apply(final GridView<GridResource> view) {
        return comparator.apply(view);
    }
}
