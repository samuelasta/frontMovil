package com.example.smartrestaurant.data.repository

import com.example.smartrestaurant.data.mapper.AdditionMapper
import com.example.smartrestaurant.data.remote.api.AdditionApi
import com.example.smartrestaurant.data.remote.dto.ApiResponse
import com.example.smartrestaurant.data.remote.dto.AdditionDto
import com.example.smartrestaurant.domain.model.Addition
import com.example.smartrestaurant.domain.model.AdditionState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.io.IOException

/**
 * Unit tests for AdditionRepositoryImpl.
 * Tests error handling for network errors, API errors, and successful responses.
 * 
 * Validates: Requirements 8.1, 8.3, 8.6, 8.7, 11.1, 13.3, 13.4
 */
class AdditionRepositoryImplTest {
    
    private lateinit var api: AdditionApi
    private lateinit var mapper: AdditionMapper
    private lateinit var repository: AdditionRepositoryImpl
    
    private val testAdditionDto = AdditionDto(
        id = "1",
        name = "Extra Cheese",
        description = "Additional cheese topping",
        price = 2.50,
        imageUrl = "https://example.com/cheese.jpg",
        state = "ACTIVE"
    )
    
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
        api = mockk()
        mapper = AdditionMapper()
        repository = AdditionRepositoryImpl(api, mapper)
    }
    
    @Test
    fun `getAdditions returns success when API response is successful`() = runTest {
        // Given
        val response = ApiResponse(message = listOf(testAdditionDto), error = false)
        coEvery { api.getAdditions(0) } returns response
        
        // When
        val result = repository.getAdditions(0)
        
        // Then
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
        assertEquals("Extra Cheese", result.getOrNull()?.first()?.name)
        assertEquals(2.50, result.getOrNull()?.first()?.price ?: 0.0, 0.01)
        coVerify(exactly = 1) { api.getAdditions(0) }
    }
    
    @Test
    fun `getAdditions returns failure when API response has error flag`() = runTest {
        // Given
        val response = ApiResponse(message = emptyList<AdditionDto>(), error = true)
        coEvery { api.getAdditions(0) } returns response
        
        // When
        val result = repository.getAdditions(0)
        
        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("Error fetching additions") == true)
    }
    
    @Test
    fun `getAdditions returns failure on network error`() = runTest {
        // Given
        coEvery { api.getAdditions(0) } throws IOException("Network error")
        
        // When
        val result = repository.getAdditions(0)
        
        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("Network error") == true)
    }
    
    @Test
    fun `getAdditions supports pagination`() = runTest {
        // Given
        val page1Response = ApiResponse(message = listOf(testAdditionDto), error = false)
        val page2Response = ApiResponse(
            message = listOf(testAdditionDto.copy(id = "2", name = "Extra Bacon")),
            error = false
        )
        coEvery { api.getAdditions(0) } returns page1Response
        coEvery { api.getAdditions(1) } returns page2Response
        
        // When
        val result1 = repository.getAdditions(0)
        val result2 = repository.getAdditions(1)
        
        // Then
        assertTrue(result1.isSuccess)
        assertTrue(result2.isSuccess)
        assertEquals("Extra Cheese", result1.getOrNull()?.first()?.name)
        assertEquals("Extra Bacon", result2.getOrNull()?.first()?.name)
        coVerify(exactly = 1) { api.getAdditions(0) }
        coVerify(exactly = 1) { api.getAdditions(1) }
    }
    
    @Test
    fun `getAdditionById returns success when API response is successful`() = runTest {
        // Given
        val response = ApiResponse(message = testAdditionDto, error = false)
        coEvery { api.getAdditionById("1") } returns response
        
        // When
        val result = repository.getAdditionById("1")
        
        // Then
        assertTrue(result.isSuccess)
        assertEquals("Extra Cheese", result.getOrNull()?.name)
        assertEquals("Additional cheese topping", result.getOrNull()?.description)
        assertEquals(2.50, result.getOrNull()?.price ?: 0.0, 0.01)
        coVerify(exactly = 1) { api.getAdditionById("1") }
    }
    
    @Test
    fun `getAdditionById returns failure when API response has error flag`() = runTest {
        // Given
        val response = ApiResponse(message = testAdditionDto, error = true)
        coEvery { api.getAdditionById("999") } returns response
        
        // When
        val result = repository.getAdditionById("999")
        
        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("Addition not found") == true)
    }
    
    @Test
    fun `getAdditionById returns failure on network error`() = runTest {
        // Given
        coEvery { api.getAdditionById("1") } throws IOException("Network error")
        
        // When
        val result = repository.getAdditionById("1")
        
        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("Network error") == true)
    }
    
    @Test
    fun `createAddition returns success when API response is successful`() = runTest {
        // Given
        val response = ApiResponse(message = testAdditionDto, error = false)
        coEvery { api.createAddition(any()) } returns response
        
        // When
        val result = repository.createAddition(testAddition)
        
        // Then
        assertTrue(result.isSuccess)
        assertEquals("Extra Cheese", result.getOrNull()?.name)
        assertEquals("Additional cheese topping", result.getOrNull()?.description)
        assertEquals(2.50, result.getOrNull()?.price ?: 0.0, 0.01)
        coVerify(exactly = 1) { api.createAddition(any()) }
    }
    
    @Test
    fun `createAddition returns failure when API response has error flag`() = runTest {
        // Given
        val response = ApiResponse(message = testAdditionDto, error = true)
        coEvery { api.createAddition(any()) } returns response
        
        // When
        val result = repository.createAddition(testAddition)
        
        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("Addition creation failed") == true)
    }
    
    @Test
    fun `createAddition returns failure on network error`() = runTest {
        // Given
        coEvery { api.createAddition(any()) } throws IOException("Network error")
        
        // When
        val result = repository.createAddition(testAddition)
        
        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("Network error") == true)
    }
    
    @Test
    fun `updateAddition returns success when API response is successful`() = runTest {
        // Given
        val updatedAdditionDto = testAdditionDto.copy(name = "Updated Cheese", price = 3.00)
        val response = ApiResponse(message = updatedAdditionDto, error = false)
        coEvery { api.updateAddition("1", any()) } returns response
        
        // When
        val updatedAddition = testAddition.copy(name = "Updated Cheese", price = 3.00)
        val result = repository.updateAddition("1", updatedAddition)
        
        // Then
        assertTrue(result.isSuccess)
        assertEquals("Updated Cheese", result.getOrNull()?.name)
        assertEquals(3.00, result.getOrNull()?.price ?: 0.0, 0.01)
        coVerify(exactly = 1) { api.updateAddition("1", any()) }
    }
    
    @Test
    fun `updateAddition returns failure when API response has error flag`() = runTest {
        // Given
        val response = ApiResponse(message = testAdditionDto, error = true)
        coEvery { api.updateAddition("1", any()) } returns response
        
        // When
        val result = repository.updateAddition("1", testAddition)
        
        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("[]") == true)
    }
    
    @Test
    fun `updateAddition returns failure on network error`() = runTest {
        // Given
        coEvery { api.updateAddition("1", any()) } throws IOException("Network error")
        
        // When
        val result = repository.updateAddition("1", testAddition)
        
        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("Network error") == true)
    }
    
    @Test
    fun `deleteAddition returns success when API response is successful`() = runTest {
        // Given
        val response = ApiResponse(message = Unit, error = false)
        coEvery { api.deleteAddition("1") } returns response
        
        // When
        val result = repository.deleteAddition("1")
        
        // Then
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { api.deleteAddition("1") }
    }
    
    @Test
    fun `deleteAddition returns failure when API response has error flag`() = runTest {
        // Given
        val response = ApiResponse(message = Unit, error = true)
        coEvery { api.deleteAddition("1") } returns response
        
        // When
        val result = repository.deleteAddition("1")
        
        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("kotlin.Unit") == true)
    }
    
    @Test
    fun `deleteAddition returns failure on network error`() = runTest {
        // Given
        coEvery { api.deleteAddition("1") } throws IOException("Network error")
        
        // When
        val result = repository.deleteAddition("1")
        
        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("Network error") == true)
    }
    
    @Test
    fun `getAdditions maps multiple additions correctly`() = runTest {
        // Given
        val additions = listOf(
            testAdditionDto,
            testAdditionDto.copy(id = "2", name = "Extra Bacon", price = 3.00),
            testAdditionDto.copy(id = "3", name = "Extra Avocado", price = 2.00)
        )
        val response = ApiResponse(message = additions, error = false)
        coEvery { api.getAdditions(0) } returns response
        
        // When
        val result = repository.getAdditions(0)
        
        // Then
        assertTrue(result.isSuccess)
        assertEquals(3, result.getOrNull()?.size)
        assertEquals("Extra Cheese", result.getOrNull()?.get(0)?.name)
        assertEquals("Extra Bacon", result.getOrNull()?.get(1)?.name)
        assertEquals("Extra Avocado", result.getOrNull()?.get(2)?.name)
        assertEquals(2.50, result.getOrNull()?.get(0)?.price ?: 0.0, 0.01)
        assertEquals(3.00, result.getOrNull()?.get(1)?.price ?: 0.0, 0.01)
        assertEquals(2.00, result.getOrNull()?.get(2)?.price ?: 0.0, 0.01)
    }
    
    @Test
    fun `repository handles additions with null imageUrl`() = runTest {
        // Given
        val additionWithoutImage = testAdditionDto.copy(imageUrl = null)
        val response = ApiResponse(message = additionWithoutImage, error = false)
        coEvery { api.getAdditionById("1") } returns response
        
        // When
        val result = repository.getAdditionById("1")
        
        // Then
        assertTrue(result.isSuccess)
        assertNull(result.getOrNull()?.imageUrl)
    }
    
    @Test
    fun `repository handles INACTIVE state correctly`() = runTest {
        // Given
        val inactiveAddition = testAdditionDto.copy(state = "INACTIVE")
        val response = ApiResponse(message = inactiveAddition, error = false)
        coEvery { api.getAdditionById("1") } returns response
        
        // When
        val result = repository.getAdditionById("1")
        
        // Then
        assertTrue(result.isSuccess)
        assertEquals(AdditionState.INACTIVE, result.getOrNull()?.state)
    }
}
