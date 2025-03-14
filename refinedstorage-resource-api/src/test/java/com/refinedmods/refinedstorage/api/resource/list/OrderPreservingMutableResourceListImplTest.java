package com.refinedmods.refinedstorage.api.resource.list;

import com.refinedmods.refinedstorage.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;

import java.util.Collection;
import java.util.Set;

import org.junit.jupiter.api.Test;

import static com.refinedmods.refinedstorage.api.resource.TestResource.A;
import static com.refinedmods.refinedstorage.api.resource.TestResource.B;
import static com.refinedmods.refinedstorage.api.resource.TestResource.C;
import static org.assertj.core.api.Assertions.assertThat;

class OrderPreservingMutableResourceListImplTest extends AbstractMutableResourceListTest {
    @Override
    protected MutableResourceList createList() {
        return MutableResourceListImpl.orderPreserving();
    }

    @Test
    void shouldPreserveOrderWhenRetrievingKeys() {
        // Arrange
        sut.add(A, 1);
        sut.add(B, 1);
        sut.add(C, 1);

        // Act
        final Set<ResourceKey> resources = sut.getAll();

        // Assert
        assertThat(resources).containsExactly(A, B, C);
    }

    @Test
    void shouldPreserveOrderWhenCopyingState() {
        // Arrange
        sut.add(A, 1);
        sut.add(B, 1);
        sut.add(C, 1);

        // Act
        final Collection<ResourceAmount> resources = sut.copyState();

        // Assert
        assertThat(resources).usingRecursiveFieldByFieldElementComparator().containsExactly(
            new ResourceAmount(A, 1),
            new ResourceAmount(B, 1),
            new ResourceAmount(C, 1)
        );
    }
}
