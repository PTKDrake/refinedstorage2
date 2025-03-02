package com.refinedmods.refinedstorage.api.resource.repository;

import java.util.function.BiPredicate;

import org.apiguardian.api.API;

@API(status = API.Status.STABLE, since = "2.0.0-beta.1")
@FunctionalInterface
public interface ResourceRepositoryFilter<T> extends BiPredicate<ResourceRepository<T>, T> {
    default ResourceRepositoryFilter<T> and(final ResourceRepositoryFilter<T> other) {
        return (view, resource) -> test(view, resource) && other.test(view, resource);
    }
}
