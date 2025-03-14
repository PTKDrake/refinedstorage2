package com.refinedmods.refinedstorage.api.resource.repository;

import com.refinedmods.refinedstorage.api.resource.ResourceKey;

import java.util.function.Function;

import org.apiguardian.api.API;

@FunctionalInterface
@API(status = API.Status.STABLE, since = "2.0.0-milestone.2.6")
public interface ResourceRepositoryMapper<T> extends Function<ResourceKey, T> {
}
