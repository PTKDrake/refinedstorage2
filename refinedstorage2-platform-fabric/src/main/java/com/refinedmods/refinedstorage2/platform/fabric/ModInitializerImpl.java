package com.refinedmods.refinedstorage2.platform.fabric;

import com.refinedmods.refinedstorage2.platform.api.PlatformApi;
import com.refinedmods.refinedstorage2.platform.api.resource.FluidResource;
import com.refinedmods.refinedstorage2.platform.api.resource.ItemResource;
import com.refinedmods.refinedstorage2.platform.common.AbstractModInitializer;
import com.refinedmods.refinedstorage2.platform.common.block.AbstractBaseBlock;
import com.refinedmods.refinedstorage2.platform.common.block.entity.InterfaceBlockEntity;
import com.refinedmods.refinedstorage2.platform.common.block.entity.diskdrive.AbstractDiskDriveBlockEntity;
import com.refinedmods.refinedstorage2.platform.common.content.BlockEntities;
import com.refinedmods.refinedstorage2.platform.common.content.BlockEntityTypeFactory;
import com.refinedmods.refinedstorage2.platform.common.content.Blocks;
import com.refinedmods.refinedstorage2.platform.common.content.CreativeModeTabItems;
import com.refinedmods.refinedstorage2.platform.common.content.DirectRegistryCallback;
import com.refinedmods.refinedstorage2.platform.common.content.MenuTypeFactory;
import com.refinedmods.refinedstorage2.platform.common.internal.network.node.iface.externalstorage.InterfacePlatformExternalStorageProviderFactory;
import com.refinedmods.refinedstorage2.platform.common.internal.storage.channel.StorageChannelTypes;
import com.refinedmods.refinedstorage2.platform.common.item.RegulatorUpgradeItem;
import com.refinedmods.refinedstorage2.platform.common.util.TickHandler;
import com.refinedmods.refinedstorage2.platform.fabric.block.entity.FabricDiskDriveBlockEntity;
import com.refinedmods.refinedstorage2.platform.fabric.integration.energy.ControllerTeamRebornEnergy;
import com.refinedmods.refinedstorage2.platform.fabric.internal.grid.FluidGridExtractionStrategy;
import com.refinedmods.refinedstorage2.platform.fabric.internal.grid.FluidGridInsertionStrategy;
import com.refinedmods.refinedstorage2.platform.fabric.internal.grid.ItemGridExtractionStrategy;
import com.refinedmods.refinedstorage2.platform.fabric.internal.grid.ItemGridScrollingStrategy;
import com.refinedmods.refinedstorage2.platform.fabric.internal.network.node.exporter.StorageExporterTransferStrategyFactory;
import com.refinedmods.refinedstorage2.platform.fabric.internal.network.node.externalstorage.StoragePlatformExternalStorageProviderFactory;
import com.refinedmods.refinedstorage2.platform.fabric.internal.network.node.importer.StorageImporterTransferStrategyFactory;
import com.refinedmods.refinedstorage2.platform.fabric.packet.PacketIds;
import com.refinedmods.refinedstorage2.platform.fabric.packet.c2s.CraftingGridClearPacket;
import com.refinedmods.refinedstorage2.platform.fabric.packet.c2s.CraftingGridRecipeTransferPacket;
import com.refinedmods.refinedstorage2.platform.fabric.packet.c2s.GridExtractPacket;
import com.refinedmods.refinedstorage2.platform.fabric.packet.c2s.GridInsertPacket;
import com.refinedmods.refinedstorage2.platform.fabric.packet.c2s.GridScrollPacket;
import com.refinedmods.refinedstorage2.platform.fabric.packet.c2s.PropertyChangePacket;
import com.refinedmods.refinedstorage2.platform.fabric.packet.c2s.ResourceSlotAmountChangePacket;
import com.refinedmods.refinedstorage2.platform.fabric.packet.c2s.ResourceSlotChangePacket;
import com.refinedmods.refinedstorage2.platform.fabric.packet.c2s.SingleAmountChangePacket;
import com.refinedmods.refinedstorage2.platform.fabric.packet.c2s.StorageInfoRequestPacket;
import com.refinedmods.refinedstorage2.platform.fabric.util.VariantUtil;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.reborn.energy.api.EnergyStorage;

import static com.refinedmods.refinedstorage2.platform.common.util.IdentifierUtil.createIdentifier;
import static com.refinedmods.refinedstorage2.platform.common.util.IdentifierUtil.createTranslation;

