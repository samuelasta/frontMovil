package com.example.smartrestaurant.presentation.addition

import app.cash.turbine.test
import com.example.smartrestaurant.domain.model.Addition
import com.example.smartrestaurant.domain.model.AdditionState
import com.example.smartrestaurant.domain.usecase.addition.*
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AdditionViewModelTest {

    private lateinit var viewModel: AdditionViewModel
    private lateinit var getAdditionsUseCase: GetAdditionsUseCase
    private lateinit var getAdditionByIdUseCase: GetAdditionByIdUseCase
    private lateinit var createAdditionUseCase: CreateAdditionUseCase
    private lateinit var updateAdditionUseCase: UpdateAdditionUseCase
    private lateinit var deleteAdditionUseCase: DeleteAdditionUseCase

    private val testDispatcher = StandardTestDispatcher()

    private val testAddition = Addition(
        id = "1",
        name = "Extra Cheese",
        description = "Additional cheese topping",
        price = 2.50,
        imageUrl = "https://example.com/cheese.jpg",
        state = AdditionState.ACTIVE
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        getAdditionsUseCase = mockk()
        getAdditionByIdUseCase = mockk()
        createAdditionUseCase = mockk()
        updateAdditionUseCase = mockk()
        deleteAdditionUseCase = mockk()

        // Mock initial load
        coEvery { getAdditionsUseCase(0) } returns Result.success(listOf(testAddition))

        viewModel = AdditionViewModel(
            getAdditionsUseCase,
            getAdditionByIdUseCase,
            createAdditionUseCase,
            updateAdditionUseCase,
            deleteAdditionUseCase
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadAdditions should update list state with success`() = runTest {
        // Given
        val additions = listOf(testAddition)
        coEvery { getAdditionsUseCase(0) } returns Result.success(additions)

        // When
        viewModel.loadAdditions(0)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.listState.test {
            val state = awaitItem()
            assertEquals(additions, state.additions)
            assertFalse(state.isLoading)
            assertNull(state.error)
            assertEquals(0, state.currentPage)
            assertTrue(state.hasMorePages)
        }
    }

    @Test
    fun `loadAdditions should update list state with error`() = runTest {
        // Given
        val errorMessage = "Network error"
        coEvery { getAdditionsUseCase(0) } returns Result.failure(Exception(errorMessage))

        // When
        viewModel.loadAdditions(0)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.listState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertEquals(errorMessage, state.error)
        }
    }

    @Test
    fun `loadAdditions with page greater than 0 should append to existing list`() = runTest {
        // Given
        val page1Additions = listOf(testAddition)
        val page2Addition = testAddition.copy(id = "2", name = "Extra Bacon")
        coEvery { getAdditionsUseCase(0) } returns Result.success(page1Additions)
        coEvery { getAdditionsUseCase(1) } returns Result.success(listOf(page2Addition))

        // When
        viewModel.loadAdditions(0)
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.loadAdditions(1)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.listState.test {
            val state = awaitItem()
            assertEquals(2, state.additions.size)
            assertEquals(1, state.currentPage)
        }
    }

    @Test
    fun `onSearchQueryChange should update search query`() = runTest {
        // When
        viewModel.onSearchQueryChange("cheese")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.listState.test {
            val state = awaitItem()
            assertEquals("cheese", state.searchQuery)
        }
    }

    @Test
    fun `loadAdditionById should update detail state with success`() = runTest {
        // Given
        coEvery { getAdditionByIdUseCase("1") } returns Result.success(testAddition)

        // When
        viewModel.loadAdditionById("1")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.detailState.test {
            val state = awaitItem()
            assertEquals(testAddition, state.addition)
            assertFalse(state.isLoading)
            assertNull(state.error)
        }
    }

    @Test
    fun `loadAdditionById should update detail state with error`() = runTest {
        // Given
        val errorMessage = "Addition not found"
        coEvery { getAdditionByIdUseCase("1") } returns Result.failure(Exception(errorMessage))

        // When
        viewModel.loadAdditionById("1")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.detailState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertEquals(errorMessage, state.error)
        }
    }

    @Test
    fun `showDeleteConfirmation should update detail state`() = runTest {
        // When
        viewModel.showDeleteConfirmation()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.detailState.test {
            val state = awaitItem()
            assertTrue(state.showDeleteConfirmation)
        }
    }

    @Test
    fun `deleteAddition should call use case and invoke onSuccess`() = runTest {
        // Given
        coEvery { getAdditionByIdUseCase("1") } returns Result.success(testAddition)
        coEvery { deleteAdditionUseCase("1") } returns Result.success(Unit)
        
        viewModel.loadAdditionById("1")
        testDispatcher.scheduler.advanceUntilIdle()

        var successCalled = false

        // When
        viewModel.deleteAddition { successCalled = true }
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify { deleteAdditionUseCase("1") }
        assertTrue(successCalled)
    }

    @Test
    fun `loadAdditionForEdit should populate form state`() = runTest {
        // Given
        coEvery { getAdditionByIdUseCase("1") } returns Result.success(testAddition)

        // When
        viewModel.loadAdditionForEdit("1")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.formState.test {
            val state = awaitItem()
            assertEquals(testAddition, state.addition)
            assertEquals(testAddition.name, state.name)
            assertEquals(testAddition.description, state.description)
            assertEquals(testAddition.price.toString(), state.price)
            assertEquals(testAddition.imageUrl, state.imageUri)
        }
    }

    @Test
    fun `onNameChange should update form state`() = runTest {
        // When
        viewModel.onNameChange("New Name")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.formState.test {
            val state = awaitItem()
            assertEquals("New Name", state.name)
            assertNull(state.nameError)
        }
    }

    @Test
    fun `onPriceChange should update form state`() = runTest {
        // When
        viewModel.onPriceChange("5.99")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.formState.test {
            val state = awaitItem()
            assertEquals("5.99", state.price)
            assertNull(state.priceError)
        }
    }

    @Test
    fun `submitAddition should fail validation with blank name`() = runTest {
        // Given
        viewModel.onNameChange("")
        viewModel.onPriceChange("5.99")
        testDispatcher.scheduler.advanceUntilIdle()

        var successCalled = false

        // When
        viewModel.submitAddition { successCalled = true }
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.formState.test {
            val state = awaitItem()
            assertNotNull(state.nameError)
            assertFalse(successCalled)
        }
    }

    @Test
    fun `submitAddition should fail validation with invalid price`() = runTest {
        // Given
        viewModel.onNameChange("Extra Cheese")
        viewModel.onPriceChange("-5")
        testDispatcher.scheduler.advanceUntilIdle()

        var successCalled = false

        // When
        viewModel.submitAddition { successCalled = true }
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.formState.test {
            val state = awaitItem()
            assertNotNull(state.priceError)
            assertFalse(successCalled)
        }
    }

    @Test
    fun `submitAddition should create new addition with valid data`() = runTest {
        // Given
        val newAddition = Addition(
            id = null,
            name = "Extra Cheese",
            description = "Delicious cheese",
            price = 2.50,
            imageUrl = null,
            state = AdditionState.ACTIVE
        )
        coEvery { createAdditionUseCase(any()) } returns Result.success(newAddition.copy(id = "1"))

        viewModel.onNameChange("Extra Cheese")
        viewModel.onDescriptionChange("Delicious cheese")
        viewModel.onPriceChange("2.50")
        testDispatcher.scheduler.advanceUntilIdle()

        var successCalled = false

        // When
        viewModel.submitAddition { successCalled = true }
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify { createAdditionUseCase(any()) }
        assertTrue(successCalled)
    }

    @Test
    fun `submitAddition should update existing addition with valid data`() = runTest {
        // Given
        coEvery { getAdditionByIdUseCase("1") } returns Result.success(testAddition)
        coEvery { updateAdditionUseCase("1", any()) } returns Result.success(testAddition)

        viewModel.loadAdditionForEdit("1")
        testDispatcher.scheduler.advanceUntilIdle()
        
        viewModel.onNameChange("Updated Name")
        viewModel.onPriceChange("3.50")
        testDispatcher.scheduler.advanceUntilIdle()

        var successCalled = false

        // When
        viewModel.submitAddition { successCalled = true }
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify { updateAdditionUseCase("1", any()) }
        assertTrue(successCalled)
    }

    @Test
    fun `retryLastOperation should retry last failed operation`() = runTest {
        // Given
        coEvery { getAdditionsUseCase(0) } returns Result.failure(Exception("Network error"))
        viewModel.loadAdditions(0)
        testDispatcher.scheduler.advanceUntilIdle()

        coEvery { getAdditionsUseCase(0) } returns Result.success(listOf(testAddition))

        // When
        viewModel.retryLastOperation()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.listState.test {
            val state = awaitItem()
            assertEquals(listOf(testAddition), state.additions)
            assertNull(state.error)
        }
    }

    @Test
    fun `resetFormState should clear form state`() = runTest {
        // Given
        viewModel.onNameChange("Test")
        viewModel.onPriceChange("5.99")
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.resetFormState()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.formState.test {
            val state = awaitItem()
            assertEquals("", state.name)
            assertEquals("", state.price)
            assertNull(state.addition)
        }
    }
}
