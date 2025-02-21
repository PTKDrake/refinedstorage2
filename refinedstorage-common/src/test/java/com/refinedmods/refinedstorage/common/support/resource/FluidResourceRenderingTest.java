package com.refinedmods.refinedstorage.common.support.resource;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FluidResourceRenderingTest {
    private static final long BUCKET_AMOUNT = 1000;

    private final FluidResourceRendering sut = new FluidResourceRendering(BUCKET_AMOUNT);

    @Test
    void shouldFormatWithUnitsForCompleteBuckets() {
        assertThat(sut.formatAmount(BUCKET_AMOUNT, true)).isEqualTo("1b");
        assertThat(sut.formatAmount(BUCKET_AMOUNT * 2, true)).isEqualTo("2b");
        assertThat(sut.formatAmount(BUCKET_AMOUNT * 3, true)).isEqualTo("3b");
        assertThat(sut.formatAmount(BUCKET_AMOUNT * 1000, true)).isEqualTo("1kb");
    }

    @Test
    void shouldFormatWithUnitsForPartialBuckets() {
        assertThat(sut.formatAmount(BUCKET_AMOUNT + (BUCKET_AMOUNT / 2), true)).isEqualTo("1.5b");
        assertThat(sut.formatAmount(BUCKET_AMOUNT + 1, true)).isEqualTo("1b");
    }

    @Test
    void shouldFormatWithUnitsForLessThan1Bucket() {
        assertThat(sut.formatAmount(BUCKET_AMOUNT / 2, true)).isEqualTo("500mb");
        assertThat(sut.formatAmount(BUCKET_AMOUNT / 3, true)).isEqualTo("333mb");
    }

    @Test
    void shouldFormatWithoutUnits() {
        assertThat(sut.formatAmount(BUCKET_AMOUNT)).isEqualTo("1b");
        assertThat(sut.formatAmount(BUCKET_AMOUNT + (BUCKET_AMOUNT / 2))).isEqualTo("1.5b");
        assertThat(sut.formatAmount(BUCKET_AMOUNT + (BUCKET_AMOUNT / 3))).isEqualTo("1.333b");
        assertThat(sut.formatAmount(BUCKET_AMOUNT * 1000)).isEqualTo("1,000b");
        assertThat(sut.formatAmount((BUCKET_AMOUNT * 1000) + (BUCKET_AMOUNT / 3))).isEqualTo("1,000.333b");
    }
}
