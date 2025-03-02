package com.refinedmods.refinedstorage.common.grid;

import com.refinedmods.refinedstorage.api.grid.view.GridView;
import com.refinedmods.refinedstorage.api.storage.tracked.TrackedResource;
import com.refinedmods.refinedstorage.common.api.grid.view.GridResource;

import java.util.Comparator;
import java.util.function.Function;
import javax.annotation.Nullable;

public enum GridSortingTypes {
    QUANTITY(trp -> view -> Comparator.comparingLong(value -> value.getAmount(view))),
    NAME(trp -> view -> Comparator.comparing(GridResource::getName)),
    ID(trp -> view -> (a, b) -> {
        if (a instanceof GridResource aa && b instanceof GridResource bb) {
            return Integer.compare(aa.getRegistryId(), bb.getRegistryId());
        }
        return 0;
    }),
    LAST_MODIFIED(trp -> view -> (a, b) -> {
        final long lastModifiedA = extractTime(trp.getTrackedResource(a));
        final long lastModifiedB = extractTime(trp.getTrackedResource(b));
        return Long.compare(lastModifiedA, lastModifiedB);
    });

    private final Function<TrackedResourceProvider, Function<GridView<GridResource>, Comparator<GridResource>>>
        comparator;

    GridSortingTypes(
        final Function<TrackedResourceProvider, Function<GridView<GridResource>, Comparator<GridResource>>> comparator
    ) {
        this.comparator = comparator;
    }

    public Function<GridView<GridResource>, Comparator<GridResource>> apply(final TrackedResourceProvider context) {
        return comparator.apply(context);
    }

    @FunctionalInterface
    public interface TrackedResourceProvider {
        @Nullable
        TrackedResource getTrackedResource(GridResource resource);
    }

    private static long extractTime(@Nullable final TrackedResource trackedResource) {
        return trackedResource != null ? trackedResource.getTime() : 0;
    }
}
