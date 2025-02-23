package com.refinedmods.refinedstorage.common.networking;

import net.minecraft.nbt.CompoundTag;

public record CableConnections(boolean north, boolean east, boolean south, boolean west, boolean up, boolean down) {
    public static final CableConnections NONE = new CableConnections(false, false, false, false, false, false);

    private static final String TAG_NORTH = "North";
    private static final String TAG_EAST = "East";
    private static final String TAG_SOUTH = "South";
    private static final String TAG_WEST = "West";
    private static final String TAG_UP = "Up";
    private static final String TAG_DOWN = "Down";

    public static CableConnections fromTag(final CompoundTag tag) {
        return new CableConnections(
            tag.getBoolean(TAG_NORTH),
            tag.getBoolean(TAG_EAST),
            tag.getBoolean(TAG_SOUTH),
            tag.getBoolean(TAG_WEST),
            tag.getBoolean(TAG_UP),
            tag.getBoolean(TAG_DOWN)
        );
    }

    public CompoundTag writeToTag(final CompoundTag tag) {
        tag.putBoolean(TAG_NORTH, north);
        tag.putBoolean(TAG_EAST, east);
        tag.putBoolean(TAG_SOUTH, south);
        tag.putBoolean(TAG_WEST, west);
        tag.putBoolean(TAG_UP, up);
        tag.putBoolean(TAG_DOWN, down);
        return tag;
    }

    public static void stripTag(final CompoundTag tag) {
        tag.remove(TAG_DOWN);
        tag.remove(TAG_UP);
        tag.remove(TAG_WEST);
        tag.remove(TAG_NORTH);
        tag.remove(TAG_SOUTH);
        tag.remove(TAG_EAST);
    }
}
