package com.refinedmods.refinedstorage.common.api.storage.externalstorage;

import com.refinedmods.refinedstorage.api.storage.external.ExternalStorageProvider;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import org.apiguardian.api.API;

@API(status = API.Status.STABLE, since = "2.0.0-milestone.2.4")
@FunctionalInterface
public interface ExternalStorageProviderFactory {
    ExternalStorageProvider create(ServerLevel level, BlockPos pos, Direction direction);
}
