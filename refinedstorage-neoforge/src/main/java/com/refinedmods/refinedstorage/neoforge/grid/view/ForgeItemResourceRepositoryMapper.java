package com.refinedmods.refinedstorage.neoforge.grid.view;

import com.refinedmods.refinedstorage.common.grid.view.AbstractItemGridResourceRepositoryMapper;

import java.util.Optional;

import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;

import static java.util.Objects.requireNonNullElse;

public class ForgeItemResourceRepositoryMapper extends AbstractItemGridResourceRepositoryMapper {
    @Override
    public Optional<String> getModName(final String modId) {
        return ModList.get()
            .getModContainerById(modId)
            .map(container -> container.getModInfo().getDisplayName());
    }

    @Override
    public String getModId(final ItemStack itemStack) {
        return requireNonNullElse(itemStack.getItem().getCreatorModId(itemStack), "");
    }
}
