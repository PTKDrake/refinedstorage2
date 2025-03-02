package com.refinedmods.refinedstorage.common.api.grid.view;

import com.refinedmods.refinedstorage.api.grid.operations.GridExtractMode;
import com.refinedmods.refinedstorage.api.grid.view.GridView;
import com.refinedmods.refinedstorage.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage.api.storage.tracked.TrackedResource;
import com.refinedmods.refinedstorage.common.api.grid.GridScrollMode;
import com.refinedmods.refinedstorage.common.api.grid.strategy.GridExtractionStrategy;
import com.refinedmods.refinedstorage.common.api.grid.strategy.GridScrollingStrategy;
import com.refinedmods.refinedstorage.common.api.support.resource.PlatformResourceKey;
import com.refinedmods.refinedstorage.common.api.support.resource.ResourceType;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import org.apiguardian.api.API;

@API(status = API.Status.STABLE, since = "2.0.0-milestone.2.6")
public interface GridResource {
    Optional<TrackedResource> getTrackedResource(GridView<GridResource> view);

    long getAmount(GridView<GridResource> view);

    String getName();

    Set<String> getAttribute(GridResourceAttributeKey key);

    boolean isAutocraftable(GridView<GridResource> view);

    boolean canExtract(ItemStack carriedStack, GridView<GridResource> view);

    void onExtract(GridExtractMode extractMode,
                   boolean cursor,
                   GridExtractionStrategy extractionStrategy);

    void onScroll(GridScrollMode scrollMode,
                  GridScrollingStrategy scrollingStrategy);

    void render(GuiGraphics graphics, int x, int y);

    String getDisplayedAmount(GridView<GridResource> view);

    String getAmountInTooltip(GridView<GridResource> view);

    boolean belongsToResourceType(ResourceType resourceType);

    List<Component> getTooltip();

    Optional<TooltipComponent> getTooltipImage();

    int getRegistryId();

    List<ClientTooltipComponent> getExtractionHints(ItemStack carriedStack, GridView<GridResource> view);

    @Nullable
    ResourceAmount getAutocraftingRequest();

    @Nullable
    @API(status = API.Status.INTERNAL)
    PlatformResourceKey getResourceForRecipeMods();
}
