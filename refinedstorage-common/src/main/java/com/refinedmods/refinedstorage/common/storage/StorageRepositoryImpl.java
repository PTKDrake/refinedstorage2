package com.refinedmods.refinedstorage.common.storage;

import com.refinedmods.refinedstorage.api.core.CoreValidations;
import com.refinedmods.refinedstorage.common.api.RefinedStorageApi;
import com.refinedmods.refinedstorage.common.api.storage.SerializableStorage;
import com.refinedmods.refinedstorage.common.api.storage.StorageInfo;
import com.refinedmods.refinedstorage.common.api.storage.StorageRepository;
import com.refinedmods.refinedstorage.common.support.AbstractPlatformSavedData;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.mojang.serialization.Codec;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;

public class StorageRepositoryImpl extends AbstractPlatformSavedData implements StorageRepository {
    public static final String NAME = "refinedstorage_storages";

    private final Codec<Map<UUID, SerializableStorage>> codec;
    private final Map<UUID, SerializableStorage> entries;

    public StorageRepositoryImpl(final CompoundTag tag, final HolderLookup.Provider provider) {
        this.codec = createCodec(this::markAsChanged);
        this.entries = new HashMap<>(codec.decode(provider.createSerializationContext(NbtOps.INSTANCE), tag)
            .getOrThrow().getFirst());
    }

    public StorageRepositoryImpl() {
        this.codec = createCodec(this::markAsChanged);
        this.entries = new HashMap<>();
    }

    private static Codec<Map<UUID, SerializableStorage>> createCodec(final Runnable listener) {
        final Codec<SerializableStorage> storageCodec = RefinedStorageApi.INSTANCE.getStorageTypeRegistry()
            .codec()
            .dispatch(SerializableStorage::getType, storage -> storage.getMapCodec(listener));
        return new ErrorHandlingMapCodec<>(UUIDUtil.STRING_CODEC, storageCodec);
    }

    @Override
    public Optional<SerializableStorage> get(final UUID id) {
        return Optional.ofNullable(entries.get(id));
    }

    @Override
    public void set(final UUID id, final SerializableStorage storage) {
        CoreValidations.validateNotNull(storage, "Storage must not be null");
        CoreValidations.validateNotNull(id, "ID must not be null");
        if (entries.containsKey(id)) {
            throw new IllegalArgumentException(id + " already exists");
        }
        entries.put(id, storage);
        setDirty();
    }

    @Override
    public Optional<SerializableStorage> removeIfEmpty(final UUID id) {
        return get(id).map(storage -> {
            if (storage.getStored() == 0) {
                remove(id);
                return storage;
            }
            return null;
        });
    }

    @Override
    public void remove(final UUID id) {
        entries.remove(id);
        setDirty();
    }

    @Override
    public StorageInfo getInfo(final UUID id) {
        return get(id).map(StorageInfo::of).orElse(StorageInfo.UNKNOWN);
    }

    @Override
    public void markAsChanged() {
        setDirty();
    }

    @Override
    public CompoundTag save(final CompoundTag tag, final HolderLookup.Provider provider) {
        return (CompoundTag) codec.encode(entries, provider.createSerializationContext(NbtOps.INSTANCE), tag)
            .getOrThrow();
    }
}
