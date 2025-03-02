package com.refinedmods.refinedstorage.common.grid.view;

import com.refinedmods.refinedstorage.api.grid.view.GridResourceFactory;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.common.api.grid.GridResourceAttributeKeys;
import com.refinedmods.refinedstorage.common.api.grid.view.PlatformGridResource;
import com.refinedmods.refinedstorage.common.support.resource.FluidResource;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.material.Fluid;

public abstract class AbstractFluidGridResourceFactory implements GridResourceFactory<PlatformGridResource> {
    @Override
    public PlatformGridResource apply(final ResourceKey resource) {
        final FluidResource fluidResource = (FluidResource) resource;
        final String name = getName(fluidResource);
        final String modId = getModId(fluidResource);
        final String modName = getModName(modId);
        final Set<String> tags = getTags(fluidResource.fluid());
        final String tooltip = getTooltip(fluidResource);
        return new FluidGridResource(
            fluidResource,
            name,
            Map.of(
                GridResourceAttributeKeys.MOD_ID, Set.of(modId),
                GridResourceAttributeKeys.MOD_NAME, Set.of(modName),
                GridResourceAttributeKeys.TAGS, tags,
                GridResourceAttributeKeys.TOOLTIP, Set.of(tooltip)
            )
        );
    }

    private Set<String> getTags(final Fluid fluid) {
        return BuiltInRegistries.FLUID.getResourceKey(fluid)
            .flatMap(BuiltInRegistries.FLUID::getHolder)
            .stream()
            .flatMap(Holder::tags)
            .map(tagKey -> tagKey.location().getPath())
            .collect(Collectors.toSet());
    }

    private String getModId(final FluidResource fluid) {
        return BuiltInRegistries.FLUID.getKey(fluid.fluid()).getNamespace();
    }

    protected abstract String getModName(String modId);

    protected abstract String getName(FluidResource fluidResource);

    protected abstract String getTooltip(FluidResource resource);
}
