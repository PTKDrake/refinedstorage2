package com.refinedmods.refinedstorage.common.grid.screen.hint;

import com.refinedmods.refinedstorage.common.api.grid.GridInsertionHint;
import com.refinedmods.refinedstorage.common.api.support.AmountFormatting;
import com.refinedmods.refinedstorage.common.support.tooltip.MouseClientTooltipComponent;

import java.util.Optional;

import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.item.ItemStack;

public class ItemGridInsertionHint implements GridInsertionHint {
    @Override
    public Optional<ClientTooltipComponent> getHint(final ItemStack carried) {
        return Optional.of(MouseClientTooltipComponent.item(
            MouseClientTooltipComponent.Type.LEFT,
            carried,
            carried.getCount() == 1 ? null : AmountFormatting.format(carried.getCount())
        ));
    }
}
