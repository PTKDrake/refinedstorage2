package com.refinedmods.refinedstorage.api.autocrafting.calculation;

import com.refinedmods.refinedstorage.api.resource.ResourceKey;

import org.apiguardian.api.API;

@API(status = API.Status.STABLE, since = "2.0.0-milestone.4.12")
public interface CraftingCalculatorListener<T> {
    CraftingCalculatorListener<T> childCalculationStarted(ResourceKey resource, long amount);

    void childCalculationCompleted(CraftingCalculatorListener<T> childListener);

    void ingredientsExhausted(ResourceKey resource, long amount);

    void ingredientExtractedFromStorage(ResourceKey resource, long amount);

    T getData();
}
