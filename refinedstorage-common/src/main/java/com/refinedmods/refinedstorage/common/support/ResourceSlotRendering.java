package com.refinedmods.refinedstorage.common.support;

import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.common.api.RefinedStorageApi;
import com.refinedmods.refinedstorage.common.api.support.resource.ResourceRendering;
import com.refinedmods.refinedstorage.common.support.containermenu.ResourceSlot;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

import static java.util.Objects.requireNonNullElse;

public final class ResourceSlotRendering {
    private ResourceSlotRendering() {
    }

    public static void render(final GuiGraphics graphics,
                              final ResourceSlot slot,
                              final int leftPos,
                              final int topPos) {
        final ResourceKey resource = slot.getResource();
        if (resource == null) {
            return;
        }
        render(
            graphics,
            leftPos + slot.x,
            topPos + slot.y,
            resource,
            slot.getAmount(),
            slot.shouldRenderAmount()
        );
    }

    private static void render(final GuiGraphics graphics,
                               final int x,
                               final int y,
                               final ResourceKey resource,
                               final long amount,
                               final boolean renderAmount) {
        final ResourceRendering rendering = RefinedStorageApi.INSTANCE.getResourceRendering(resource.getClass());
        rendering.render(resource, graphics, x, y);
        if (renderAmount) {
            render(graphics, x, y, amount, rendering);
        }
    }

    public static void render(final GuiGraphics graphics,
                              final int x,
                              final int y,
                              final long amount,
                              final ResourceRendering rendering) {
        renderAmount(
            graphics,
            x,
            y,
            rendering.formatAmount(amount, true),
            requireNonNullElse(ChatFormatting.WHITE.getColor(), 15),
            true
        );
    }

    public static void renderAmount(final GuiGraphics graphics,
                                    final int x,
                                    final int y,
                                    final String amount,
                                    final int color,
                                    final boolean large) {
        final Font font = Minecraft.getInstance().font;
        final PoseStack poseStack = graphics.pose();
        poseStack.pushPose();
        // Large amounts overlap with the slot lines (see Minecraft behavior)
        poseStack.translate(x + (large ? 1D : 0D), y + (large ? 1D : 0D), 199);
        if (!large) {
            poseStack.scale(0.5F, 0.5F, 1);
        }
        graphics.drawString(font, amount, (large ? 16 : 30) - font.width(amount), large ? 8 : 22, color, true);
        poseStack.popPose();
    }
}
