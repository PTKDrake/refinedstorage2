package com.refinedmods.refinedstorage.common.constructordestructor;

import com.refinedmods.refinedstorage.common.support.exportingindicator.ExportingIndicator;
import com.refinedmods.refinedstorage.common.support.resource.ResourceContainerData;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import static com.refinedmods.refinedstorage.common.util.PlatformUtil.enumStreamCodec;

public record ConstructorData(ResourceContainerData resourceContainerData,
                              List<ExportingIndicator> exportingIndicators) {
    public static final StreamCodec<RegistryFriendlyByteBuf, ConstructorData> STREAM_CODEC = StreamCodec.composite(
        ResourceContainerData.STREAM_CODEC, ConstructorData::resourceContainerData,
        ByteBufCodecs.collection(ArrayList::new, enumStreamCodec(ExportingIndicator.values())),
        ConstructorData::exportingIndicators,
        ConstructorData::new
    );
}
