package com.refinedmods.refinedstorage.common.support.packet.s2c;

import com.refinedmods.refinedstorage.common.support.exportingindicator.ExportingIndicator;
import com.refinedmods.refinedstorage.common.support.exportingindicator.ExportingIndicatorListener;
import com.refinedmods.refinedstorage.common.support.packet.PacketContext;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.inventory.AbstractContainerMenu;

import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createIdentifier;
import static com.refinedmods.refinedstorage.common.util.PlatformUtil.enumStreamCodec;

public record ExportingIndicatorUpdatePacket(List<UpdatedIndicator> updatedIndicators) implements CustomPacketPayload {
    public static final Type<ExportingIndicatorUpdatePacket> PACKET_TYPE = new Type<>(createIdentifier(
        "exporting_indicator_update"
    ));
    public static final StreamCodec<RegistryFriendlyByteBuf, ExportingIndicatorUpdatePacket> STREAM_CODEC =
        StreamCodec.composite(
            ByteBufCodecs.collection(ArrayList::new, UpdatedIndicator.CODEC),
            ExportingIndicatorUpdatePacket::updatedIndicators,
            ExportingIndicatorUpdatePacket::new
        );

    public static void handle(final ExportingIndicatorUpdatePacket packet, final PacketContext ctx) {
        final AbstractContainerMenu menu = ctx.getPlayer().containerMenu;
        if (menu instanceof ExportingIndicatorListener listener) {
            packet.updatedIndicators.forEach(
                updatedIndicator -> listener.indicatorChanged(updatedIndicator.idx(), updatedIndicator.indicator())
            );
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return PACKET_TYPE;
    }

    public record UpdatedIndicator(int idx, ExportingIndicator indicator) {
        private static final StreamCodec<RegistryFriendlyByteBuf, UpdatedIndicator> CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, UpdatedIndicator::idx,
            enumStreamCodec(ExportingIndicator.values()), UpdatedIndicator::indicator,
            UpdatedIndicator::new
        );
    }
}
