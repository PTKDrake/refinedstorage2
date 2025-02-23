package com.refinedmods.refinedstorage.common.support.exportingindicator;

@FunctionalInterface
public interface ExportingIndicatorListener {
    void indicatorChanged(int index, ExportingIndicator indicator);
}
