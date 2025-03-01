package com.refinedmods.refinedstorage.api.grid.view;

import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.api.resource.list.ResourceList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;

class ViewList {
    private final List<GridResource> list = new ArrayList<>();
    private final List<GridResource> listView = Collections.unmodifiableList(list);
    private final Map<ResourceKey, GridResource> index = new HashMap<>();

    @Nullable
    GridResource get(final ResourceKey resource) {
        return index.get(resource);
    }

    void remove(final ResourceKey resource, final GridResource gridResource) {
        list.remove(gridResource);
        index.remove(resource);
    }

    void add(final ResourceKey resource, final GridResource gridResource, final Comparator<GridResource> comparator) {
        index.put(resource, gridResource);
        add(gridResource, comparator);
    }

    private void add(final GridResource gridResource, final Comparator<GridResource> comparator) {
        // Calculate the position according to sorting rules.
        final int wouldBePosition = Collections.binarySearch(list, gridResource, comparator);
        // Most of the time, the "would be" position is negative, indicating that the resource wasn't found yet in the
        // list, comparing with sorting rules. The absolute of this position would be the "real" position if sorted.
        if (wouldBePosition < 0) {
            list.add(-wouldBePosition - 1, gridResource);
        } else {
            // If the "would-be" position is positive, this means that the resource is already contained in the list,
            // comparing with sorting rules.
            // This doesn't mean that the *exact* resource is already in the list, but that is purely "contained"
            // in the list when comparing with sorting rules.
            // For example: a resource with different identity but the same name (in Minecraft: an enchanted book
            // with different NBT).
            // In that case, just insert it after the "existing" resource.
            list.add(wouldBePosition + 1, gridResource);
        }
    }

    void update(final GridResource gridResource, final Comparator<GridResource> comparator) {
        list.remove(gridResource);
        add(gridResource, comparator);
    }

    List<GridResource> getListView() {
        return listView;
    }

    void clear() {
        index.clear();
        list.clear();
    }

    static ViewList create(final ViewList existingList,
                           final ResourceList source,
                           final Set<ResourceKey> autocraftableResources,
                           final Comparator<GridResource> comparator,
                           final GridResourceFactory resourceFactory,
                           final Predicate<GridResource> filter) {
        final ViewList newList = new ViewList();
        for (final ResourceKey resource : source.getAll()) {
            tryAdd(resource, existingList, newList, autocraftableResources.contains(resource), resourceFactory, filter);
        }
        for (final ResourceKey autocraftableResource : autocraftableResources) {
            if (!newList.index.containsKey(autocraftableResource)) {
                tryAdd(autocraftableResource, existingList, newList, true, resourceFactory, filter);
            }
        }
        newList.list.sort(comparator);
        return newList;
    }

    private static void tryAdd(final ResourceKey resource,
                               final ViewList existingList,
                               final ViewList newList,
                               final boolean autocraftable,
                               final GridResourceFactory resourceFactory,
                               final Predicate<GridResource> filter) {
        final GridResource existing = existingList.get(resource);
        if (existing != null) {
            tryAdd(existing, newList, resource, filter);
        } else {
            final GridResource newGridResource = resourceFactory.apply(resource, autocraftable);
            tryAdd(newGridResource, newList, resource, filter);
        }
    }

    private static void tryAdd(final GridResource gridResource,
                               final ViewList newList,
                               final ResourceKey resource,
                               final Predicate<GridResource> filter) {
        if (filter.test(gridResource)) {
            newList.list.add(gridResource);
            newList.index.put(resource, gridResource);
        }
    }
}
