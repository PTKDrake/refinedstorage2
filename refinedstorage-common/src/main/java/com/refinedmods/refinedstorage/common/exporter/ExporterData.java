package com.refinedmods.refinedstorage.common.exporter;

import com.refinedmods.refinedstorage.common.support.exportingindicator.ExportingIndicator;
import com.refinedmods.refinedstorage.common.support.resource.ResourceContainerData;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import static com.refinedmods.refinedstorage.common.util.PlatformUtil.enumStreamCodec;

public record ExporterData(ResourceContainerData resourceContainerData, List<ExportingIndicator> exportingIndicators) {
    public static final StreamCodec<RegistryFriendlyByteBuf, ExporterData> STREAM_CODEC = StreamCodec.composite(
        ResourceContainerData.STREAM_CODEC, ExporterData::resourceContainerData,
        ByteBufCodecs.collection(ArrayList::new, enumStreamCodec(ExportingIndicator.values())),
        ExporterData::exportingIndicators,
        ExporterData::new
    );
}
