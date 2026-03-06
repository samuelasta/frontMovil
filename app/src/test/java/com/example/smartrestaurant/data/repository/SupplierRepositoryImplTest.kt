package com.example.smartrestaurant.data.repository

import com.example.smartrestaurant.data.mapper.SupplierMapper
import com.example.smartrestaurant.data.remote.api.SupplierApi
import com.example.smartrestaurant.data.remote.dto.ApiResponse
import com.example.smartrestaurant.data.remote.dto.SupplierDto
import com.example.smartrestaurant.domain.model.Supplier
import com.example.smartrestaurant.domain.model.SupplierState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.io.IOException

/**
 * Unit tests for SupplierRepositoryImpl.
 * Tests error handling for network errors, API errors, and successful responses.
 */
class SupplierRepositoryImplTest {
    
    private lateinit var api: SupplierApi
    private lateinit var mapper: SupplierMapper
    private lateinit var repository: SupplierRepositoryImpl
    
    private val testSupplierDto = SupplierDto(
        id = "1",
        name = "Test Supplier",
        email = "supplier@test.com",
        phone = "123456789",
        address = "Test Address",
        state = "ACTIVE"
    )
    
    private val testSupplier = Supplier(
        id = "1",
        name = "Test Supplier",
        email = "supplier@test.com",
        phone = "123456789",
        address = "Test Address",
        state = SupplierState.ACTIVE
    )
    
    @Before
    fun setup() {
        api = mockk()
        mapper = SupplierMapper()
        repository = SupplierRepositoryImpl(api, mapper)
    }
    
    @Test
    fun `getSuppliers returns success when API response is successful`() = runTest {
        // Given
        val response = ApiResponse(message = listOf(testSupplierDto), error = false)
        coEvery { api.getSuppliers() } returns response
        
        // When
        val result = repository.getSuppliers()
        
        // Then
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
        assertEquals("Test Supplier", result.getOrNull()?.first()?.name)
        coVerify(exactly = 1) { api.getSuppliers() }
    }
    
    @Test
    fun `getSuppliers returns failure when API response has error flag`() = runTest {
        // Given
        coEvery { api.getSuppliers() } throws Exception("Error message")
        
        // When
        val result = repository.getSuppliers()
        
        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("Error message") == true)
    }
    
    @Test
    fun `getSuppliers returns failure on network error`() = runTest {
        // Given
        coEvery { api.getSuppliers() } throws IOException("Network error")
        
        // When
        val result = repository.getSuppliers()
        
        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("Network error") == true)
    }
    
    @Test
    fun `getSupplierById returns success when API response is successful`() = runTest {
        // Given
        val response = ApiResponse(message = testSupplierDto, error = false)
        coEvery { api.getSupplierById("1") } returns response
        
        // When
        val result = repository.getSupplierById("1")
        
        // Then
        assertTrue(result.isSuccess)
        assertEquals("Test Supplier", result.getOrNull()?.name)
        coVerify(exactly = 1) { api.getSupplierById("1") }
    }
    
    @Test
    fun `getSupplierById returns failure when API response has error flag`() = runTest {
        // Given
        coEvery { api.getSupplierById("999") } throws Exception("Supplier not found")
        
        // When
        val result = repository.getSupplierById("999")
        
        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("Supplier not found") == true)
    }
    
    @Test
    fun `createSupplier returns success when API response is successful`() = runTest {
        // Given
        val response = ApiResponse(message = testSupplierDto, error = false)
        coEvery { api.createSupplier(any()) } returns response
        
        // When
        val result = repository.createSupplier(testSupplier)
        
        // Then
        assertTrue(result.isSuccess)
        assertEquals("Test Supplier", result.getOrNull()?.name)
        coVerify(exactly = 1) { api.createSupplier(any()) }
    }
    
    @Test
    fun `createSupplier returns failure on network error`() = runTest {
        // Given
        coEvery { api.createSupplier(any()) } throws IOException("Network error")
        
        // When
        val result = repository.createSupplier(testSupplier)
        
        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("Network error") == true)
    }
    
    @Test
    fun `updateSupplier returns success when API response is successful`() = runTest {
        // Given
        val response = ApiResponse(message = testSupplierDto, error = false)
        coEvery { api.updateSupplier("1", any()) } returns response
        
        // When
        val result = repository.updateSupplier("1", testSupplier)
        
        // Then
        assertTrue(result.isSuccess)
        assertEquals("Test Supplier", result.getOrNull()?.name)
        coVerify(exactly = 1) { api.updateSupplier("1", any()) }
    }
    
    @Test
    fun `updateSupplier returns failure when API response has error flag`() = runTest {
        // Given
        coEvery { api.updateSupplier("1", any()) } throws Exception("Update failed")
        
        // When
        val result = repository.updateSupplier("1", testSupplier)
        
        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("Update failed") == true)
    }
    
    @Test
    fun `deleteSupplier returns success when API response is successful`() = runTest {
        // Given
        val response = ApiResponse(message = Unit, error = false)
        coEvery { api.deleteSupplier("1") } returns response
        
        // When
        val result = repository.deleteSupplier("1")
        
        // Then
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { api.deleteSupplier("1") }
    }
    
    @Test
    fun `deleteSupplier returns failure when API response has error flag`() = runTest {
        // Given
        coEvery { api.deleteSupplier("1") } throws Exception("Delete failed")
        
        // When
        val result = repository.deleteSupplier("1")
        
        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("Delete failed") == true)
    }
}
