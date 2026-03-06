package com.example.smartrestaurant.domain.usecase.supplier

import com.example.smartrestaurant.domain.model.Supplier
import com.example.smartrestaurant.domain.model.SupplierState
import com.example.smartrestaurant.domain.repository.SupplierRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for GetSuppliersUseCase.
 * Validates: Requirements 4.1
 */
class GetSuppliersUseCaseTest {
    
    private lateinit var repository: SupplierRepository
    private lateinit var useCase: GetSuppliersUseCase
    
    private val testSuppliers = listOf(
        Supplier(
            id = "1",
            name = "Supplier 1",
            email = "supplier1@test.com",
            phone = "123456789",
            address = "Address 1",
            state = SupplierState.ACTIVE
        ),
        Supplier(
            id = "2",
            name = "Supplier 2",
            email = "supplier2@test.com",
            phone = "987654321",
            address = "Address 2",
            state = SupplierState.ACTIVE
        )
    )
    
    @Before
    fun setup() {
        repository = mockk()
        useCase = GetSuppliersUseCase(repository)
    }
    
    @Test
    fun `invoke returns success with supplier list when repository succeeds`() = runTest {
        // Given
        coEvery { repository.getSuppliers() } returns Result.success(testSuppliers)
        
        // When
        val result = useCase()
        
        // Then
        assertTrue(result.isSuccess)
        assertEquals(2, result.getOrNull()?.size)
        assertEquals("Supplier 1", result.getOrNull()?.get(0)?.name)
        coVerify(exactly = 1) { repository.getSuppliers() }
    }
    
    @Test
    fun `invoke returns empty list when no suppliers exist`() = runTest {
        // Given
        coEvery { repository.getSuppliers() } returns Result.success(emptyList())
        
        // When
        val result = useCase()
        
        // Then
        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()?.isEmpty() == true)
        coVerify(exactly = 1) { repository.getSuppliers() }
    }
    
    @Test
    fun `invoke returns failure when repository fails`() = runTest {
        // Given
        coEvery { repository.getSuppliers() } returns Result.failure(
            Exception("Network error")
        )
        
        // When
        val result = useCase()
        
        // Then
        assertTrue(result.isFailure)
        assertEquals("Network error", result.exceptionOrNull()?.message)
        coVerify(exactly = 1) { repository.getSuppliers() }
    }
}
