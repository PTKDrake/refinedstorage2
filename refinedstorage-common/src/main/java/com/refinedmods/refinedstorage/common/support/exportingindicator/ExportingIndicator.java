package com.refinedmods.refinedstorage.common.support.exportingindicator;

import com.refinedmods.refinedstorage.common.support.Sprites;

import javax.annotation.Nullable;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createTranslation;

public enum ExportingIndicator {
    NONE(null),
    DESTINATION_DOES_NOT_ACCEPT_RESOURCE(Sprites.WARNING),
    RESOURCE_MISSING(Sprites.WARNING),
    AUTOCRAFTING_STARTED(Sprites.AUTOCRAFTING_INDICATOR),
    AUTOCRAFTING_MISSING_RESOURCES(Sprites.WARNING);

    private final Component tooltip;
    @Nullable
    private final ResourceLocation sprite;

    ExportingIndicator(@Nullable final ResourceLocation sprite) {
        this.tooltip = createTranslation("gui", "exporting_indicator." + name().toLowerCase());
        this.sprite = sprite;
    }

    @Nullable
    public ResourceLocation getSprite() {
        return sprite;
    }

    public Component getTooltip() {
        return tooltip;
    }
}
