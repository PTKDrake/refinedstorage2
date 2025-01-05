package com.refinedmods.refinedstorage.api.autocrafting.task;

import com.refinedmods.refinedstorage.api.autocrafting.Pattern;
import com.refinedmods.refinedstorage.api.core.Action;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.api.resource.list.MutableResourceList;
import com.refinedmods.refinedstorage.api.resource.list.ResourceList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class InternalTaskPattern extends AbstractTaskPattern {
    private static final Logger LOGGER = LoggerFactory.getLogger(InternalTaskPattern.class);

    private long iterationsRemaining;

    InternalTaskPattern(final Pattern pattern, final TaskPlan.PatternPlan plan) {
        super(pattern, plan);
        this.iterationsRemaining = plan.iterations();
    }

    @Override
    boolean step(final MutableResourceList internalStorage, final ExternalPatternInputSink externalPatternInputSink) {
        final ResourceList iterationInputsSimulated = calculateIterationInputs(Action.SIMULATE);
        if (!extractAll(iterationInputsSimulated, internalStorage, Action.SIMULATE)) {
            return false;
        }
        LOGGER.debug("Stepping {}", pattern);
        final ResourceList iterationInputs = calculateIterationInputs(Action.EXECUTE);
        extractAll(iterationInputs, internalStorage, Action.EXECUTE);
        pattern.outputs().forEach(output -> {
            LOGGER.debug("Inserting {}x {} into internal storage", output.amount(), output.resource());
            internalStorage.add(output);
        });
        return useIteration();
    }

    @Override
    long interceptInsertion(final ResourceKey resource, final long amount) {
        return 0;
    }

    protected boolean useIteration() {
        iterationsRemaining--;
        LOGGER.debug("Stepped {} with {} iterations remaining", pattern, iterationsRemaining);
        return iterationsRemaining == 0;
    }
}
