package com.refinedmods.refinedstorage2.platform.common.containermenu.storage.block;

import com.refinedmods.refinedstorage2.platform.api.PlatformApi;
import com.refinedmods.refinedstorage2.platform.common.containermenu.storage.StorageConfigurationContainer;
import com.refinedmods.refinedstorage2.platform.common.content.Menus;
import com.refinedmods.refinedstorage2.platform.common.internal.resource.ResourceContainer;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;

public class FluidStorageBlockContainerMenu extends AbstractStorageBlockContainerMenu {
    public FluidStorageBlockContainerMenu(final int syncId,
                                          final Inventory playerInventory,
                                          final FriendlyByteBuf buf) {
        super(
            Menus.INSTANCE.getFluidStorage(),
            syncId,
            playerInventory.player,
            buf,
            PlatformApi.INSTANCE.getFluidResourceFactory()
        );
    }

    public FluidStorageBlockContainerMenu(final int syncId,
                                          final Player player,
                                          final ResourceContainer resourceContainer,
                                          final StorageConfigurationContainer configContainer) {
        super(Menus.INSTANCE.getFluidStorage(), syncId, player, resourceContainer, configContainer);
    }

    @Override
    public boolean hasCapacity() {
        return getCapacity() > 0;
    }
}
