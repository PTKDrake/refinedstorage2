package com.refinedmods.refinedstorage.neoforge.exporter;

import com.refinedmods.refinedstorage.api.network.impl.node.exporter.ExporterTransferStrategyImpl;
import com.refinedmods.refinedstorage.api.network.impl.node.exporter.MissingResourcesListeningExporterTransferStrategy;
import com.refinedmods.refinedstorage.api.network.node.exporter.ExporterTransferStrategy;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.common.Platform;
import com.refinedmods.refinedstorage.common.api.exporter.ExporterTransferStrategyFactory;
import com.refinedmods.refinedstorage.common.api.storage.root.FuzzyRootStorage;
import com.refinedmods.refinedstorage.common.api.support.network.AmountOverride;
import com.refinedmods.refinedstorage.common.api.upgrade.UpgradeState;
import com.refinedmods.refinedstorage.common.content.Items;
import com.refinedmods.refinedstorage.common.support.resource.FluidResource;
import com.refinedmods.refinedstorage.neoforge.storage.CapabilityCache;
import com.refinedmods.refinedstorage.neoforge.storage.CapabilityCacheImpl;
import com.refinedmods.refinedstorage.neoforge.storage.FluidHandlerInsertableStorage;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;

import static com.refinedmods.refinedstorage.api.network.impl.node.exporter.MissingResourcesListeningExporterTransferStrategy.OnMissingResources.scheduleAutocrafting;

public class FluidHandlerExporterTransferStrategyFactory implements ExporterTransferStrategyFactory {
    @Override
    public Class<? extends ResourceKey> getResourceType() {
        return FluidResource.class;
    }

    @Override
    public ExporterTransferStrategy create(final ServerLevel level,
                                           final BlockPos pos,
                                           final Direction direction,
                                           final UpgradeState upgradeState,
                                           final AmountOverride amountOverride,
                                           final boolean fuzzyMode) {
        final CapabilityCache coordinates = new CapabilityCacheImpl(level, pos, direction);
        final FluidHandlerInsertableStorage destination = new FluidHandlerInsertableStorage(
            coordinates,
            amountOverride
        );
        final long transferQuota = (upgradeState.has(Items.INSTANCE.getStackUpgrade()) ? 64 : 1)
            * Platform.INSTANCE.getBucketAmount();
        final ExporterTransferStrategy strategy = create(fuzzyMode, destination, transferQuota);
        if (upgradeState.has(Items.INSTANCE.getAutocraftingUpgrade())) {
            return new MissingResourcesListeningExporterTransferStrategy(strategy,
                scheduleAutocrafting(resource -> transferQuota));
        }
        return strategy;
    }

    private ExporterTransferStrategy create(final boolean fuzzyMode,
                                            final FluidHandlerInsertableStorage destination,
                                            final long transferQuota) {
        if (fuzzyMode) {
            return new ExporterTransferStrategyImpl(destination, transferQuota, FuzzyRootStorage.expander());
        }
        return new ExporterTransferStrategyImpl(destination, transferQuota);
    }
}
