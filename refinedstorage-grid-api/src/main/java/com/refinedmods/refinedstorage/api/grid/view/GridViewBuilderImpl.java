package com.refinedmods.refinedstorage.api.grid.view;

import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.api.resource.list.MutableResourceList;
import com.refinedmods.refinedstorage.api.resource.list.MutableResourceListImpl;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import org.apiguardian.api.API;

@API(status = API.Status.STABLE, since = "2.0.0-milestone.2.4")
public class GridViewBuilderImpl<T> implements GridViewBuilder<T> {
    private final GridResourceFactory<T> resourceFactory;
    private final MutableResourceList backingList = MutableResourceListImpl.create();
    private final Set<ResourceKey> autocraftableResources = new HashSet<>();
    private final Function<GridView<T>, Comparator<T>> identitySortingType;
    private final Function<GridView<T>, Comparator<T>> defaultSortingType;

    public GridViewBuilderImpl(final GridResourceFactory<T> resourceFactory,
                               final Function<GridView<T>, Comparator<T>> identitySortingType,
                               final Function<GridView<T>, Comparator<T>> defaultSortingType) {
        this.resourceFactory = resourceFactory;
        this.identitySortingType = identitySortingType;
        this.defaultSortingType = defaultSortingType;
    }

    @Override
    public GridViewBuilder<T> withResource(final ResourceKey resource, final long amount) {
        backingList.add(resource, amount);
        return this;
    }

    @Override
    public GridViewBuilder<T> withAutocraftableResource(final ResourceKey resource) {
        autocraftableResources.add(resource);
        return this;
    }

    @Override
    public GridView<T> build() {
        return new GridViewImpl<>(
            resourceFactory,
            backingList,
            autocraftableResources,
            identitySortingType,
            defaultSortingType
        );
    }
}
