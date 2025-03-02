package com.refinedmods.refinedstorage.api.grid.query;

import com.refinedmods.refinedstorage.api.grid.view.GridResourceAttributeKey;

import java.util.Map;
import java.util.Set;

enum GridResourceAttributeKeys implements GridResourceAttributeKey {
    MOD_ID,
    MOD_NAME,
    TAGS;

    public static final Map<String, Set<GridResourceAttributeKey>> UNARY_OPERATOR_TO_ATTRIBUTE_KEY_MAPPING = Map.of(
        "@", Set.of(MOD_ID, MOD_NAME),
        "$", Set.of(TAGS)
    );
}
