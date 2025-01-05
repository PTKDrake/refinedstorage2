package com.refinedmods.refinedstorage.api.network.impl.node.patternprovider;

import com.refinedmods.refinedstorage.api.autocrafting.Pattern;
import com.refinedmods.refinedstorage.api.autocrafting.task.ExternalPatternInputSink;
import com.refinedmods.refinedstorage.api.autocrafting.task.Task;
import com.refinedmods.refinedstorage.api.autocrafting.task.TaskState;
import com.refinedmods.refinedstorage.api.core.Action;
import com.refinedmods.refinedstorage.api.network.Network;
import com.refinedmods.refinedstorage.api.network.autocrafting.AutocraftingNetworkComponent;
import com.refinedmods.refinedstorage.api.network.autocrafting.ParentContainer;
import com.refinedmods.refinedstorage.api.network.autocrafting.PatternProvider;
import com.refinedmods.refinedstorage.api.network.autocrafting.PatternProviderExternalPatternInputSink;
import com.refinedmods.refinedstorage.api.network.impl.node.SimpleNetworkNode;
import com.refinedmods.refinedstorage.api.network.storage.StorageNetworkComponent;
import com.refinedmods.refinedstorage.api.resource.ResourceAmount;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.annotation.Nullable;

// TODO(feat): return root results as soon as finished
// TODO(feat): persistence of tasks
// TODO(feat): autocrafter locking support
// TODO(feat): autocrafting monitor support
// TODO(feat): throttling and step behavior control
public class PatternProviderNetworkNode extends SimpleNetworkNode implements PatternProvider {
    private final Pattern[] patterns;
    private final Set<ParentContainer> parents = new HashSet<>();
    private final List<Task> tasks = new CopyOnWriteArrayList<>();
    private int priority;
    @Nullable
    private PatternProviderExternalPatternInputSink externalPatternInputSink;

    public PatternProviderNetworkNode(final long energyUsage, final int patterns) {
        super(energyUsage);
        this.patterns = new Pattern[patterns];
    }

    public void setExternalPatternInputSink(final PatternProviderExternalPatternInputSink externalPatternInputSink) {
        this.externalPatternInputSink = externalPatternInputSink;
    }

    public void setPattern(final int index, @Nullable final Pattern pattern) {
        final Pattern oldPattern = patterns[index];
        if (oldPattern != null) {
            parents.forEach(parent -> parent.remove(this, oldPattern));
        }
        patterns[index] = pattern;
        if (pattern != null) {
            parents.forEach(parent -> parent.add(this, pattern, priority));
        }
    }

    @Override
    public void setNetwork(@Nullable final Network network) {
        if (this.network != null) {
            for (final Task task : tasks) {
                this.network.getComponent(StorageNetworkComponent.class).removeListener(task);
            }
        }
        super.setNetwork(network);
        if (network != null) {
            final StorageNetworkComponent storage = network.getComponent(StorageNetworkComponent.class);
            for (final Task task : tasks) {
                storage.addListener(task);
            }
        }
    }

    @Override
    protected void onActiveChanged(final boolean newActive) {
        super.onActiveChanged(newActive);
        if (!newActive) {
            for (final Pattern pattern : patterns) {
                if (pattern != null) {
                    parents.forEach(parent -> parent.remove(this, pattern));
                }
            }
            return;
        }
        for (final Pattern pattern : patterns) {
            if (pattern != null) {
                parents.forEach(parent -> parent.add(this, pattern, priority));
            }
        }
    }

    @Override
    public void onAddedIntoContainer(final ParentContainer parentContainer) {
        parents.add(parentContainer);
        for (final Pattern pattern : patterns) {
            if (pattern != null) {
                parentContainer.add(this, pattern, priority);
            }
        }
    }

    @Override
    public void onRemovedFromContainer(final ParentContainer parentContainer) {
        parents.remove(parentContainer);
        for (final Pattern pattern : patterns) {
            if (pattern != null) {
                parentContainer.remove(this, pattern);
            }
        }
    }

    @Override
    public void addTask(final Task task) {
        tasks.add(task);
        if (network != null) {
            network.getComponent(StorageNetworkComponent.class).addListener(task);
        }
    }

    @Override
    public boolean accept(final Collection<ResourceAmount> resources, final Action action) {
        if (externalPatternInputSink == null) {
            return false;
        }
        return externalPatternInputSink.accept(resources, action);
    }

    public List<Task> getTasks() {
        return tasks;
    }

    @Override
    public void doWork() {
        super.doWork();
        if (network == null || !isActive()) {
            return;
        }
        final StorageNetworkComponent storage = network.getComponent(StorageNetworkComponent.class);
        final ExternalPatternInputSink outerExternalPatternInputSink =
            network.getComponent(AutocraftingNetworkComponent.class);
        tasks.removeIf(task -> {
            task.step(storage, outerExternalPatternInputSink);
            return task.getState() == TaskState.COMPLETED;
        });
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(final int priority) {
        this.priority = priority;
        for (final Pattern pattern : patterns) {
            if (pattern != null) {
                parents.forEach(parent -> parent.update(pattern, priority));
            }
        }
    }
}
