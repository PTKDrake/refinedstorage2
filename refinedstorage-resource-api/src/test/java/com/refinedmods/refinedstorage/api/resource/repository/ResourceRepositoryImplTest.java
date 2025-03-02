package com.refinedmods.refinedstorage.api.resource.repository;

import com.refinedmods.refinedstorage.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.api.resource.list.ResourceList;

import java.util.Comparator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static com.refinedmods.refinedstorage.api.resource.TestResource.A;
import static com.refinedmods.refinedstorage.api.resource.TestResource.B;
import static com.refinedmods.refinedstorage.api.resource.TestResource.C;
import static com.refinedmods.refinedstorage.api.resource.TestResource.D;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

class ResourceRepositoryImplTest {
    private ResourceRepositoryBuilder<ResourceKey> builder;

    @BeforeEach
    void setUp() {
        builder = new ResourceRepositoryBuilderImpl<>(
            resource -> resource,
            repository -> Comparator.comparing(ResourceKey::toString),
            repository -> Comparator.comparingLong(repository::getAmount)
        );
    }

    @Test
    void shouldPreserveOrderWhenSortingAndTwoResourcesHaveTheSameQuantity() {
        // Arrange
        final ResourceRepository<ResourceKey> sut = builder.build();
        sut.setSort(Comparator.comparingLong(sut::getAmount), SortingDirection.DESCENDING);

        // Act & assert
        sut.onChange(A, 10);
        sut.onChange(A, 5);
        sut.onChange(B, 15);
        sut.onChange(C, 2);

        assertThat(sut.getViewList()).usingRecursiveFieldByFieldElementComparator().containsExactly(B, A, C);

        sut.onChange(A, -15);
        sut.onChange(A, 15);

        sut.onChange(B, -15);
        sut.onChange(B, 15);

        assertThat(sut.getViewList()).usingRecursiveFieldByFieldElementComparator().containsExactly(B, A, C);
    }

    @Test
    void shouldLoadResources() {
        // Arrange
        final ResourceRepository<ResourceKey> sut = builder
            .addResource(A, 1)
            .addResource(A, 1)
            .addResource(B, 1)
            .addResource(B, 1)
            .addResource(D, 1)
            .build();

        // Act
        final ResourceList backingList = sut.copyBackingList();

        // Assert
        assertThat(backingList.copyState()).usingRecursiveFieldByFieldElementComparator().containsExactlyInAnyOrder(
            new ResourceAmount(A, 2),
            new ResourceAmount(B, 2),
            new ResourceAmount(D, 1)
        );
        assertThat(sut.getAmount(A)).isEqualTo(2);
        assertThat(sut.getAmount(B)).isEqualTo(2);
        assertThat(sut.getAmount(C)).isZero();
        assertThat(sut.getAmount(D)).isEqualTo(1);
    }

    @Test
    void shouldAddNewResource() {
        // Arrange
        final ResourceRepository<ResourceKey> sut = builder
            .addResource(B, 15)
            .addResource(D, 10)
            .build();

        sut.sort();

        final Runnable listener = mock(Runnable.class);
        sut.setListener(listener);

        // Act
        sut.onChange(A, 12);

        // Assert
        assertThat(sut.getViewList()).usingRecursiveFieldByFieldElementComparator().containsExactly(D, A, B);
        assertThat(sut.copyBackingList().copyState())
            .usingRecursiveFieldByFieldElementComparator()
            .containsExactlyInAnyOrder(
                new ResourceAmount(D, 10),
                new ResourceAmount(A, 12),
                new ResourceAmount(B, 15)
            );
        verify(listener, times(1)).run();
        assertThat(sut.getAmount(A)).isEqualTo(12);
        assertThat(sut.getAmount(B)).isEqualTo(15);
        assertThat(sut.getAmount(D)).isEqualTo(10);
    }

