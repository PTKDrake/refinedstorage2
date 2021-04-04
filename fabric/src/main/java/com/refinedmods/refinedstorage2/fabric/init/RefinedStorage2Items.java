package com.refinedmods.refinedstorage2.fabric.init;

import java.util.HashMap;
import java.util.Map;

import com.refinedmods.refinedstorage2.fabric.RefinedStorage2Mod;
import com.refinedmods.refinedstorage2.fabric.coreimpl.storage.disk.ItemStorageType;
import com.refinedmods.refinedstorage2.fabric.item.CoreItem;
import com.refinedmods.refinedstorage2.fabric.item.ProcessorBindingItem;
import com.refinedmods.refinedstorage2.fabric.item.ProcessorItem;
import com.refinedmods.refinedstorage2.fabric.item.QuartzEnrichedIronItem;
import com.refinedmods.refinedstorage2.fabric.item.SiliconItem;
import com.refinedmods.refinedstorage2.fabric.item.StorageDiskItem;
import com.refinedmods.refinedstorage2.fabric.item.StorageHousingItem;
import com.refinedmods.refinedstorage2.fabric.item.StoragePartItem;
import com.refinedmods.refinedstorage2.fabric.item.block.ColoredBlockItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.registry.Registry;

public class RefinedStorage2Items {
    private StorageHousingItem storageHousing;
    private final Map<ItemStorageType, StoragePartItem> storageParts = new HashMap<>();

    public void register(RefinedStorage2Blocks blocks, ItemGroup itemGroup) {
        Registry.register(Registry.ITEM, RefinedStorage2Mod.createIdentifier("cable"), new BlockItem(blocks.getCable(), createSettings(itemGroup)));
        Registry.register(Registry.ITEM, RefinedStorage2Mod.createIdentifier("quartz_enriched_iron"), new QuartzEnrichedIronItem(createSettings(itemGroup)));
        Registry.register(Registry.ITEM, RefinedStorage2Mod.createIdentifier("quartz_enriched_iron_block"), new BlockItem(blocks.getQuartzEnrichedIron(), createSettings(itemGroup)));
        Registry.register(Registry.ITEM, RefinedStorage2Mod.createIdentifier("silicon"), new SiliconItem(createSettings(itemGroup)));
        Registry.register(Registry.ITEM, RefinedStorage2Mod.createIdentifier("processor_binding"), new ProcessorBindingItem(createSettings(itemGroup)));
        Registry.register(Registry.ITEM, RefinedStorage2Mod.createIdentifier("disk_drive"), new BlockItem(blocks.getDiskDrive(), createSettings(itemGroup)));
        storageHousing = Registry.register(Registry.ITEM, RefinedStorage2Mod.createIdentifier("storage_housing"), new StorageHousingItem(createSettings(itemGroup)));
        Registry.register(Registry.ITEM, RefinedStorage2Mod.createIdentifier("machine_casing"), new BlockItem(blocks.getMachineCasing(), createSettings(itemGroup)));
        blocks.getGrid().forEach((color, block, nameFactory) -> Registry.register(Registry.ITEM, RefinedStorage2Mod.createIdentifier(nameFactory.apply("grid")), new ColoredBlockItem(block, createSettings(itemGroup), color, new TranslatableText("block.refinedstorage2.grid"))));

        for (ProcessorItem.Type type : ProcessorItem.Type.values()) {
            Registry.register(Registry.ITEM, RefinedStorage2Mod.createIdentifier(type.getName() + "_processor"), new ProcessorItem(createSettings(itemGroup)));
        }

        for (ItemStorageType type : ItemStorageType.values()) {
            if (type != ItemStorageType.CREATIVE) {
                storageParts.put(type, Registry.register(Registry.ITEM, RefinedStorage2Mod.createIdentifier(type.getName() + "_storage_part"), new StoragePartItem(createSettings(itemGroup))));
            }
        }

        for (ItemStorageType type : ItemStorageType.values()) {
            Registry.register(Registry.ITEM, RefinedStorage2Mod.createIdentifier(type.getName() + "_storage_disk"), new StorageDiskItem(createSettings(itemGroup).maxCount(1).fireproof(), type));
        }

        Registry.register(Registry.ITEM, RefinedStorage2Mod.createIdentifier("construction_core"), new CoreItem(createSettings(itemGroup)));
        Registry.register(Registry.ITEM, RefinedStorage2Mod.createIdentifier("destruction_core"), new CoreItem(createSettings(itemGroup)));
    }

    private Item.Settings createSettings(ItemGroup itemGroup) {
        return new Item.Settings().group(itemGroup);
    }

    public StorageHousingItem getStorageHousing() {
        return storageHousing;
    }

    public StoragePartItem getStoragePart(ItemStorageType type) {
        return storageParts.get(type);
    }
}
