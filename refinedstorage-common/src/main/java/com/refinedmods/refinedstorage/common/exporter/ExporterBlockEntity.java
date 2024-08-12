package com.refinedmods.refinedstorage.common.exporter;

import com.refinedmods.refinedstorage.api.network.impl.node.exporter.CompositeExporterTransferStrategy;
import com.refinedmods.refinedstorage.api.network.impl.node.exporter.ExporterNetworkNode;
import com.refinedmods.refinedstorage.api.network.node.exporter.ExporterTransferStrategy;
import com.refinedmods.refinedstorage.api.network.node.task.TaskExecutor;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.common.Platform;
import com.refinedmods.refinedstorage.common.api.RefinedStorageApi;
import com.refinedmods.refinedstorage.common.api.exporter.ExporterTransferStrategyFactory;
import com.refinedmods.refinedstorage.common.api.support.network.AmountOverride;
import com.refinedmods.refinedstorage.common.content.BlockEntities;
import com.refinedmods.refinedstorage.common.content.ContentNames;
import com.refinedmods.refinedstorage.common.content.Items;
import com.refinedmods.refinedstorage.common.support.AbstractDirectionalBlock;
import com.refinedmods.refinedstorage.common.support.BlockEntityWithDrops;
import com.refinedmods.refinedstorage.common.support.network.AbstractSchedulingNetworkNodeContainerBlockEntity;
import com.refinedmods.refinedstorage.common.upgrade.UpgradeContainer;
import com.refinedmods.refinedstorage.common.upgrade.UpgradeDestinations;
import com.refinedmods.refinedstorage.common.util.ContainerUtil;

import java.util.List;
import java.util.function.LongSupplier;
import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExporterBlockEntity
    extends AbstractSchedulingNetworkNodeContainerBlockEntity<ExporterNetworkNode, ExporterNetworkNode.TaskContext>
    implements AmountOverride, BlockEntityWithDrops {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExporterBlockEntity.class);
    private static final String TAG_UPGRADES = "upgr";

    private final UpgradeContainer upgradeContainer;

    public ExporterBlockEntity(final BlockPos pos, final BlockState state) {
        super(
            BlockEntities.INSTANCE.getExporter(),
            pos,
            state,
            new ExporterNetworkNode(Platform.INSTANCE.getConfig().getExporter().getEnergyUsage())
        );
        this.upgradeContainer = new UpgradeContainer(UpgradeDestinations.EXPORTER, (rate, upgradeEnergyUsage) -> {
            setWorkTickRate(rate);
            final long baseEnergyUsage = Platform.INSTANCE.getConfig().getExporter().getEnergyUsage();
            mainNetworkNode.setEnergyUsage(baseEnergyUsage + upgradeEnergyUsage);
            setChanged();
            if (level instanceof ServerLevel serverLevel) {
                initialize(serverLevel);
            }
        });
    }

    @Override
    protected boolean hasWorkTickRate() {
        return true;
    }

    @Override
    public List<Item> getUpgradeItems() {
        return upgradeContainer.getUpgradeItems();
    }

    @Override
    public boolean addUpgradeItem(final Item upgradeItem) {
        return upgradeContainer.addUpgradeItem(upgradeItem);
    }

    @Override
    protected void initialize(final ServerLevel level, final Direction direction) {
        super.initialize(level, direction);
        final ExporterTransferStrategy strategy = createStrategy(level, direction);
        LOGGER.debug("Initialized exporter at {} with strategy {}", worldPosition, strategy);
        mainNetworkNode.setTransferStrategy(strategy);
    }

    private ExporterTransferStrategy createStrategy(final ServerLevel serverLevel, final Direction direction) {
        final Direction incomingDirection = direction.getOpposite();
        final BlockPos sourcePosition = worldPosition.relative(direction);
        final List<ExporterTransferStrategyFactory> factories =
            RefinedStorageApi.INSTANCE.getExporterTransferStrategyRegistry().getAll();
        final List<ExporterTransferStrategy> strategies = factories
            .stream()
            .map(factory -> factory.create(
                serverLevel,
                sourcePosition,
                incomingDirection,
                upgradeContainer,
                this,
                filter.isFuzzyMode()
            ))
            .toList();
        return new CompositeExporterTransferStrategy(strategies);
    }

    @Override
    public void saveAdditional(final CompoundTag tag, final HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.put(TAG_UPGRADES, ContainerUtil.write(upgradeContainer, provider));
    }

    @Override
    public void loadAdditional(final CompoundTag tag, final HolderLookup.Provider provider) {
        if (tag.contains(TAG_UPGRADES)) {
            ContainerUtil.read(tag.getCompound(TAG_UPGRADES), upgradeContainer, provider);
        }
        super.loadAdditional(tag, provider);
    }

    @Override
    public final NonNullList<ItemStack> getDrops() {
        return upgradeContainer.getDrops();
    }

    @Override
    public Component getDisplayName() {
        return getName(ContentNames.EXPORTER);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(final int syncId, final Inventory inventory, final Player player) {
        return new ExporterContainerMenu(syncId, player, this, filter.getFilterContainer(), upgradeContainer);
    }

    @Override
    protected void setTaskExecutor(final TaskExecutor<ExporterNetworkNode.TaskContext> taskExecutor) {
        mainNetworkNode.setTaskExecutor(taskExecutor);
    }

    @Override
    protected void setFilters(final List<ResourceKey> filters) {
        mainNetworkNode.setFilters(filters);
    }

    @Override
    public long overrideAmount(final ResourceKey resource,
                               final long amount,
                               final LongSupplier currentAmountSupplier) {
        if (!upgradeContainer.has(Items.INSTANCE.getRegulatorUpgrade())) {
            return amount;
        }
        return upgradeContainer.getRegulatedAmount(resource)
            .stream()
            .map(desiredAmount -> getAmountStillNeeded(amount, currentAmountSupplier.getAsLong(), desiredAmount))
            .findFirst()
            .orElse(amount);
    }

    private long getAmountStillNeeded(final long amount, final long currentAmount, final long desiredAmount) {
        final long stillNeeding = desiredAmount - currentAmount;
        if (stillNeeding <= 0) {
            return 0;
        }
        return Math.min(stillNeeding, amount);
    }

    @Override
    protected boolean doesBlockStateChangeWarrantNetworkNodeUpdate(final BlockState oldBlockState,
                                                                   final BlockState newBlockState) {
        return AbstractDirectionalBlock.doesBlockStateChangeWarrantNetworkNodeUpdate(oldBlockState, newBlockState);
    }
}
