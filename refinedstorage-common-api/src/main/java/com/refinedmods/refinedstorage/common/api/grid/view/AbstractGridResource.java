package com.refinedmods.refinedstorage.common.api.grid.view;

import com.refinedmods.refinedstorage.api.grid.view.GridView;
import com.refinedmods.refinedstorage.api.storage.tracked.TrackedResource;
import com.refinedmods.refinedstorage.common.api.support.resource.PlatformResourceKey;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;

import org.apiguardian.api.API;

@API(status = API.Status.STABLE, since = "2.0.0-milestone.3.0")
public abstract class AbstractGridResource<T extends PlatformResourceKey> implements GridResource {
    protected final T resource;
    private final String name;
    private final Map<GridResourceAttributeKey, Set<String>> attributes;

    protected AbstractGridResource(final T resource,
                                   final String name,
                                   final Map<GridResourceAttributeKey, Set<String>> attributes) {
        this.resource = resource;
        this.name = name;
        this.attributes = attributes;
    }

    @Override
    public Optional<TrackedResource> getTrackedResource(final GridView<GridResource> view) {
        return view.getTrackedResource(resource);
    }

    @Override
    public long getAmount(final GridView<GridResource> view) {
        return view.getAmount(resource);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Set<String> getAttribute(final GridResourceAttributeKey key) {
        return attributes.getOrDefault(key, Collections.emptySet());
    }

    @Override
    public boolean isAutocraftable(final GridView<GridResource> view) {
        return view.isAutocraftable(resource);
    }

    @Nullable
    @Override
    public PlatformResourceKey getResourceForRecipeMods() {
        return resource;
    }

    @Override
    public String toString() {
        return "AbstractGridResource{"
            + "resource=" + resource
            + ", name='" + name + '\''
            + ", attributes=" + attributes
            + '}';
    }
}
