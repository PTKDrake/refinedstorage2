package com.refinedmods.refinedstorage.api.autocrafting.task;

import com.refinedmods.refinedstorage.api.autocrafting.Pattern;
import com.refinedmods.refinedstorage.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;

import java.util.Collection;
import java.util.Map;

import org.apiguardian.api.API;

@API(status = API.Status.STABLE, since = "2.0.0-milestone.4.12")
public record TaskPlan(Pattern pattern,
                       Map<Pattern, PatternPlan> patterns,
                       Collection<ResourceAmount> initialRequirements) {
    public PatternPlan getPattern(final Pattern p) {
        return patterns.get(p);
    }

    public record PatternPlan(long iterations, Map<Integer, Map<ResourceKey, Long>> ingredients) {
    }
}
