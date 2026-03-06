package com.example.smartrestaurant.data.repository

import com.example.smartrestaurant.data.mapper.CategoryMapper
import com.example.smartrestaurant.data.remote.api.CategoryApi
import com.example.smartrestaurant.data.remote.dto.ApiResponse
import com.example.smartrestaurant.data.remote.dto.CategoryDto
import com.example.smartrestaurant.domain.model.Category
import com.example.smartrestaurant.domain.model.CategoryState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.io.IOException

/**
 * Unit tests for CategoryRepositoryImpl.
 * Tests error handling for network errors, API errors, and successful responses.
 * 
 * Validates: Requirements 5.1, 5.3, 5.6, 5.7, 13.3, 13.4
 */
class CategoryRepositoryImplTest {
    
    private lateinit var api: CategoryApi
    private lateinit var mapper: CategoryMapper
    private lateinit var repository: CategoryRepositoryImpl
    
    private val testCategoryDto = CategoryDto(
        id = "1",
        name = "Test Category",
        description = "Test Description",
        state = "ACTIVE"
    )
    
    private val testCategory = Category(
        id = "1",
        name = "Test Category",
        description = "Test Description",
        state = CategoryState.ACTIVE
    )
    
    @Before
    fun setup() {
        api = mockk()
        mapper = CategoryMapper()
        repository = CategoryRepositoryImpl(api, mapper)
    }
    
    @Test
    fun `getCategories returns success when API response is successful`() = runTest {
        // Given
        val response = ApiResponse(message = listOf(testCategoryDto), error = false)
        coEvery { api.getCategories() } returns response
        
        // When
        val result = repository.getCategories()
        
        // Then
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
        assertEquals("Test Category", result.getOrNull()?.first()?.name)
        coVerify(exactly = 1) { api.getCategories() }
    }
    
    @Test
    fun `getCategories returns failure when API response has error flag`() = runTest {
        // Given
        val response = ApiResponse(message = "Error fetching categories", error = true)
        coEvery { api.getCategories() } returns response
        
        // When
        val result = repository.getCategories()
        
        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("Error fetching categories") == true)
    }
    
    @Test
    fun `getCategories returns failure on network error`() = runTest {
        // Given
        coEvery { api.getCategories() } throws IOException("Network error")
        
        // When
        val result = repository.getCategories()
        
        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("Network error") == true)
    }
    
    @Test
    fun `getCategoryById returns success when API response is successful`() = runTest {
        // Given
        val response = ApiResponse(message = testCategoryDto, error = false)
        coEvery { api.getCategoryById("1") } returns response
        
        // When
        val result = repository.getCategoryById("1")
        
        // Then
        assertTrue(result.isSuccess)
        assertEquals("Test Category", result.getOrNull()?.name)
        assertEquals("Test Description", result.getOrNull()?.description)
        coVerify(exactly = 1) { api.getCategoryById("1") }
    }
    
    @Test
    fun `getCategoryById returns failure when API response has error flag`() = runTest {
        // Given
        val response = ApiResponse(message = "Category not found", error = true)
        coEvery { api.getCategoryById("999") } returns response
        
        // When
        val result = repository.getCategoryById("999")
        
        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("Category not found") == true)
    }
    
    @Test
    fun `getCategoryById returns failure on network error`() = runTest {
        // Given
        coEvery { api.getCategoryById("1") } throws IOException("Network error")
        
        // When
        val result = repository.getCategoryById("1")
        
        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("Network error") == true)
    }
    
    @Test
    fun `createCategory returns success when API response is successful`() = runTest {
        // Given
        val response = ApiResponse(message = testCategoryDto, error = false)
        coEvery { api.createCategory(any()) } returns response
        
        // When
        val result = repository.createCategory(testCategory)
        
        // Then
        assertTrue(result.isSuccess)
        assertEquals("Test Category", result.getOrNull()?.name)
        assertEquals("Test Description", result.getOrNull()?.description)
        coVerify(exactly = 1) { api.createCategory(any()) }
    }
    
    @Test
    fun `createCategory returns failure when API response has error flag`() = runTest {
        // Given
        val response = ApiResponse(message = "Category creation failed", error = true)
        coEvery { api.createCategory(any()) } returns response
        
        // When
        val result = repository.createCategory(testCategory)
        
        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("Category creation failed") == true)
    }
    
    @Test
    fun `createCategory returns failure on network error`() = runTest {
        // Given
        coEvery { api.createCategory(any()) } throws IOException("Network error")
        
        // When
        val result = repository.createCategory(testCategory)
        
        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("Network error") == true)
    }
    
    @Test
    fun `updateCategory returns success when API response is successful`() = runTest {
        // Given
        val updatedCategoryDto = testCategoryDto.copy(name = "Updated Category")
        val response = ApiResponse(message = updatedCategoryDto, error = false)
        coEvery { api.updateCategory("1", any()) } returns response
        
        // When
        val updatedCategory = testCategory.copy(name = "Updated Category")
        val result = repository.updateCategory("1", updatedCategory)
        
        // Then
        assertTrue(result.isSuccess)
        assertEquals("Updated Category", result.getOrNull()?.name)
        coVerify(exactly = 1) { api.updateCategory("1", any()) }
    }
    
    @Test
    fun `updateCategory returns failure when API response has error flag`() = runTest {
        // Given
        val response = ApiResponse(message = "Update failed", error = true)
        coEvery { api.updateCategory("1", any()) } returns response
        
        // When
        val result = repository.updateCategory("1", testCategory)
        
        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("Update failed") == true)
    }
    
    @Test
    fun `updateCategory returns failure on network error`() = runTest {
        // Given
        coEvery { api.updateCategory("1", any()) } throws IOException("Network error")
        
        // When
        val result = repository.updateCategory("1", testCategory)
        
        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("Network error") == true)
    }
    
    @Test
    fun `deleteCategory returns success when API response is successful`() = runTest {
        // Given
        val response = ApiResponse(message = Unit, error = false)
        coEvery { api.deleteCategory("1") } returns response
        
        // When
        val result = repository.deleteCategory("1")
        
        // Then
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { api.deleteCategory("1") }
    }
    
    @Test
    fun `deleteCategory returns failure when API response has error flag`() = runTest {
        // Given
        val response = ApiResponse(message = "Delete failed", error = true)
        coEvery { api.deleteCategory("1") } returns response
        
        // When
        val result = repository.deleteCategory("1")
        
        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("Delete failed") == true)
    }
    
    @Test
    fun `deleteCategory returns failure on network error`() = runTest {
        // Given
        coEvery { api.deleteCategory("1") } throws IOException("Network error")
        
        // When
        val result = repository.deleteCategory("1")
        
        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("Network error") == true)
    }
    
    @Test
    fun `getCategories maps multiple categories correctly`() = runTest {
        // Given
        val categories = listOf(
            testCategoryDto,
            testCategoryDto.copy(id = "2", name = "Category 2"),
            testCategoryDto.copy(id = "3", name = "Category 3")
        )
        val response = ApiResponse(message = categories, error = false)
        coEvery { api.getCategories() } returns response
        
        // When
        val result = repository.getCategories()
        
        // Then
        assertTrue(result.isSuccess)
        assertEquals(3, result.getOrNull()?.size)
        assertEquals("Test Category", result.getOrNull()?.get(0)?.name)
        assertEquals("Category 2", result.getOrNull()?.get(1)?.name)
        assertEquals("Category 3", result.getOrNull()?.get(2)?.name)
    }
}
