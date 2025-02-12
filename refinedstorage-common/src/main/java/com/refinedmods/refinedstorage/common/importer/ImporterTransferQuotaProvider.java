package com.refinedmods.refinedstorage.common.importer;

import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.common.api.upgrade.UpgradeState;
import com.refinedmods.refinedstorage.common.content.Items;

import java.util.function.ToLongFunction;

public class ImporterTransferQuotaProvider implements ToLongFunction<ResourceKey> {
    private final long baseTransferQuota;
    private final UpgradeState upgradeState;
    private final boolean regulating;
    private final ToLongFunction<ResourceKey> currentAmountProvider;

    public ImporterTransferQuotaProvider(final long singleAmount,
                                         final UpgradeState upgradeState,
                                         final ToLongFunction<ResourceKey> currentAmountProvider) {
        this.baseTransferQuota = upgradeState.has(Items.INSTANCE.getStackUpgrade())
            ? singleAmount * 64
            : singleAmount;
        this.upgradeState = upgradeState;
        this.regulating = upgradeState.has(Items.INSTANCE.getRegulatorUpgrade());
        this.currentAmountProvider = currentAmountProvider;
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
        final long stillAvailableToImport = currentAmount - desiredAmount;
        if (stillAvailableToImport <= 0) {
            return 0;
        }
        return Math.min(baseTransferQuota, stillAvailableToImport);
    }
}
