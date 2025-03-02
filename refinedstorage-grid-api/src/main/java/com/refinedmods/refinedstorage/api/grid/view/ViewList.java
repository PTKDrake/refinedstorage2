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

class ViewList<T> {
    private final List<T> list = new ArrayList<>();
    private final List<T> listView = Collections.unmodifiableList(list);
    private final Map<ResourceKey, T> index = new HashMap<>();

    @Nullable
    T get(final ResourceKey resource) {
        return index.get(resource);
    }

    void remove(final ResourceKey resource, final T gridResource) {
        list.remove(gridResource);
        index.remove(resource);
    }

    void add(final ResourceKey resource, final T gridResource, final Comparator<T> comparator) {
        index.put(resource, gridResource);
        add(gridResource, comparator);
    }

    private void add(final T gridResource, final Comparator<T> comparator) {
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

    void update(final T gridResource, final Comparator<T> comparator) {
        list.remove(gridResource);
        add(gridResource, comparator);
    }

    List<T> getListView() {
        return listView;
    }

    void clear() {
        index.clear();
        list.clear();
    }

    static <T> ViewList<T> create(final ViewList<T> existingList,
                                  final ResourceList source,
                                  final Set<ResourceKey> autocraftableResources,
                                  final Comparator<T> comparator,
                                  final GridResourceFactory<T> resourceFactory,
                                  final Predicate<T> filter) {
        final ViewList<T> newList = new ViewList<>();
        for (final ResourceKey resource : source.getAll()) {
            tryAdd(resource, existingList, newList, resourceFactory, filter);
        }
        for (final ResourceKey autocraftableResource : autocraftableResources) {
            if (!newList.index.containsKey(autocraftableResource)) {
                tryAdd(autocraftableResource, existingList, newList, resourceFactory, filter);
            }
        }
        newList.list.sort(comparator);
        return newList;
    }

    private static <T> void tryAdd(final ResourceKey resource,
                                   final ViewList<T> existingList,
                                   final ViewList<T> newList,
                                   final GridResourceFactory<T> resourceFactory,
                                   final Predicate<T> filter) {
        final T existing = existingList.get(resource);
        if (existing != null) {
            tryAdd(existing, newList, resource, filter);
        } else {
            final T newGridResource = resourceFactory.apply(resource);
            tryAdd(newGridResource, newList, resource, filter);
        }
    }

    private static <T> void tryAdd(final T gridResource,
                                   final ViewList<T> newList,
                                   final ResourceKey resource,
                                   final Predicate<T> filter) {
        if (filter.test(gridResource)) {
            newList.list.add(gridResource);
            newList.index.put(resource, gridResource);
        }
    }
}
