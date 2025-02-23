package com.refinedmods.refinedstorage.neoforge.importer;

import com.refinedmods.refinedstorage.api.network.impl.node.importer.ImporterTransferStrategyImpl;
import com.refinedmods.refinedstorage.api.network.node.importer.ImporterTransferStrategy;
import com.refinedmods.refinedstorage.common.api.importer.ImporterTransferStrategyFactory;
import com.refinedmods.refinedstorage.common.api.upgrade.UpgradeState;
import com.refinedmods.refinedstorage.common.content.Items;
import com.refinedmods.refinedstorage.common.importer.ImporterTransferQuotaProvider;
import com.refinedmods.refinedstorage.neoforge.storage.CapabilityCacheImpl;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;

public class ItemHandlerImporterTransferStrategyFactory implements ImporterTransferStrategyFactory {
    @Override
    public ImporterTransferStrategy create(final ServerLevel level,
                                           final BlockPos pos,
                                           final Direction direction,
                                           final UpgradeState upgradeState) {
        final ItemHandlerImporterSource source = new ItemHandlerImporterSource(new CapabilityCacheImpl(
            level,
            pos,
            direction
        ));
        final ImporterTransferQuotaProvider transferQuotaProvider = new ImporterTransferQuotaProvider(
            upgradeState.has(Items.INSTANCE.getStackUpgrade()) ? 64 : 1,
            upgradeState,
            source::getAmount
        );
        return new ImporterTransferStrategyImpl(source, transferQuotaProvider);
    }
}
