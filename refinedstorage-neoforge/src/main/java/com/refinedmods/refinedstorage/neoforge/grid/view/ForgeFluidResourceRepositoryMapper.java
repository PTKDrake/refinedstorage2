package com.refinedmods.refinedstorage.neoforge.grid.view;

import com.refinedmods.refinedstorage.common.grid.view.AbstractFluidGridResourceRepositoryMapper;
import com.refinedmods.refinedstorage.common.support.resource.FluidResource;

import net.neoforged.fml.ModList;
import net.neoforged.neoforge.fluids.FluidType;

import static com.refinedmods.refinedstorage.neoforge.support.resource.VariantUtil.toFluidStack;

public class ForgeFluidResourceRepositoryMapper extends AbstractFluidGridResourceRepositoryMapper {
    @Override
    protected String getTooltip(final FluidResource resource) {
        return getName(resource);
    }

    @Override
    protected String getModName(final String modId) {
        return ModList
            .get()
            .getModContainerById(modId)
            .map(container -> container.getModInfo().getDisplayName())
            .orElse("");
    }

    @Override
    protected String getName(final FluidResource fluidResource) {
        return toFluidStack(fluidResource, FluidType.BUCKET_VOLUME).getHoverName().getString();
    }
}
