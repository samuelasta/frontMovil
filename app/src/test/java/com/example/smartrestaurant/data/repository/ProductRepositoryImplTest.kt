package com.example.smartrestaurant.data.repository

import com.example.smartrestaurant.data.mapper.ProductMapper
import com.example.smartrestaurant.data.mapper.SupplierMapper
import com.example.smartrestaurant.data.remote.api.ProductApi
import com.example.smartrestaurant.data.remote.dto.ApiResponse
import com.example.smartrestaurant.data.remote.dto.ProductDto
import com.example.smartrestaurant.data.remote.dto.StockMovementDto
import com.example.smartrestaurant.data.remote.dto.SupplierDto
import com.example.smartrestaurant.domain.model.Product
import com.example.smartrestaurant.domain.model.ProductState
import com.example.smartrestaurant.domain.model.Supplier
import com.example.smartrestaurant.domain.model.SupplierState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import java.io.IOException

/**
 * Unit tests for ProductRepositoryImpl.
 * Tests error handling for network errors, API errors, and successful responses.
 */
class ProductRepositoryImplTest {
    
    private lateinit var api: ProductApi
    private lateinit var mapper: ProductMapper
    private lateinit var repository: ProductRepositoryImpl
    
    private val testSupplierDto = SupplierDto(
        id = "1",
        name = "Test Supplier",
        email = "supplier@test.com",
        phone = "123456789",
        address = "Test Address",
        state = "ACTIVE"
    )
    
    private val testProductDto = ProductDto(
        id = "1",
        name = "Test Product",
        description = "Test Description",
        weight = 1.0,
        unit = "kg",
        price = 10.0,
        stock = 5.0,
        minimumStock = 2.0,
        imageUrl = "photo1.jpg",
        state = "ACTIVE",
        suplier = testSupplierDto
    )
    
    private val testSupplier = Supplier(
        id = "1",
        name = "Test Supplier",
        email = "supplier@test.com",
        phone = "123456789",
        address = "Test Address",
        state = SupplierState.ACTIVE
    )
    
    private val testProduct = Product(
        id = "1",
        name = "Test Product",
        description = "Test Description",
        weight = 1.0,
        unit = "kg",
        price = 10.0,
        stock = 5.0,
        minimumStock = 2.0,
        imageUrl = "photo1.jpg",
        state = ProductState.ACTIVE,
        supplier = testSupplier
    )
    
    @Before
    fun setup() {
        api = mockk()
        val supplierMapper = SupplierMapper()
        mapper = ProductMapper(supplierMapper)
        repository = ProductRepositoryImpl(api, mapper)
    }
    
    @Test
    fun `getProducts returns success when API response is successful`() = runTest {
        // Given
        val response = ApiResponse(message = listOf(testProductDto), error = false)
        coEvery { api.getProducts(0) } returns response
        
        // When
        val result = repository.getProducts(0)
        
        // Then
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
        assertEquals("Test Product", result.getOrNull()?.first()?.name)
        coVerify(exactly = 1) { api.getProducts(0) }
    }
    
