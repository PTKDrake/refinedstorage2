package com.refinedmods.refinedstorage.common.storage.externalstorage;

import com.refinedmods.refinedstorage.api.core.Action;
import com.refinedmods.refinedstorage.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.api.storage.Actor;
import com.refinedmods.refinedstorage.api.storage.external.ExternalStorageProvider;

import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Iterators;

class CompositeExternalStorageProvider implements ExternalStorageProvider {
    private final List<ExternalStorageProvider> providers;

    CompositeExternalStorageProvider(final List<ExternalStorageProvider> providers) {
        this.providers = providers;
    }

    @Override
    public Iterator<ResourceAmount> iterator() {
        return Iterators.concat(providers.stream().map(ExternalStorageProvider::iterator).toList().iterator());
    }

    @Override
    public long extract(final ResourceKey resource, final long amount, final Action action, final Actor actor) {
        for (final ExternalStorageProvider provider : providers) {
            final long extracted = provider.extract(resource, amount, action, actor);
            if (extracted > 0) {
                return extracted;
            }
        }
        return 0;
    }

    @Override
    public long insert(final ResourceKey resource, final long amount, final Action action, final Actor actor) {
        for (final ExternalStorageProvider provider : providers) {
            final long inserted = provider.insert(resource, amount, action, actor);
            if (inserted > 0) {
                return inserted;
            }
        }
        return 0;
    }
}
