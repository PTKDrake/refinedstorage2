package com.refinedmods.refinedstorage.common.api.storage.root;

import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.api.storage.root.RootStorage;

import java.util.Collection;
import java.util.Collections;
import java.util.function.BiFunction;

import org.apiguardian.api.API;

@API(status = API.Status.STABLE, since = "2.0.0-milestone.2.4")
public interface FuzzyRootStorage extends RootStorage {
    Collection<ResourceKey> getFuzzy(ResourceKey resource);

    static BiFunction<RootStorage, ResourceKey, Collection<ResourceKey>> expander() {
        return (rootStorage, resource) -> rootStorage instanceof FuzzyRootStorage fuzzyRootStorage
            ? fuzzyRootStorage.getFuzzy(resource)
            : Collections.singletonList(resource);
    }
}
