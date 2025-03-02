package com.refinedmods.refinedstorage.api.resource.repository;

import com.refinedmods.refinedstorage.api.resource.ResourceKey;

import java.util.HashMap;
import java.util.Map;

class CachingResourceRepositoryMapper<T> implements ResourceRepositoryMapper<T> {
    private final ResourceRepositoryMapper<T> delegate;
    private final Map<ResourceKey, T> cache = new HashMap<>();

    CachingResourceRepositoryMapper(final ResourceRepositoryMapper<T> delegate) {
        this.delegate = delegate;
    }

    @Override
    public T apply(final ResourceKey resource) {
        return cache.computeIfAbsent(resource, delegate);
    }
}