    @Test
    fun `getProducts returns failure when API response has error flag`() = runTest {
        // Given
        val response = ApiResponse(message = "Error message", error = true)
        coEvery { api.getProducts(0) } returns response
        
        // When
        val result = repository.getProducts(0)
        
        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("Error message") == true)
    }
    
    @Test
    fun `getProducts returns failure on network error`() = runTest {
        // Given
        coEvery { api.getProducts(0) } throws IOException("Network error")
        
        // When
        val result = repository.getProducts(0)
        
        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("Network error") == true)
    }
    
    @Test
    fun `getProductById returns success when API response is successful`() = runTest {
        // Given
        val response = ApiResponse(message = testProductDto, error = false)
        coEvery { api.getProductById("1") } returns response
        
        // When
        val result = repository.getProductById("1")
        
        // Then
        assertTrue(result.isSuccess)
        assertEquals("Test Product", result.getOrNull()?.name)
        coVerify(exactly = 1) { api.getProductById("1") }
    }
    
    @Test
    fun `getProductById returns failure when API response has error flag`() = runTest {
        // Given
        val response = ApiResponse(message = "Product not found", error = true)
        coEvery { api.getProductById("999") } returns response
        
        // When
        val result = repository.getProductById("999")
        
        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("Product not found") == true)
    }
    
    @Test
    fun `createProduct returns success when API response is successful`() = runTest {
        // Given
        val response = ApiResponse(message = testProductDto, error = false)
        coEvery { api.createProduct(any()) } returns response
        
        // When
        val result = repository.createProduct(testProduct)
        
        // Then
        assertTrue(result.isSuccess)
        assertEquals("Test Product", result.getOrNull()?.name)
        coVerify(exactly = 1) { api.createProduct(any()) }
    }
    
    @Test
    fun `createProduct returns failure on network error`() = runTest {
        // Given
        coEvery { api.createProduct(any()) } throws IOException("Network error")
        
        // When
        val result = repository.createProduct(testProduct)
        
        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("Network error") == true)
    }
    
    @Test
    fun `updateProduct returns success when API response is successful`() = runTest {
        // Given
        val response = ApiResponse(message = testProductDto, error = false)
        coEvery { api.updateProduct("1", any()) } returns response
        
        // When
        val result = repository.updateProduct("1", testProduct)
        
        // Then
        assertTrue(result.isSuccess)
        assertEquals("Test Product", result.getOrNull()?.name)
        coVerify(exactly = 1) { api.updateProduct("1", any()) }
    }
    
    @Test
    fun `updateProduct returns failure when API response has error flag`() = runTest {
        // Given
        val response = ApiResponse(message = "Update failed", error = true)
        coEvery { api.updateProduct("1", any()) } returns response
        
        // When
        val result = repository.updateProduct("1", testProduct)
        
        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("Update failed") == true)
    }
    
    @Test
    fun `deleteProduct returns success when API response is successful`() = runTest {
        // Given
        val response = ApiResponse(message = Unit, error = false)
        coEvery { api.deleteProduct("1") } returns response
        
        // When
        val result = repository.deleteProduct("1")
        
        // Then
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { api.deleteProduct("1") }
    }
    
    @Test
    fun `deleteProduct returns failure when API response has error flag`() = runTest {
        // Given
        val response = ApiResponse(message = "Delete failed", error = true)
        coEvery { api.deleteProduct("1") } returns response
        
        // When
        val result = repository.deleteProduct("1")
        
        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("Delete failed") == true)
    }
    
    @Test
    fun `addStock returns success when API response is successful`() = runTest {
        // Given
        val updatedProductDto = testProductDto.copy(stock = 15.0)
        val response = ApiResponse(message = updatedProductDto, error = false)
        coEvery { api.addStock("1", any()) } returns response
        
        // When
        val result = repository.addStock("1", 10.0, "Restock")
        
        // Then
        assertTrue(result.isSuccess)
        assertEquals(15.0, result.getOrNull()?.stock, 0.01)
        coVerify(exactly = 1) { api.addStock("1", StockMovementDto(10.0)) }
    }
    
    @Test
    fun `addStock returns failure when API response has error flag`() = runTest {
        // Given
        val response = ApiResponse(message = "Stock addition failed", error = true)
        coEvery { api.addStock("1", any()) } returns response
        
        // When
        val result = repository.addStock("1", 10.0, null)
        
        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("Stock addition failed") == true)
    }
    
    @Test
    fun `subtractStock returns success when API response is successful`() = runTest {
        // Given
        val updatedProductDto = testProductDto.copy(stock = 3.0)
        val response = ApiResponse(message = updatedProductDto, error = false)
        coEvery { api.subtractStock("1", any()) } returns response
        
        // When
        val result = repository.subtractStock("1", 2.0, "Usage")
        
        // Then
        assertTrue(result.isSuccess)
        assertEquals(3.0, result.getOrNull()?.stock, 0.01)
        coVerify(exactly = 1) { api.subtractStock("1", StockMovementDto(2.0)) }
    }
    
    @Test
    fun `subtractStock returns failure when API response has error flag`() = runTest {
        // Given
        val response = ApiResponse(message = "Insufficient stock", error = true)
        coEvery { api.subtractStock("1", any()) } returns response
        
        // When
        val result = repository.subtractStock("1", 10.0, null)
        
        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("Insufficient stock") == true)
    }
    
    @Test
    fun `subtractStock returns failure on network error`() = runTest {
        // Given
        coEvery { api.subtractStock("1", any()) } throws IOException("Network error")
        
        // When
        val result = repository.subtractStock("1", 2.0, null)
        
        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("Network error") == true)
    }
}
