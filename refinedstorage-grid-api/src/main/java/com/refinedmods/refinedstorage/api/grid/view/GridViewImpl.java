package com.refinedmods.refinedstorage.api.grid.view;

import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.api.resource.list.MutableResourceList;
import com.refinedmods.refinedstorage.api.storage.tracked.TrackedResource;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiPredicate;
import javax.annotation.Nullable;

import org.apiguardian.api.API;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@API(status = API.Status.STABLE, since = "2.0.0-milestone.1.0")
public class GridViewImpl implements GridView {
    private static final Logger LOGGER = LoggerFactory.getLogger(GridViewImpl.class);

    private final MutableResourceList backingList;
    private final Comparator<GridResource> identitySort;
    private final GridResourceFactory resourceFactory;
    private final Map<ResourceKey, TrackedResource> trackedResources = new HashMap<>();
    private final Set<ResourceKey> autocraftableResources;

    private ViewList viewList = new ViewList();
    private GridSortingType sortingType;
    private GridSortingDirection sortingDirection = GridSortingDirection.ASCENDING;
    private BiPredicate<GridView, GridResource> filter = (view, resource) -> true;
    @Nullable
    private Runnable listener;
    private boolean preventSorting;

    /**
     * @param resourceFactory         a factory that transforms a resource amount to a grid resource
     * @param backingList             the backing list
     * @param initialTrackedResources initial tracked resources state
     * @param identitySortingType     a sorting type required to keep a consistent sorting order with quantity sorting
     * @param defaultSortingType      the default sorting type
     * @param autocraftableResources  resources which are autocraftable and must stay in the view list
     */
    public GridViewImpl(final GridResourceFactory resourceFactory,
                        final MutableResourceList backingList,
                        final Map<ResourceKey, TrackedResource> initialTrackedResources,
                        final Set<ResourceKey> autocraftableResources,
                        final GridSortingType identitySortingType,
                        final GridSortingType defaultSortingType) {
        this.resourceFactory = resourceFactory;
        this.identitySort = identitySortingType.apply(this);
        this.sortingType = defaultSortingType;
        this.backingList = backingList;
        this.trackedResources.putAll(initialTrackedResources);
        this.autocraftableResources = autocraftableResources;
    }

    @Override
    public void setListener(@Nullable final Runnable listener) {
        this.listener = listener;
    }

    @Override
    public void setSortingType(final GridSortingType sortingType) {
        this.sortingType = sortingType;
    }

    @Override
    public BiPredicate<GridView, GridResource> setFilterAndSort(final BiPredicate<GridView, GridResource> predicate) {
        final BiPredicate<GridView, GridResource> previousPredicate = filter;
        this.filter = predicate;
        sort();
        return previousPredicate;
    }

    @Override
    public boolean setPreventSorting(final boolean changedPreventSorting) {
        final boolean changed = preventSorting != changedPreventSorting;
        this.preventSorting = changedPreventSorting;
        return changed;
    }

    @Override
    public void setSortingDirection(final GridSortingDirection sortingDirection) {
        this.sortingDirection = sortingDirection;
    }

    @Override
    public Optional<TrackedResource> getTrackedResource(final ResourceKey resource) {
        return Optional.ofNullable(trackedResources.get(resource));
    }

    @Override
    public long getAmount(final ResourceKey resource) {
        return backingList.get(resource);
    }

    @Override
    public boolean isAutocraftable(final ResourceKey resource) {
        return autocraftableResources.contains(resource);
    }

    @Override
    public void sort() {
        LOGGER.debug("Sorting grid view");
        viewList = ViewList.create(viewList, backingList, autocraftableResources, getComparator(), resourceFactory,
            resource -> filter.test(this, resource));
        notifyListener();
    }

    @Override
    public void onChange(final ResourceKey resource,
                         final long amount,
                         @Nullable final TrackedResource trackedResource) {
        final MutableResourceList.OperationResult backingListResult = updateBackingList(resource, amount);
        if (backingListResult == null) {
            LOGGER.warn("Failed to update backing list for {} {}", amount, resource);
            return;
        }
        updateOrRemoveTrackedResource(resource, trackedResource);
        final GridResource viewListResource = viewList.get(resource);
        if (viewListResource != null) {
            updateExistingResource(resource, !backingListResult.available(), viewListResource);
            return;
        }
        tryAddNewResource(resource);
    }

    @Nullable
    private MutableResourceList.OperationResult updateBackingList(final ResourceKey resource, final long amount) {
        if (amount < 0) {
            return backingList.remove(resource, Math.abs(amount));
        }
        return backingList.add(resource, amount);
    }

    private void updateOrRemoveTrackedResource(final ResourceKey resource,
                                               @Nullable final TrackedResource trackedResource) {
        if (trackedResource == null) {
            trackedResources.remove(resource);
        } else {
            trackedResources.put(resource, trackedResource);
        }
    }

    private void updateExistingResource(final ResourceKey resource,
                                        final boolean removedFromBackingList,
                                        final GridResource gridResource) {
        final boolean canBeSorted = !preventSorting;
        if (canBeSorted) {
            LOGGER.debug("Actually updating {} resource in the view list", resource);
            if (removedFromBackingList && !autocraftableResources.contains(resource)) {
                viewList.remove(resource, gridResource);
                notifyListener();
            } else {
                viewList.update(gridResource, getComparator());
                notifyListener();
            }
        } else if (removedFromBackingList) {
            LOGGER.debug("{} is no longer available", resource);
        } else {
            LOGGER.debug("{} can't be sorted, preventing sorting is on", resource);
        }
    }

    private void tryAddNewResource(final ResourceKey resource) {
        final GridResource gridResource = resourceFactory.apply(resource);
        if (filter.test(this, gridResource)) {
            LOGGER.debug("Filter allowed, actually adding {}", resource);
            viewList.add(resource, gridResource, getComparator());
            notifyListener();
        }
    }

    private void notifyListener() {
        if (listener != null) {
            listener.run();
        }
    }

    private Comparator<GridResource> getComparator() {
        // An identity sort is necessary so the order of items is preserved in quantity sorting mode.
        // If two grid resources have the same quantity, their order would otherwise not be preserved.
        final Comparator<GridResource> comparator = sortingType.apply(this).thenComparing(identitySort);
        if (sortingDirection == GridSortingDirection.ASCENDING) {
            return comparator;
        }
        return comparator.reversed();
    }

    @Override
    public List<GridResource> getViewList() {
        return viewList.getListView();
    }

    @Override
    public MutableResourceList copyBackingList() {
        return backingList.copy();
    }

    @Override
    public void clear() {
        backingList.clear();
        viewList.clear();
        trackedResources.clear();
    }
}
