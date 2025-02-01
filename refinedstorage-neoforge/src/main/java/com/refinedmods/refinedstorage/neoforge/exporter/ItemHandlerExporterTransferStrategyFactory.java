package com.refinedmods.refinedstorage.neoforge.exporter;

import com.refinedmods.refinedstorage.api.network.impl.node.exporter.ExporterTransferStrategyImpl;
import com.refinedmods.refinedstorage.api.network.impl.node.exporter.MissingResourcesListeningExporterTransferStrategy;
import com.refinedmods.refinedstorage.api.network.node.exporter.ExporterTransferStrategy;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.common.api.exporter.ExporterTransferStrategyFactory;
import com.refinedmods.refinedstorage.common.api.storage.root.FuzzyRootStorage;
import com.refinedmods.refinedstorage.common.api.support.network.AmountOverride;
import com.refinedmods.refinedstorage.common.api.upgrade.UpgradeState;
import com.refinedmods.refinedstorage.common.content.Items;
import com.refinedmods.refinedstorage.common.support.resource.ItemResource;
import com.refinedmods.refinedstorage.neoforge.storage.CapabilityCacheImpl;
import com.refinedmods.refinedstorage.neoforge.storage.ItemHandlerInsertableStorage;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;

import static com.refinedmods.refinedstorage.api.network.impl.node.exporter.MissingResourcesListeningExporterTransferStrategy.OnMissingResources.scheduleAutocrafting;

public class ItemHandlerExporterTransferStrategyFactory implements ExporterTransferStrategyFactory {
    @Override
    public Class<? extends ResourceKey> getResourceType() {
        return ItemResource.class;
    }

    @Override
    public ExporterTransferStrategy create(final ServerLevel level,
                                           final BlockPos pos,
                                           final Direction direction,
                                           final UpgradeState upgradeState,
                                           final AmountOverride amountOverride,
                                           final boolean fuzzyMode) {
        final CapabilityCacheImpl coordinates = new CapabilityCacheImpl(level, pos, direction);
        final ItemHandlerInsertableStorage destination = new ItemHandlerInsertableStorage(coordinates, amountOverride);
        final int transferQuota = upgradeState.has(Items.INSTANCE.getStackUpgrade()) ? 64 : 1;
        final ExporterTransferStrategy strategy = create(fuzzyMode, destination, transferQuota);
        if (upgradeState.has(Items.INSTANCE.getAutocraftingUpgrade())) {
            return new MissingResourcesListeningExporterTransferStrategy(strategy,
                scheduleAutocrafting(resource -> transferQuota));
        }
        return strategy;
    }

    private ExporterTransferStrategy create(final boolean fuzzyMode,
                                            final ItemHandlerInsertableStorage destination,
                                            final int transferQuota) {
        if (fuzzyMode) {
            return new ExporterTransferStrategyImpl(destination, transferQuota, FuzzyRootStorage.expander());
        }
        return new ExporterTransferStrategyImpl(destination, transferQuota);
    }
}
