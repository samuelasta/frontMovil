package com.example.smartrestaurant.di

import com.example.smartrestaurant.data.repository.AdditionRepositoryImpl
import com.example.smartrestaurant.data.repository.CategoryRepositoryImpl
import com.example.smartrestaurant.data.repository.DishRepositoryImpl
import com.example.smartrestaurant.data.repository.DrinkRepositoryImpl
import com.example.smartrestaurant.data.repository.InventoryRepositoryImpl
import com.example.smartrestaurant.data.repository.ProductRepositoryImpl
import com.example.smartrestaurant.data.repository.SupplierRepositoryImpl
import com.example.smartrestaurant.domain.repository.AdditionRepository
import com.example.smartrestaurant.domain.repository.CategoryRepository
import com.example.smartrestaurant.domain.repository.DishRepository
import com.example.smartrestaurant.domain.repository.DrinkRepository
import com.example.smartrestaurant.domain.repository.InventoryRepository
import com.example.smartrestaurant.domain.repository.ProductRepository
import com.example.smartrestaurant.domain.repository.SupplierRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    @Binds
    @Singleton
    abstract fun bindProductRepository(
        productRepositoryImpl: ProductRepositoryImpl
    ): ProductRepository
    
    @Binds
    @Singleton
    abstract fun bindSupplierRepository(
        supplierRepositoryImpl: SupplierRepositoryImpl
    ): SupplierRepository
    
    @Binds
    @Singleton
    abstract fun bindCategoryRepository(
        categoryRepositoryImpl: CategoryRepositoryImpl
    ): CategoryRepository
    
    @Binds
    @Singleton
    abstract fun bindDishRepository(
        dishRepositoryImpl: DishRepositoryImpl
    ): DishRepository
    
    @Binds
    @Singleton
    abstract fun bindDrinkRepository(
        drinkRepositoryImpl: DrinkRepositoryImpl
    ): DrinkRepository
    
    @Binds
    @Singleton
    abstract fun bindAdditionRepository(
        additionRepositoryImpl: AdditionRepositoryImpl
    ): AdditionRepository
    
    @Binds
    @Singleton
    abstract fun bindInventoryRepository(
        inventoryRepositoryImpl: InventoryRepositoryImpl
    ): InventoryRepository
}
