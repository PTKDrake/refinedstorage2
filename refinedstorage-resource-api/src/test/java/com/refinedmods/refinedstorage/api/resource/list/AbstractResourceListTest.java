package com.refinedmods.refinedstorage.api.resource.list;

import com.refinedmods.refinedstorage.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage.api.resource.TestResource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

abstract class AbstractResourceListTest {
    private ResourceList list;

    @BeforeEach
    void setUp() {
        list = createList();
    }

    protected abstract ResourceList createList();

    @Test
    void shouldAddNewResource() {
        // Act
        final ResourceList.OperationResult result = list.add(TestResource.A, 10);

        // Assert
        assertThat(result.change()).isEqualTo(10);
        assertThat(result.resourceAmount().getAmount()).isEqualTo(10);
        assertThat(result.resourceAmount().getResource()).isEqualTo(TestResource.A);
        assertThat(result.available()).isTrue();

        assertThat(list.getAll()).usingRecursiveFieldByFieldElementComparator().containsExactly(
            new ResourceAmount(TestResource.A, 10)
        );
        assertThat(list.getAmount(TestResource.A)).isEqualTo(10);
        assertThat(list.contains(TestResource.A)).isTrue();
    }

    @Test
    void shouldAddNewResourceWithResourceAmountDirectly() {
        // Act
        final ResourceList.OperationResult result = list.add(new ResourceAmount(TestResource.A, 10));

        // Assert
        assertThat(result.change()).isEqualTo(10);
        assertThat(result.resourceAmount().getAmount()).isEqualTo(10);
        assertThat(result.resourceAmount().getResource()).isEqualTo(TestResource.A);
        assertThat(result.available()).isTrue();

        assertThat(list.getAll()).usingRecursiveFieldByFieldElementComparator().containsExactly(
            new ResourceAmount(TestResource.A, 10)
        );
        assertThat(list.getAmount(TestResource.A)).isEqualTo(10);
        assertThat(list.contains(TestResource.A)).isTrue();
    }

    @Test
    void shouldAddMultipleOfSameResource() {
        // Act
        final ResourceList.OperationResult result1 = list.add(TestResource.A, 10);
        final ResourceList.OperationResult result2 = list.add(TestResource.A, 5);

        // Assert
        assertThat(result1.change()).isEqualTo(10);
        assertThat(result1.resourceAmount().getAmount()).isEqualTo(15);
        assertThat(result1.resourceAmount().getResource()).isEqualTo(TestResource.A);
        assertThat(result1.available()).isTrue();

        assertThat(result2.change()).isEqualTo(5);
        assertThat(result1.resourceAmount().getAmount()).isEqualTo(15);
        assertThat(result1.resourceAmount().getResource()).isEqualTo(TestResource.A);
        assertThat(result2.available()).isTrue();

        assertThat(list.getAll()).usingRecursiveFieldByFieldElementComparator().containsExactly(
            new ResourceAmount(TestResource.A, 15)
        );
        assertThat(list.getAmount(TestResource.A)).isEqualTo(15);
        assertThat(list.contains(TestResource.A)).isTrue();
    }

