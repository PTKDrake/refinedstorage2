package com.refinedmods.refinedstorage.api.network.impl.node.exporter;

import com.refinedmods.refinedstorage.api.network.impl.node.AbstractNetworkNode;
import com.refinedmods.refinedstorage.api.network.node.NetworkNodeActor;
import com.refinedmods.refinedstorage.api.network.node.SchedulingMode;
import com.refinedmods.refinedstorage.api.network.node.exporter.ExporterTransferStrategy;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.api.storage.Actor;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

public class ExporterNetworkNode extends AbstractNetworkNode {
    private long energyUsage;
    private final Actor actor = new NetworkNodeActor(this);
    private final List<ExporterTask> tasks = new ArrayList<>();
    @Nullable
    private ExporterTransferStrategy transferStrategy;
    @Nullable
    private SchedulingMode schedulingMode;

    public ExporterNetworkNode(final long energyUsage) {
        this.energyUsage = energyUsage;
    }

    public void setTransferStrategy(final ExporterTransferStrategy transferStrategy) {
        this.transferStrategy = transferStrategy;
    }

    public void setSchedulingMode(@Nullable final SchedulingMode schedulingMode) {
        this.schedulingMode = schedulingMode;
    }

    @Override
    public void doWork() {
        super.doWork();
        if (network == null || !isActive() || schedulingMode == null) {
            return;
        }
        schedulingMode.execute(tasks);
    }

    @Nullable
    public ExporterTransferStrategy.Result getLastResult(final int filterIndex) {
        return tasks.get(filterIndex).lastResult;
    }

    public void setFilters(final List<ResourceKey> filters) {
        final List<ExporterTask> updatedTasks = new ArrayList<>();
        for (int i = 0; i < filters.size(); ++i) {
            final ResourceKey filter = filters.get(i);
            final ExporterTransferStrategy.Result lastResult = (i < tasks.size() && tasks.get(i).filter.equals(filter))
                ? tasks.get(i).lastResult
                : null;
            final ExporterTask task = new ExporterTask(filter, lastResult);
            updatedTasks.add(task);
        }
        tasks.clear();
        tasks.addAll(updatedTasks);
    }

    public void setEnergyUsage(final long energyUsage) {
        this.energyUsage = energyUsage;
    }

    @Override
    public long getEnergyUsage() {
        return energyUsage;
    }

    class ExporterTask implements SchedulingMode.ScheduledTask {
        private final ResourceKey filter;
        @Nullable
        private ExporterTransferStrategy.Result lastResult;

        ExporterTask(final ResourceKey filter, @Nullable final ExporterTransferStrategy.Result lastResult) {
            this.filter = filter;
            this.lastResult = lastResult;
        }

        @Override
        public boolean run() {
            if (transferStrategy == null || network == null) {
                return false;
            }
            final ExporterTransferStrategy.Result result = transferStrategy.transfer(filter, actor, network);
            this.lastResult = result;
            return result == ExporterTransferStrategy.Result.EXPORTED;
        }
    }
}
