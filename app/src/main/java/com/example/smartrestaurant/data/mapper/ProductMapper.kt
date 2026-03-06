package com.example.smartrestaurant.data.mapper

import com.example.smartrestaurant.data.remote.dto.ProductDto
import com.example.smartrestaurant.domain.model.Product
import com.example.smartrestaurant.domain.model.ProductState
import javax.inject.Inject

class ProductMapper @Inject constructor(
    private val supplierMapper: SupplierMapper
) {
    
    fun toDomain(dto: ProductDto): Product = Product(
        id = dto.id,
        name = dto.name,
        description = dto.description,
        weight = dto.weight,
        unit = dto.unit,
        price = dto.price,
        stock = dto.stock,
        minimumStock = dto.minimumStock,
        imageUrl = dto.imageUrl,
        state = ProductState.valueOf(dto.state),
        supplier = supplierMapper.toDomain(dto.suplier)
    )
    
    fun toDto(domain: Product): ProductDto = ProductDto(
        id = domain.id,
        name = domain.name,
        description = domain.description,
        weight = domain.weight,
        unit = domain.unit,
        price = domain.price,
        stock = domain.stock,
        minimumStock = domain.minimumStock,
        imageUrl = domain.imageUrl,
        state = domain.state.name,
        suplier = supplierMapper.toDto(domain.supplier)
    )
}
