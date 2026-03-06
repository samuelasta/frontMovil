package com.example.smartrestaurant.data.mapper

import com.example.smartrestaurant.data.remote.dto.SupplierDto
import com.example.smartrestaurant.domain.model.Supplier
import com.example.smartrestaurant.domain.model.SupplierState
import javax.inject.Inject

class SupplierMapper @Inject constructor() {
    
    fun toDomain(dto: SupplierDto): Supplier = Supplier(
        id = dto.id,
        name = dto.name,
        email = dto.email,
        phone = dto.phone,
        address = dto.address,
        state = SupplierState.valueOf(dto.state)
    )
    
    fun toDto(domain: Supplier): SupplierDto = SupplierDto(
        id = domain.id,
        name = domain.name,
        email = domain.email,
        phone = domain.phone,
        address = domain.address,
        state = domain.state.name
    )
}
