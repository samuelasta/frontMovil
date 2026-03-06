package com.example.smartrestaurant.data.repository

import com.example.smartrestaurant.data.mapper.DrinkMapper
import com.example.smartrestaurant.data.remote.api.DrinkApi
import com.example.smartrestaurant.domain.model.Drink
import com.example.smartrestaurant.domain.repository.DrinkRepository
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

/**
 * Implementation of DrinkRepository interface.
 * Validates: Requirements 7.1, 7.3, 7.6, 7.7, 11.1, 13.3, 13.4
 */
class DrinkRepositoryImpl @Inject constructor(
    private val api: DrinkApi,
    private val mapper: DrinkMapper
) : DrinkRepository {
    
    override suspend fun getDrinks(page: Int): Result<List<Drink>> = try {
        val response = api.getDrinks(page)
        if (response.error) {
            Result.failure(Exception(response.message.toString()))
        } else {
            Result.success(response.message.map { mapper.toDomain(it) })
        }
    } catch (e: IOException) {
        Result.failure(Exception("Network error: ${e.message}"))
    } catch (e: HttpException) {
        Result.failure(Exception("Server error: ${e.code()} - ${e.message()}"))
    } catch (e: Exception) {
        Result.failure(Exception("Unexpected error: ${e.message}"))
    }
    
    override suspend fun getDrinkById(id: String): Result<Drink> = try {
        val response = api.getDrinkById(id)
        if (response.error) {
            Result.failure(Exception(response.message.toString()))
        } else {
            Result.success(mapper.toDomain(response.message))
        }
    } catch (e: IOException) {
        Result.failure(Exception("Network error: ${e.message}"))
    } catch (e: HttpException) {
        Result.failure(Exception("Server error: ${e.code()} - ${e.message()}"))
    } catch (e: Exception) {
        Result.failure(Exception("Unexpected error: ${e.message}"))
    }
    
    override suspend fun createDrink(drink: Drink): Result<Drink> = try {
        val drinkDto = mapper.toDto(drink)
        val response = api.createDrink(drinkDto)
        if (response.error) {
            Result.failure(Exception(response.message.toString()))
        } else {
            Result.success(mapper.toDomain(response.message))
        }
    } catch (e: IOException) {
        Result.failure(Exception("Network error: ${e.message}"))
    } catch (e: HttpException) {
        Result.failure(Exception("Server error: ${e.code()} - ${e.message()}"))
    } catch (e: Exception) {
        Result.failure(Exception("Unexpected error: ${e.message}"))
    }
    
    override suspend fun updateDrink(id: String, drink: Drink): Result<Drink> = try {
        val drinkDto = mapper.toDto(drink)
        val response = api.updateDrink(id, drinkDto)
        if (response.error) {
            Result.failure(Exception(response.message.toString()))
        } else {
            Result.success(mapper.toDomain(response.message))
        }
    } catch (e: IOException) {
        Result.failure(Exception("Network error: ${e.message}"))
    } catch (e: HttpException) {
        Result.failure(Exception("Server error: ${e.code()} - ${e.message()}"))
    } catch (e: Exception) {
        Result.failure(Exception("Unexpected error: ${e.message}"))
    }
    
    override suspend fun deleteDrink(id: String): Result<Unit> = try {
        val response = api.deleteDrink(id)
        if (response.error) {
            Result.failure(Exception(response.message.toString()))
        } else {
            Result.success(Unit)
        }
    } catch (e: IOException) {
        Result.failure(Exception("Network error: ${e.message}"))
    } catch (e: HttpException) {
        Result.failure(Exception("Server error: ${e.code()} - ${e.message()}"))
    } catch (e: Exception) {
        Result.failure(Exception("Unexpected error: ${e.message}"))
    }
}
