package com.refinedmods.refinedstorage.common.exporter;

import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.common.api.upgrade.UpgradeState;
import com.refinedmods.refinedstorage.common.content.Items;

import java.util.function.ToLongFunction;

public class ExporterTransferQuotaProvider implements ToLongFunction<ResourceKey> {
    private final long baseTransferQuota;
    private final UpgradeState upgradeState;
    private final boolean regulating;
    private final ToLongFunction<ResourceKey> currentAmountProvider;
    private final boolean respectTransferQuotaWhenRegulating;

    public ExporterTransferQuotaProvider(final long singleAmount,
                                         final UpgradeState upgradeState,
                                         final ToLongFunction<ResourceKey> currentAmountProvider,
                                         final boolean respectTransferQuotaWhenRegulating) {
        this.baseTransferQuota = upgradeState.has(Items.INSTANCE.getStackUpgrade())
            ? singleAmount * 64
            : singleAmount;
        this.upgradeState = upgradeState;
        this.regulating = upgradeState.has(Items.INSTANCE.getRegulatorUpgrade());
        this.currentAmountProvider = currentAmountProvider;
        this.respectTransferQuotaWhenRegulating = respectTransferQuotaWhenRegulating;
    }

    @Override
    public long applyAsLong(final ResourceKey resource) {
        if (!regulating) {
            return baseTransferQuota;
        }
        final long desiredAmount = upgradeState.getRegulatedAmount(resource);
        if (desiredAmount <= 0) {
            return baseTransferQuota;
        }
        final long currentAmount = currentAmountProvider.applyAsLong(resource);
        final long stillNeeded = desiredAmount - currentAmount;
        if (stillNeeded <= 0) {
            return 0;
        }
        return respectTransferQuotaWhenRegulating ? Math.min(stillNeeded, baseTransferQuota) : stillNeeded;
    }
}
