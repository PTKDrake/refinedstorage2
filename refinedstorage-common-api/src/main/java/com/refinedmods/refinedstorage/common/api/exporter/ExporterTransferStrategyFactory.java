package com.refinedmods.refinedstorage.common.api.exporter;

import com.refinedmods.refinedstorage.api.network.node.exporter.ExporterTransferStrategy;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.common.api.support.network.AmountOverride;
import com.refinedmods.refinedstorage.common.api.upgrade.UpgradeState;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import org.apiguardian.api.API;

@API(status = API.Status.STABLE, since = "2.0.0-milestone.2.4")
public interface ExporterTransferStrategyFactory {
    Class<? extends ResourceKey> getResourceType();

    ExporterTransferStrategy create(
        ServerLevel level,
        BlockPos pos,
        Direction direction,
        UpgradeState upgradeState,
        AmountOverride amountOverride,
        boolean fuzzyMode
    );
}