public class ModInitializerImpl extends AbstractModInitializer implements ModInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger(ModInitializerImpl.class);

    @Override
    public void onInitialize() {
        AutoConfig.register(ConfigImpl.class, Toml4jConfigSerializer::new);

        initializePlatform(new PlatformImpl());
        initializePlatformApi();
        registerAdditionalGridInsertionStrategyFactories();
        registerGridExtractionStrategyFactories();
        registerGridScrollingStrategyFactories();
        registerImporterTransferStrategyFactories();
        registerExporterTransferStrategyFactories();
        registerExternalStorageProviderFactories();
        registerContent();
        registerPackets();
        registerSounds(new DirectRegistryCallback<>(BuiltInRegistries.SOUND_EVENT));
        registerRecipeSerializers(new DirectRegistryCallback<>(BuiltInRegistries.RECIPE_SERIALIZER));
        registerSidedHandlers();
        registerTickHandler();
        registerWrenchingEvent();

        LOGGER.info("Refined Storage 2 has loaded.");
    }

    private void registerAdditionalGridInsertionStrategyFactories() {
        PlatformApi.INSTANCE.addGridInsertionStrategyFactory(FluidGridInsertionStrategy::new);
    }

    private void registerGridExtractionStrategyFactories() {
        PlatformApi.INSTANCE.addGridExtractionStrategyFactory(
            (containerMenu, player, gridServiceFactory, itemStorage) ->
                new ItemGridExtractionStrategy(containerMenu, player, gridServiceFactory)
        );
        PlatformApi.INSTANCE.addGridExtractionStrategyFactory(FluidGridExtractionStrategy::new);
    }

    private void registerGridScrollingStrategyFactories() {
        PlatformApi.INSTANCE.addGridScrollingStrategyFactory(ItemGridScrollingStrategy::new);
    }

    private void registerImporterTransferStrategyFactories() {
        PlatformApi.INSTANCE.getImporterTransferStrategyRegistry().register(
            createIdentifier("item"),
            new StorageImporterTransferStrategyFactory<>(
                ItemStorage.SIDED,
                StorageChannelTypes.ITEM,
                VariantUtil::ofItemVariant,
                VariantUtil::toItemVariant,
                1
            )
        );
        PlatformApi.INSTANCE.getImporterTransferStrategyRegistry().register(
            createIdentifier("fluid"),
            new StorageImporterTransferStrategyFactory<>(
                FluidStorage.SIDED,
                StorageChannelTypes.FLUID,
                VariantUtil::ofFluidVariant,
                VariantUtil::toFluidVariant,
                FluidConstants.BUCKET
            )
        );
    }

    private void registerExporterTransferStrategyFactories() {
        PlatformApi.INSTANCE.getExporterTransferStrategyRegistry().register(
            createIdentifier("item"),
            new StorageExporterTransferStrategyFactory<>(
                ItemStorage.SIDED,
                StorageChannelTypes.ITEM,
                resource -> resource instanceof ItemResource itemResource
                    ? Optional.of(itemResource)
                    : Optional.empty(),
                VariantUtil::toItemVariant,
                1
            )
        );
        PlatformApi.INSTANCE.getExporterTransferStrategyRegistry().register(
            createIdentifier("fluid"),
            new StorageExporterTransferStrategyFactory<>(
                FluidStorage.SIDED,
                StorageChannelTypes.FLUID,
                resource -> resource instanceof FluidResource fluidResource
                    ? Optional.of(fluidResource)
                    : Optional.empty(),
                VariantUtil::toFluidVariant,
                FluidConstants.BUCKET
            )
        );
    }

    private void registerExternalStorageProviderFactories() {
        PlatformApi.INSTANCE.addExternalStorageProviderFactory(
            StorageChannelTypes.ITEM,
            new InterfacePlatformExternalStorageProviderFactory()
        );
        PlatformApi.INSTANCE.addExternalStorageProviderFactory(
            StorageChannelTypes.ITEM,
            new StoragePlatformExternalStorageProviderFactory<>(
                ItemStorage.SIDED,
                VariantUtil::ofItemVariant,
                VariantUtil::toItemVariant
            )
        );
        PlatformApi.INSTANCE.addExternalStorageProviderFactory(
            StorageChannelTypes.FLUID,
            new StoragePlatformExternalStorageProviderFactory<>(
                FluidStorage.SIDED,
                VariantUtil::ofFluidVariant,
                VariantUtil::toFluidVariant
            )
        );
    }

    private void registerContent() {
        registerBlocks(new DirectRegistryCallback<>(BuiltInRegistries.BLOCK), FabricDiskDriveBlockEntity::new);
        registerItems(
            new DirectRegistryCallback<>(BuiltInRegistries.ITEM),
            () -> new RegulatorUpgradeItem(PlatformApi.INSTANCE.getUpgradeRegistry()) {
                @Override
                public boolean allowNbtUpdateAnimation(final Player player,
                                                       final InteractionHand hand,
                                                       final ItemStack oldStack,
                                                       final ItemStack newStack) {
                    return RegulatorUpgradeItem.allowNbtUpdateAnimation(oldStack, newStack);
                }
            }
        );
        registerUpgradeMappings();
        registerCreativeModeTab();
        registerBlockEntities(
            new DirectRegistryCallback<>(BuiltInRegistries.BLOCK_ENTITY_TYPE),
            new BlockEntityTypeFactory() {
                @Override
                public <T extends BlockEntity> BlockEntityType<T> create(final BlockEntitySupplier<T> factory,
                                                                         final Block... allowedBlocks) {
                    return new BlockEntityType<>(factory::create, new HashSet<>(Arrays.asList(allowedBlocks)), null);
                }
            },
            FabricDiskDriveBlockEntity::new
        );
        registerMenus(new DirectRegistryCallback<>(BuiltInRegistries.MENU), new MenuTypeFactory() {
            @Override
            public <T extends AbstractContainerMenu> MenuType<T> create(final MenuSupplier<T> supplier) {
                return new ExtendedScreenHandlerType<>(supplier::create);
            }
        });
        registerLootFunctions(new DirectRegistryCallback<>(BuiltInRegistries.LOOT_FUNCTION_TYPE));
    }

    private void registerCreativeModeTab() {
        Registry.register(
            BuiltInRegistries.CREATIVE_MODE_TAB,
            createIdentifier("general"),
            CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
                .title(createTranslation("itemGroup", "general"))
                .icon(() -> new ItemStack(Blocks.INSTANCE.getController().getDefault()))
                .displayItems((params, output) -> CreativeModeTabItems.append(output::accept))
                .build()
        );
    }

    private void registerPackets() {
        ServerPlayNetworking.registerGlobalReceiver(PacketIds.STORAGE_INFO_REQUEST, new StorageInfoRequestPacket());
        ServerPlayNetworking.registerGlobalReceiver(PacketIds.GRID_INSERT, new GridInsertPacket());
        ServerPlayNetworking.registerGlobalReceiver(PacketIds.GRID_EXTRACT, new GridExtractPacket());
        ServerPlayNetworking.registerGlobalReceiver(PacketIds.GRID_SCROLL, new GridScrollPacket());
        ServerPlayNetworking.registerGlobalReceiver(PacketIds.CRAFTING_GRID_CLEAR, new CraftingGridClearPacket());
        ServerPlayNetworking.registerGlobalReceiver(
            PacketIds.CRAFTING_GRID_RECIPE_TRANSFER,
            new CraftingGridRecipeTransferPacket()
        );
        ServerPlayNetworking.registerGlobalReceiver(PacketIds.PROPERTY_CHANGE, new PropertyChangePacket());
        ServerPlayNetworking.registerGlobalReceiver(
            PacketIds.RESOURCE_SLOT_AMOUNT_CHANGE,
            new ResourceSlotAmountChangePacket()
        );
        ServerPlayNetworking.registerGlobalReceiver(
            PacketIds.RESOURCE_SLOT_CHANGE,
            new ResourceSlotChangePacket()
        );
        ServerPlayNetworking.registerGlobalReceiver(PacketIds.SINGLE_AMOUNT_CHANGE, new SingleAmountChangePacket());
    }

    private void registerSidedHandlers() {
        registerItemStorage(
            AbstractDiskDriveBlockEntity.class::isInstance,
            AbstractDiskDriveBlockEntity.class::cast,
            AbstractDiskDriveBlockEntity::getDiskInventory,
            BlockEntities.INSTANCE.getDiskDrive()
        );
        registerItemStorage(
            InterfaceBlockEntity.class::isInstance,
            InterfaceBlockEntity.class::cast,
            InterfaceBlockEntity::getExportedItems,
            BlockEntities.INSTANCE.getInterface()
        );
        registerControllerEnergy();
    }

    private <T extends BlockEntity> void registerItemStorage(final Predicate<BlockEntity> test,
                                                             final Function<BlockEntity, T> caster,
                                                             final Function<T, Container> containerSupplier,
                                                             final BlockEntityType<?> type) {
        ItemStorage.SIDED.registerForBlockEntities((blockEntity, context) -> {
            if (test.test(blockEntity)) {
                final T casted = caster.apply(blockEntity);
                return InventoryStorage.of(containerSupplier.apply(casted), context);
            }
            return null;
        }, type);
    }

    private void registerControllerEnergy() {
        EnergyStorage.SIDED.registerForBlockEntity(
            (be, direction) -> ((ControllerTeamRebornEnergy) be.getEnergyStorage()).getExposedStorage(),
            BlockEntities.INSTANCE.getController()
        );
    }

    private void registerTickHandler() {
        ServerTickEvents.START_SERVER_TICK.register(server -> TickHandler.runQueuedActions());
    }

    private void registerWrenchingEvent() {
        UseBlockCallback.EVENT.register((player, level, hand, hitResult) -> {
            final BlockState state = level.getBlockState(hitResult.getBlockPos());
            return AbstractBaseBlock.tryUseWrench(state, level, hitResult, player, hand)
                .or(() -> AbstractBaseBlock.tryUpdateColor(state, level, hitResult.getBlockPos(), player, hand))
                .orElse(InteractionResult.PASS);
        });
    }
}
