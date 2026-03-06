package com.example.smartrestaurant.data.mapper

import com.example.smartrestaurant.data.remote.dto.CategoryDto
import com.example.smartrestaurant.domain.model.Category
import com.example.smartrestaurant.domain.model.CategoryState
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CategoryMapperTest {
    
    private lateinit var mapper: CategoryMapper
    
    @Before
    fun setup() {
        mapper = CategoryMapper()
    }
    
    @Test
    fun `toDomain should map CategoryDto to Category correctly`() {
        // Given
        val dto = CategoryDto(
            id = "1",
            name = "Appetizers",
            description = "Starter dishes",
            state = "ACTIVE"
        )
        
        // When
        val domain = mapper.toDomain(dto)
        
        // Then
        assertEquals("1", domain.id)
        assertEquals("Appetizers", domain.name)
        assertEquals("Starter dishes", domain.description)
        assertEquals(CategoryState.ACTIVE, domain.state)
    }
    
    @Test
    fun `toDto should map Category to CategoryDto correctly`() {
        // Given
        val domain = Category(
            id = "2",
            name = "Main Courses",
            description = "Main dishes",
            state = CategoryState.INACTIVE
        )
        
        // When
        val dto = mapper.toDto(domain)
        
        // Then
        assertEquals("2", dto.id)
        assertEquals("Main Courses", dto.name)
        assertEquals("Main dishes", dto.description)
        assertEquals("INACTIVE", dto.state)
    }
    
    @Test
    fun `toDomain should handle null id`() {
        // Given
        val dto = CategoryDto(
            id = null,
            name = "Desserts",
            description = "Sweet dishes",
            state = "ACTIVE"
        )
        
        // When
        val domain = mapper.toDomain(dto)
        
        // Then
        assertEquals(null, domain.id)
        assertEquals("Desserts", domain.name)
        assertEquals("Sweet dishes", domain.description)
        assertEquals(CategoryState.ACTIVE, domain.state)
    }
    
    @Test
    fun `toDto should handle null id`() {
        // Given
        val domain = Category(
            id = null,
            name = "Beverages",
            description = "Drinks",
            state = CategoryState.ACTIVE
        )
        
        // When
        val dto = mapper.toDto(domain)
        
        // Then
        assertEquals(null, dto.id)
        assertEquals("Beverages", dto.name)
        assertEquals("Drinks", dto.description)
        assertEquals("ACTIVE", dto.state)
    }
    
    @Test
    fun `toDomain and toDto should be reversible`() {
        // Given
        val originalDto = CategoryDto(
            id = "3",
            name = "Salads",
            description = "Fresh salads",
            state = "ACTIVE"
        )
        
        // When
        val domain = mapper.toDomain(originalDto)
        val resultDto = mapper.toDto(domain)
        
        // Then
        assertEquals(originalDto.id, resultDto.id)
        assertEquals(originalDto.name, resultDto.name)
        assertEquals(originalDto.description, resultDto.description)
        assertEquals(originalDto.state, resultDto.state)
    }
}