    @Test
    void shouldSetFilterAndSort() {
        // Arrange
        final ResourceRepository<ResourceKey> sut = builder
            .addResource(A, 10)
            .addResource(B, 10)
            .build();

        final ResourceRepositoryFilter<ResourceKey> filter1 = (v, resource) -> resource == A;
        final ResourceRepositoryFilter<ResourceKey> filter2 = (v, resource) -> resource == B;

        // Act
        final ResourceRepositoryFilter<ResourceKey> previousFilter1 = sut.setFilterAndSort(filter1);
        final ResourceRepositoryFilter<ResourceKey> previousFilter2 = sut.setFilterAndSort(filter2);

        // Assert
        assertThat(previousFilter1).isNotNull();
        assertThat(previousFilter2).isEqualTo(filter1);
        assertThat(sut.getViewList()).usingRecursiveFieldByFieldElementComparator().containsExactly(B);
        assertThat(sut.getAmount(A)).isEqualTo(10);
        assertThat(sut.getAmount(B)).isEqualTo(10);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void shouldNotAddNewResourceWhenFilteringDoesNotAllowIt(final boolean sticky) {
        // Arrange
        builder.addResource(B, 15).addResource(D, 10);
        if (sticky) {
            builder.addStickyResource(A);
        }
        final ResourceRepository<ResourceKey> sut = builder.build();

        sut.setFilterAndSort((v, resource) -> resource != A);

        final Runnable listener = mock(Runnable.class);
        sut.setListener(listener);

        // Act
        sut.onChange(A, 12);

        // Assert
        assertThat(sut.getViewList()).usingRecursiveFieldByFieldElementComparator().containsExactly(D, B);
        verify(listener, never()).run();
        assertThat(sut.getAmount(A)).isEqualTo(12);
        assertThat(sut.getAmount(B)).isEqualTo(15);
        assertThat(sut.getAmount(D)).isEqualTo(10);
    }

    @Test
    void shouldNotifyListenerWhenSorting() {
        // Arrange
        final ResourceRepository<ResourceKey> sut = builder
            .addResource(B, 6)
            .addResource(A, 15)
            .addResource(D, 10)
            .build();

        final Runnable listener = mock(Runnable.class);
        sut.setListener(listener);

        // Act
        sut.sort();

        // Assert
        verify(listener, times(1)).run();
        verifyNoMoreInteractions(listener);
    }

    @Test
    void shouldUpdateExistingResource() {
        // Arrange
        final ResourceRepository<ResourceKey> sut = builder
            .addResource(B, 6)
            .addResource(A, 15)
            .addResource(D, 10)
            .build();

        sut.sort();

        final Runnable listener = mock(Runnable.class);
        sut.setListener(listener);

        // Act
        sut.onChange(B, 5);

        // Assert
        assertThat(sut.getViewList()).usingRecursiveFieldByFieldElementComparator().containsExactly(D, B, A);
        assertThat(sut.copyBackingList().copyState())
            .usingRecursiveFieldByFieldElementComparator()
            .containsExactlyInAnyOrder(
                new ResourceAmount(D, 10),
                new ResourceAmount(B, 11),
                new ResourceAmount(A, 15)
            );
        verify(listener, times(1)).run();
        assertThat(sut.getAmount(A)).isEqualTo(15);
        assertThat(sut.getAmount(B)).isEqualTo(11);
        assertThat(sut.getAmount(D)).isEqualTo(10);
    }

    @Test
    void shouldNotUpdateExistingResourceWhenFilteringDoesNotAllowIt() {
        // Arrange
        final ResourceRepository<ResourceKey> sut = builder
            .addResource(B, 6)
            .addResource(A, 15)
            .addResource(D, 10)
            .build();

        sut.setFilterAndSort((v, resource) -> resource != B);

        final Runnable listener = mock(Runnable.class);
        sut.setListener(listener);

        // Act
        sut.onChange(B, 5);

        // Assert
        assertThat(sut.getViewList()).usingRecursiveFieldByFieldElementComparator().containsExactly(D, A);
        assertThat(sut.copyBackingList().copyState())
            .usingRecursiveFieldByFieldElementComparator()
            .containsExactlyInAnyOrder(
                new ResourceAmount(B, 11),
                new ResourceAmount(D, 10),
                new ResourceAmount(A, 15)
            );
        verify(listener, never()).run();
        assertThat(sut.getAmount(A)).isEqualTo(15);
        assertThat(sut.getAmount(B)).isEqualTo(11);
        assertThat(sut.getAmount(D)).isEqualTo(10);
    }

    @Test
    void shouldNotReorderExistingResourceWhenPreventingSorting() {
        // Arrange
        final ResourceRepository<ResourceKey> sut = builder
            .addResource(B, 6)
            .addResource(A, 15)
            .addResource(D, 10)
            .build();

        sut.sort();

        final Runnable listener = mock(Runnable.class);
        sut.setListener(listener);

        // Act & assert
        assertThat(sut.getViewList()).usingRecursiveFieldByFieldElementComparator().containsExactly(B, D, A);

        final boolean changed = sut.setPreventSorting(true);
        assertThat(changed).isTrue();
        final boolean changed2 = sut.setPreventSorting(true);
        assertThat(changed2).isFalse();

        sut.onChange(B, 5);
        verify(listener, never()).run();

        assertThat(sut.getViewList()).usingRecursiveFieldByFieldElementComparator().containsExactly(B, D, A);
        assertThat(sut.copyBackingList().copyState())
            .usingRecursiveFieldByFieldElementComparator()
            .containsExactlyInAnyOrder(
                new ResourceAmount(B, 11),
                new ResourceAmount(D, 10),
                new ResourceAmount(A, 15)
            );

        final boolean changed3 = sut.setPreventSorting(false);
        assertThat(changed3).isTrue();
        sut.sort();

        assertThat(sut.getViewList()).usingRecursiveFieldByFieldElementComparator().containsExactly(D, B, A);
        assertThat(sut.copyBackingList().copyState())
            .usingRecursiveFieldByFieldElementComparator()
            .containsExactlyInAnyOrder(
                new ResourceAmount(B, 11),
                new ResourceAmount(D, 10),
                new ResourceAmount(A, 15)
            );
    }

    @Test
    void shouldUpdateExistingResourceOnPartialRemoval() {
        // Arrange
        final ResourceRepository<ResourceKey> sut = builder
            .addResource(B, 20)
            .addResource(A, 15)
            .addResource(D, 10)
            .build();

        sut.sort();

        final Runnable listener = mock(Runnable.class);
        sut.setListener(listener);

        // Act
        sut.onChange(B, -7);

        // Assert
        assertThat(sut.getViewList()).usingRecursiveFieldByFieldElementComparator().containsExactly(D, B, A);
        assertThat(sut.copyBackingList().copyState())
            .usingRecursiveFieldByFieldElementComparator()
            .containsExactlyInAnyOrder(
                new ResourceAmount(D, 10),
                new ResourceAmount(B, 13),
                new ResourceAmount(A, 15)
            );
        verify(listener, times(1)).run();
        assertThat(sut.getAmount(A)).isEqualTo(15);
        assertThat(sut.getAmount(B)).isEqualTo(13);
        assertThat(sut.getAmount(D)).isEqualTo(10);
    }

    @Test
    void shouldNotUpdateExistingResourceOnPartialRemovalAndFilteringDoesNotAllowIt() {
        // Arrange
        final ResourceRepository<ResourceKey> sut = builder
            .addResource(B, 20)
            .addResource(A, 15)
            .addResource(D, 10)
            .build();

        sut.setFilterAndSort((v, resource) -> resource != B);

        final Runnable listener = mock(Runnable.class);
        sut.setListener(listener);

        // Act
        sut.onChange(B, -7);

        // Assert
        assertThat(sut.getViewList()).usingRecursiveFieldByFieldElementComparator().containsExactly(D, A);
        verify(listener, never()).run();
        assertThat(sut.getAmount(A)).isEqualTo(15);
        assertThat(sut.getAmount(B)).isEqualTo(13);
        assertThat(sut.getAmount(D)).isEqualTo(10);
    }

    @Test
    void shouldNotReorderExistingResourceOnPartialRemovalAndPreventingSorting() {
        // Arrange
        final ResourceRepository<ResourceKey> sut = builder
            .addResource(B, 20)
            .addResource(A, 15)
            .addResource(D, 10)
            .build();

        sut.sort();

        final Runnable listener = mock(Runnable.class);
        sut.setListener(listener);

        // Act & assert
        assertThat(sut.getViewList()).usingRecursiveFieldByFieldElementComparator().containsExactly(D, A, B);

        sut.setPreventSorting(true);

        sut.onChange(B, -7);
        verify(listener, never()).run();

        assertThat(sut.getViewList()).usingRecursiveFieldByFieldElementComparator().containsExactly(D, A, B);

        sut.setPreventSorting(false);
        sut.sort();

        assertThat(sut.getViewList()).usingRecursiveFieldByFieldElementComparator().containsExactly(D, B, A);
    }

    @Test
    void shouldNotRemoveNonExistentResource() {
        // Arrange
        final ResourceRepository<ResourceKey> sut = builder
            .addResource(B, 20)
            .addResource(A, 15)
            .addResource(D, 10)
            .build();

        sut.sort();

        final Runnable listener = mock(Runnable.class);
        sut.setListener(listener);

        // Act
        sut.onChange(C, -7);

        // Assert
        assertThat(sut.getViewList()).usingRecursiveFieldByFieldElementComparator().containsExactly(D, A, B);
        verify(listener, never()).run();
        assertThat(sut.getAmount(A)).isEqualTo(15);
        assertThat(sut.getAmount(B)).isEqualTo(20);
        assertThat(sut.getAmount(D)).isEqualTo(10);
    }

    @Test
    void shouldRemoveExistingResourceCompletely() {
        // Arrange
        final ResourceRepository<ResourceKey> sut = builder
            .addResource(B, 20)
            .addResource(A, 15)
            .addResource(D, 10)
            .build();

        sut.sort();

        final Runnable listener = mock(Runnable.class);
        sut.setListener(listener);

        // Act
        sut.onChange(B, -20);

        // Assert
        assertThat(sut.getViewList()).usingRecursiveFieldByFieldElementComparator().containsExactly(D, A);
        assertThat(sut.copyBackingList().copyState())
            .usingRecursiveFieldByFieldElementComparator()
            .containsExactlyInAnyOrder(
                new ResourceAmount(D, 10),
                new ResourceAmount(A, 15)
            );
        verify(listener, times(1)).run();
        assertThat(sut.getAmount(A)).isEqualTo(15);
        assertThat(sut.getAmount(B)).isZero();
        assertThat(sut.getAmount(D)).isEqualTo(10);
    }

    @Test
    void shouldNotReorderWhenRemovingExistingResourceCompletelyAndPreventingSorting() {
        // Arrange
        final ResourceRepository<ResourceKey> sut = builder
            .addResource(A, 15)
            .addResource(B, 20)
            .addResource(D, 10)
            .build();

        sut.sort();

        final Runnable listener = mock(Runnable.class);
        sut.setListener(listener);

        // Act & assert
        assertThat(sut.getViewList()).usingRecursiveFieldByFieldElementComparator().containsExactly(D, A, B);

        sut.setPreventSorting(true);
        sut.onChange(B, -20);
        verify(listener, never()).run();

        assertThat(sut.getViewList()).usingRecursiveFieldByFieldElementComparator().containsExactly(D, A, B);
        assertThat(sut.copyBackingList().copyState())
            .usingRecursiveFieldByFieldElementComparator()
            .containsExactlyInAnyOrder(
                new ResourceAmount(D, 10),
                new ResourceAmount(A, 15)
            );

        sut.setPreventSorting(false);
        sut.sort();

        assertThat(sut.getViewList()).usingRecursiveFieldByFieldElementComparator().containsExactly(D, A);
        assertThat(sut.copyBackingList().copyState())
            .usingRecursiveFieldByFieldElementComparator()
            .containsExactlyInAnyOrder(
                new ResourceAmount(D, 10),
                new ResourceAmount(A, 15)
            );
    }

    @Test
    void shouldReuseExistingResourceWhenPreventingSortingAndRemovingExistingResourceCompletelyAndThenAddingAgain() {
        // Arrange
        final ResourceRepository<ResourceKey> sut = builder
            .addResource(A, 15)
            .addResource(B, 20)
            .addResource(D, 10)
            .build();

        sut.sort();

        final Runnable listener = mock(Runnable.class);
        sut.setListener(listener);

        // Act & assert
        assertThat(sut.getViewList()).usingRecursiveFieldByFieldElementComparator().containsExactly(D, A, B);

        // Delete the item
        sut.setPreventSorting(true);
        sut.onChange(B, -20);
        verify(listener, never()).run();

        assertThat(sut.getViewList()).usingRecursiveFieldByFieldElementComparator().containsExactly(D, A, B);

        // Re-insert the item
        sut.onChange(B, 5);
        verify(listener, never()).run();

        assertThat(sut.getViewList()).usingRecursiveFieldByFieldElementComparator().containsExactly(D, A, B);

        // Re-insert the item again
        sut.onChange(B, 3);
        verify(listener, never()).run();

        assertThat(sut.getViewList()).usingRecursiveFieldByFieldElementComparator().containsExactly(D, A, B);
    }

    @Test
    void shouldClear() {
        // Arrange
        final ResourceRepository<ResourceKey> sut = builder
            .addResource(A, 15)
            .addResource(B, 20)
            .addResource(D, 10)
            .build();

        // Act
        sut.clear();

        // Assert
        assertThat(sut.getViewList()).isEmpty();
        assertThat(sut.copyBackingList().copyState()).isEmpty();
        assertThat(sut.getAmount(A)).isZero();
        assertThat(sut.getAmount(B)).isZero();
        assertThat(sut.getAmount(D)).isZero();
    }

    @Test
    void shouldIncludeStickyResourceInViewList() {
        // Arrange
        final ResourceRepository<ResourceKey> sut = builder
            .addResource(A, 15)
            .addStickyResource(B)
            .build();

        // Act
        sut.sort();

        // Assert
        assertThat(sut.getViewList()).usingRecursiveFieldByFieldElementComparator().containsExactlyInAnyOrder(A, B);
        assertThat(sut.isSticky(A)).isFalse();
        assertThat(sut.isSticky(B)).isTrue();
        assertThat(sut.getAmount(A)).isEqualTo(15);
        assertThat(sut.getAmount(B)).isZero();
        assertThat(sut.copyBackingList().copyState()).usingRecursiveFieldByFieldElementComparator()
            .containsExactly(new ResourceAmount(A, 15));
    }

    @Test
    void shouldIncludeStickyResourceInViewListEvenIfItIsInTheBackingList() {
        // Arrange
        final ResourceRepository<ResourceKey> sut = builder
            .addResource(A, 15)
            .addStickyResource(A)
            .build();

        // Act
        sut.sort();

        // Assert
        assertThat(sut.getViewList()).usingRecursiveFieldByFieldElementComparator().containsExactly(A);
        assertThat(sut.isSticky(A)).isTrue();
        assertThat(sut.getAmount(A)).isEqualTo(15);
        assertThat(sut.copyBackingList().copyState())
            .usingRecursiveFieldByFieldElementComparator()
            .containsExactly(new ResourceAmount(A, 15));
    }

    @Test
    void shouldNotRemoveStickyResource() {
        // Arrange
        final ResourceRepository<ResourceKey> sut = builder
            .addResource(A, 15)
            .addStickyResource(A)
            .build();

        sut.sort();

        // Act
        sut.onChange(A, -15);

        // Assert
        assertThat(sut.getViewList()).usingRecursiveFieldByFieldElementComparator().containsExactly(A);
        assertThat(sut.isSticky(A)).isTrue();
        assertThat(sut.getAmount(A)).isZero();
        assertThat(sut.copyBackingList().copyState()).isEmpty();
    }

    @Test
    void shouldNotRemoveStickyResourceEvenWhenPreventingSorting() {
        // Arrange
        final ResourceRepository<ResourceKey> sut = builder
            .addResource(A, 15)
            .addStickyResource(A)
            .build();

        sut.sort();
        sut.setPreventSorting(true);

        // Act & assert
        sut.onChange(A, -15);

        assertThat(sut.getViewList()).usingRecursiveFieldByFieldElementComparator().containsExactly(A);
        assertThat(sut.isSticky(A)).isTrue();
        assertThat(sut.getAmount(A)).isZero();
        assertThat(sut.copyBackingList().copyState()).isEmpty();

        sut.onChange(A, 1);

        assertThat(sut.getViewList()).usingRecursiveFieldByFieldElementComparator().containsExactly(A);
        assertThat(sut.isSticky(A)).isTrue();
        assertThat(sut.getAmount(A)).isEqualTo(1);
        assertThat(sut.copyBackingList().copyState()).usingRecursiveFieldByFieldElementComparator().containsExactly(
            new ResourceAmount(A, 1)
        );
    }

    // Ensure that we do not get in trouble when adding 2 resources with the same name, but a different identity.
    // This test avoids the bug where the view insertion fails, because the resource is already "contained"
    // in the view, but actually isn't because it has a different identity.
    @Test
    void shouldAddResourcesWithSameNameButDifferentIdentity() {
        // Arrange
        final ResourceRepositoryBuilder<WrappedGridResource> wrappedBuilder = new ResourceRepositoryBuilderImpl<>(
            resourceKey -> new WrappedGridResource((WrappedResourceKey) resourceKey),
            view -> Comparator.comparing(WrappedGridResource::toString),
            view -> Comparator.comparingLong(wgr -> view.getAmount(wgr.wrappedResourceKey))
        );
        final ResourceRepository<WrappedGridResource> sut = wrappedBuilder.build();

        // Act
        sut.onChange(new WrappedResourceKey(A, 1), 1);
        sut.onChange(new WrappedResourceKey(A, 2), 1);

        // Assert
        assertThat(sut.getViewList()).usingRecursiveFieldByFieldElementComparator().containsExactly(
            new WrappedGridResource(new WrappedResourceKey(A, 1)),
            new WrappedGridResource(new WrappedResourceKey(A, 2))
        );
    }

    private record WrappedGridResource(WrappedResourceKey wrappedResourceKey) {
    }

    private record WrappedResourceKey(ResourceKey resource, int meta) implements ResourceKey {
    }
}
