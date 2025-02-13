package com.refinedmods.refinedstorage.common.exporter;

import com.refinedmods.refinedstorage.common.Platform;
import com.refinedmods.refinedstorage.common.support.AbstractFilterScreen;
import com.refinedmods.refinedstorage.common.support.containermenu.PropertyTypes;
import com.refinedmods.refinedstorage.common.support.exportingindicator.ExportingIndicator;
import com.refinedmods.refinedstorage.common.support.widget.FuzzyModeSideButtonWidget;
import com.refinedmods.refinedstorage.common.support.widget.SchedulingModeSideButtonWidget;

import java.util.List;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import static com.refinedmods.refinedstorage.common.support.Sprites.WARNING_SIZE;

public class ExporterScreen extends AbstractFilterScreen<ExporterContainerMenu> {
    public ExporterScreen(final ExporterContainerMenu menu, final Inventory playerInventory, final Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void init() {
        super.init();
        addSideButton(new FuzzyModeSideButtonWidget(
            getMenu().getProperty(PropertyTypes.FUZZY_MODE),
            () -> FuzzyModeSideButtonWidget.Type.EXTRACTING_STORAGE_NETWORK
        ));
        addSideButton(new SchedulingModeSideButtonWidget(getMenu().getProperty(PropertyTypes.SCHEDULING_MODE)));
    }

    @Override
    protected void renderTooltip(final GuiGraphics graphics, final int x, final int y) {
        for (int i = 0; i < getMenu().getIndicators(); ++i) {
            final ExportingIndicator indicator = getMenu().getIndicator(i);
            final int xx = leftPos + 7 + (i * 18) + 18 - 10 + 1;
            final int yy = topPos + 19 + 18 - 10 + 1;
            final ResourceLocation sprite = indicator.getSprite();
            if (sprite != null) {
                graphics.pose().pushPose();
                graphics.pose().translate(0, 0, 300);
                graphics.blitSprite(sprite, xx, yy, WARNING_SIZE, WARNING_SIZE);
                graphics.pose().popPose();
            }
            if (indicator != ExportingIndicator.NONE
                && isHovering(xx - leftPos, yy - topPos, WARNING_SIZE, WARNING_SIZE, x, y)) {
                Platform.INSTANCE.renderTooltip(
                    graphics,
                    List.of(ClientTooltipComponent.create(indicator.getTooltip().getVisualOrderText())),
                    x,
                    y
                );
                return;
            }
        }
        super.renderTooltip(graphics, x, y);
    }
}