    @Test
    void shouldAddMultipleOfDifferentResources() {
        // Act
        final ResourceList.OperationResult result1 = list.add(TestResource.A, 10);
        final ResourceList.OperationResult result2 = list.add(TestResource.A, 5);
        final ResourceList.OperationResult result3 = list.add(TestResource.B, 3);

        // Assert
        assertThat(result1.change()).isEqualTo(10);
        assertThat(result1.resourceAmount().getAmount()).isEqualTo(15);
        assertThat(result1.resourceAmount().getResource()).isEqualTo(TestResource.A);
        assertThat(result1.available()).isTrue();

        assertThat(result2.change()).isEqualTo(5);
        assertThat(result2.resourceAmount().getAmount()).isEqualTo(15);
        assertThat(result2.resourceAmount().getResource()).isEqualTo(TestResource.A);
        assertThat(result2.available()).isTrue();

        assertThat(result3.change()).isEqualTo(3);
        assertThat(result3.resourceAmount().getAmount()).isEqualTo(3);
        assertThat(result3.resourceAmount().getResource()).isEqualTo(TestResource.B);
        assertThat(result3.available()).isTrue();

        assertThat(list.getAll()).usingRecursiveFieldByFieldElementComparator().containsExactlyInAnyOrder(
            new ResourceAmount(TestResource.A, 15),
            new ResourceAmount(TestResource.B, 3)
        );
        assertThat(list.getAmount(TestResource.A)).isEqualTo(15);
        assertThat(list.contains(TestResource.A)).isTrue();
        assertThat(list.getAmount(TestResource.B)).isEqualTo(3);
        assertThat(list.contains(TestResource.B)).isTrue();
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void shouldNotAddInvalidResourceOrAmount() {
        // Act
        final Executable action1 = () -> list.add(TestResource.A, 0);
        final Executable action2 = () -> list.add(TestResource.A, -1);
        final Executable action3 = () -> list.add(null, 1);

        // Assert
        assertThrows(IllegalArgumentException.class, action1);
        assertThrows(IllegalArgumentException.class, action2);
        assertThrows(NullPointerException.class, action3);
    }

    @Test
    void shouldNotRemoveResourceWhenItIsNotAvailable() {
        // Act
        final Optional<ResourceList.OperationResult> result = list.remove(TestResource.A, 10);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void shouldRemoveResourcePartly() {
        // Arrange
        list.add(TestResource.A, 20);
        list.add(TestResource.B, 6);

        // Act
        final Optional<ResourceList.OperationResult> result2 = list.remove(TestResource.A, 5);

        // Assert
        assertThat(result2).isPresent();
        assertThat(result2.get().change()).isEqualTo(-5);
        assertThat(result2.get().resourceAmount().getAmount()).isEqualTo(15);
        assertThat(result2.get().resourceAmount().getResource()).isEqualTo(TestResource.A);
        assertThat(result2.get().available()).isTrue();

        assertThat(list.getAll()).usingRecursiveFieldByFieldElementComparator().containsExactlyInAnyOrder(
            new ResourceAmount(TestResource.A, 15),
            new ResourceAmount(TestResource.B, 6)
        );

        assertThat(list.getAmount(TestResource.A)).isEqualTo(15);
        assertThat(list.contains(TestResource.A)).isTrue();
        assertThat(list.getAmount(TestResource.B)).isEqualTo(6);
        assertThat(list.contains(TestResource.B)).isTrue();
    }

    @Test
    void shouldRemoveResourcePartlyWithResourceAmountDirectly() {
        // Arrange
        list.add(TestResource.A, 20);
        list.add(TestResource.B, 6);

        // Act
        final Optional<ResourceList.OperationResult> result2 = list.remove(new ResourceAmount(
            TestResource.A,
            5
        ));

        // Assert
        assertThat(result2).isPresent();
        assertThat(result2.get().change()).isEqualTo(-5);
        assertThat(result2.get().resourceAmount().getAmount()).isEqualTo(15);
        assertThat(result2.get().resourceAmount().getResource()).isEqualTo(TestResource.A);
        assertThat(result2.get().available()).isTrue();

        assertThat(list.getAll()).usingRecursiveFieldByFieldElementComparator().containsExactlyInAnyOrder(
            new ResourceAmount(TestResource.A, 15),
            new ResourceAmount(TestResource.B, 6)
        );

        assertThat(list.getAmount(TestResource.A)).isEqualTo(15);
        assertThat(list.contains(TestResource.A)).isTrue();
        assertThat(list.getAmount(TestResource.B)).isEqualTo(6);
        assertThat(list.contains(TestResource.B)).isTrue();
    }

    @Test
    void shouldRemoveResourceCompletely() {
        // Arrange
        list.add(TestResource.A, 20);
        list.add(TestResource.B, 6);

        // Act
        final Optional<ResourceList.OperationResult> result2 = list.remove(TestResource.A, 20);

        // Assert
        assertThat(result2).isPresent();
        assertThat(result2.get().change()).isEqualTo(-20);
        assertThat(result2.get().resourceAmount().getAmount()).isEqualTo(20);
        assertThat(result2.get().resourceAmount().getResource()).isEqualTo(TestResource.A);
        assertThat(result2.get().available()).isFalse();

        assertThat(list.getAll()).usingRecursiveFieldByFieldElementComparator().containsExactlyInAnyOrder(
            new ResourceAmount(TestResource.B, 6)
        );

        assertThat(list.getAmount(TestResource.A)).isZero();
        assertThat(list.contains(TestResource.A)).isFalse();
        assertThat(list.getAmount(TestResource.B)).isEqualTo(6);
        assertThat(list.contains(TestResource.B)).isTrue();
    }

    @Test
    void shouldRemoveResourceCompletelyWithResourceAmountDirectly() {
        // Arrange
        list.add(TestResource.A, 20);
        list.add(TestResource.B, 6);

        // Act
        final Optional<ResourceList.OperationResult> result2 = list.remove(new ResourceAmount(
            TestResource.A,
            20
        ));

        // Assert
        assertThat(result2).isPresent();
        assertThat(result2.get().change()).isEqualTo(-20);
        assertThat(result2.get().resourceAmount().getAmount()).isEqualTo(20);
        assertThat(result2.get().resourceAmount().getResource()).isEqualTo(TestResource.A);
        assertThat(result2.get().available()).isFalse();

        assertThat(list.getAll()).usingRecursiveFieldByFieldElementComparator().containsExactlyInAnyOrder(
            new ResourceAmount(TestResource.B, 6)
        );

        assertThat(list.getAmount(TestResource.A)).isZero();
        assertThat(list.contains(TestResource.A)).isFalse();
        assertThat(list.getAmount(TestResource.B)).isEqualTo(6);
        assertThat(list.contains(TestResource.B)).isTrue();
    }

    @Test
    void shouldNotRemoveResourceWithMoreThanIsAvailable() {
        // Arrange
        list.add(TestResource.A, 20);
        list.add(TestResource.B, 6);

        // Act
        final Optional<ResourceList.OperationResult> result2 = list.remove(TestResource.A, 21);

        // Assert
        assertThat(result2).isPresent();
        assertThat(result2.get().change()).isEqualTo(-20);
        assertThat(result2.get().resourceAmount().getAmount()).isEqualTo(20);
        assertThat(result2.get().resourceAmount().getResource()).isEqualTo(TestResource.A);
        assertThat(result2.get().available()).isFalse();

        assertThat(list.getAll()).usingRecursiveFieldByFieldElementComparator().containsExactlyInAnyOrder(
            new ResourceAmount(TestResource.B, 6)
        );

        assertThat(list.getAmount(TestResource.A)).isZero();
        assertThat(list.contains(TestResource.A)).isFalse();
        assertThat(list.getAmount(TestResource.B)).isEqualTo(6);
        assertThat(list.contains(TestResource.B)).isTrue();
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void shouldNotRemoveInvalidResourceOrAmount() {
        // Act
        final Executable action1 = () -> list.remove(TestResource.A, 0);
        final Executable action2 = () -> list.remove(TestResource.A, -1);
        final Executable action3 = () -> list.remove(null, 1);

        // Assert
        assertThrows(IllegalArgumentException.class, action1);
        assertThrows(IllegalArgumentException.class, action2);
        assertThrows(NullPointerException.class, action3);
    }

    @Test
    void shouldClearList() {
        // Arrange
        list.add(TestResource.A, 10);
        list.add(TestResource.B, 5);

        final Collection<ResourceAmount> contentsBeforeClear = new ArrayList<>(list.getAll());

        // Act
        list.clear();

        // Assert
        final Collection<ResourceAmount> contentsAfterClear = list.getAll();

        assertThat(contentsBeforeClear).hasSize(2);
        assertThat(contentsAfterClear).isEmpty();

        assertThat(list.getAmount(TestResource.A)).isZero();
        assertThat(list.getAmount(TestResource.B)).isZero();
    }
}
