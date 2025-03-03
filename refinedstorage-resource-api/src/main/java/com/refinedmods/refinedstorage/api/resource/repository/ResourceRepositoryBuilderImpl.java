package com.refinedmods.refinedstorage.api.resource.repository;

import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.api.resource.list.MutableResourceList;
import com.refinedmods.refinedstorage.api.resource.list.MutableResourceListImpl;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import org.apiguardian.api.API;

@API(status = API.Status.STABLE, since = "2.0.0-milestone.2.4")
public class ResourceRepositoryBuilderImpl<T> implements ResourceRepositoryBuilder<T> {
    private final ResourceRepositoryMapper<T> mapper;
    private final MutableResourceList backingList = MutableResourceListImpl.create();
    private final Set<ResourceKey> stickyResources = new HashSet<>();
    private final Function<ResourceRepository<T>, Comparator<T>> identitySortingType;
    private final Function<ResourceRepository<T>, Comparator<T>> defaultSortingType;

    public ResourceRepositoryBuilderImpl(final ResourceRepositoryMapper<T> mapper,
                                         final Function<ResourceRepository<T>, Comparator<T>> identitySortingType,
                                         final Function<ResourceRepository<T>, Comparator<T>> defaultSortingType) {
        this.mapper = mapper;
        this.identitySortingType = identitySortingType;
        this.defaultSortingType = defaultSortingType;
    }

    @Override
    public ResourceRepositoryBuilder<T> addResource(final ResourceKey resource, final long amount) {
        backingList.add(resource, amount);
        return this;
    }

    @Override
    public ResourceRepositoryBuilder<T> addStickyResource(final ResourceKey resource) {
        stickyResources.add(resource);
        return this;
    }

    @Override
    public ResourceRepository<T> build() {
        return new ResourceRepositoryImpl<>(
            mapper,
            backingList,
            stickyResources,
            identitySortingType,
            defaultSortingType
        );
    }
}
