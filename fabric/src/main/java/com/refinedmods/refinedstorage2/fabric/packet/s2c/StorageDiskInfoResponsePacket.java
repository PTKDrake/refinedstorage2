package com.refinedmods.refinedstorage2.fabric.packet.s2c;

import java.util.UUID;

import com.refinedmods.refinedstorage2.core.storage.disk.ClientStorageDiskManager;
import com.refinedmods.refinedstorage2.fabric.RefinedStorage2Mod;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class StorageDiskInfoResponsePacket implements ClientPlayNetworking.PlayChannelHandler {
    public static final Identifier ID = RefinedStorage2Mod.createIdentifier("storage_disk_info_response");

    @Override
    public void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        UUID id = buf.readUuid();
        int stored = buf.readInt();
        int capacity = buf.readInt();
        client.execute(() -> {
            ((ClientStorageDiskManager) RefinedStorage2Mod.API.getStorageDiskManager(client.world)).setInfo(id, stored, capacity);
        });
    }
}
