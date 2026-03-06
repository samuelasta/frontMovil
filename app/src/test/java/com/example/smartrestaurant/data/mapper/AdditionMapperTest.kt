package com.example.smartrestaurant.data.mapper

import com.example.smartrestaurant.data.remote.dto.AdditionDto
import com.example.smartrestaurant.domain.model.Addition
import com.example.smartrestaurant.domain.model.AdditionState
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for AdditionMapper
 * Validates: Requirements 8.1, 8.4
 */
class AdditionMapperTest {
    
    private lateinit var mapper: AdditionMapper
    
    @Before
    fun setup() {
        mapper = AdditionMapper()
    }
    
    @Test
    fun `toDomain maps AdditionDto to Addition correctly`() {
        // Given
        val dto = AdditionDto(
            id = "1",
            name = "Extra Cheese",
            description = "Additional cheese topping",
            price = 2.50,
            imageUrl = "https://example.com/cheese.jpg",
            state = "ACTIVE"
        )
        
        // When
        val domain = mapper.toDomain(dto)
        
        // Then
        assertEquals("1", domain.id)
        assertEquals("Extra Cheese", domain.name)
        assertEquals("Additional cheese topping", domain.description)
        assertEquals(2.50, domain.price, 0.001)
        assertEquals("https://example.com/cheese.jpg", domain.imageUrl)
        assertEquals(AdditionState.ACTIVE, domain.state)
    }
    
    @Test
    fun `toDomain handles null id correctly`() {
        // Given
        val dto = AdditionDto(
            id = null,
            name = "Extra Bacon",
            description = "Additional bacon",
            price = 3.00,
            imageUrl = null,
            state = "ACTIVE"
        )
        
        // When
        val domain = mapper.toDomain(dto)
        
        // Then
        assertEquals(null, domain.id)
        assertEquals("Extra Bacon", domain.name)
        assertEquals(null, domain.imageUrl)
    }
    
    @Test
    fun `toDomain maps INACTIVE state correctly`() {
        // Given
        val dto = AdditionDto(
            id = "2",
            name = "Removed Addition",
            description = "No longer available",
            price = 1.00,
            imageUrl = null,
            state = "INACTIVE"
        )
        
        // When
        val domain = mapper.toDomain(dto)
        
        // Then
        assertEquals(AdditionState.INACTIVE, domain.state)
    }
    
    @Test
    fun `toDto maps Addition to AdditionDto correctly`() {
        // Given
        val domain = Addition(
            id = "3",
            name = "Guacamole",
            description = "Fresh guacamole",
            price = 4.50,
            imageUrl = "https://example.com/guac.jpg",
            state = AdditionState.ACTIVE
        )
        
        // When
        val dto = mapper.toDto(domain)
        
        // Then
        assertEquals("3", dto.id)
        assertEquals("Guacamole", dto.name)
        assertEquals("Fresh guacamole", dto.description)
        assertEquals(4.50, dto.price, 0.001)
        assertEquals("https://example.com/guac.jpg", dto.imageUrl)
        assertEquals("ACTIVE", dto.state)
    }
    
    @Test
    fun `toDto handles null values correctly`() {
        // Given
        val domain = Addition(
            id = null,
            name = "New Addition",
            description = "Description",
            price = 1.50,
            imageUrl = null,
            state = AdditionState.ACTIVE
        )
        
        // When
        val dto = mapper.toDto(domain)
        
        // Then
        assertEquals(null, dto.id)
        assertEquals(null, dto.imageUrl)
    }
    
    @Test
    fun `toDto maps INACTIVE state correctly`() {
        // Given
        val domain = Addition(
            id = "4",
            name = "Inactive Addition",
            description = "Not available",
            price = 2.00,
            imageUrl = null,
            state = AdditionState.INACTIVE
        )
        
        // When
        val dto = mapper.toDto(domain)
        
        // Then
        assertEquals("INACTIVE", dto.state)
    }
    
    @Test
    fun `toDomain and toDto are inverse operations`() {
        // Given
        val originalDto = AdditionDto(
            id = "5",
            name = "Sour Cream",
            description = "Fresh sour cream",
            price = 1.75,
            imageUrl = "https://example.com/cream.jpg",
            state = "ACTIVE"
        )
        
        // When
        val domain = mapper.toDomain(originalDto)
        val resultDto = mapper.toDto(domain)
        
        // Then
        assertEquals(originalDto.id, resultDto.id)
        assertEquals(originalDto.name, resultDto.name)
        assertEquals(originalDto.description, resultDto.description)
        assertEquals(originalDto.price, resultDto.price, 0.001)
        assertEquals(originalDto.imageUrl, resultDto.imageUrl)
        assertEquals(originalDto.state, resultDto.state)
    }
}
