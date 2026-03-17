package com.example.smartrestaurant.di

import com.example.smartrestaurant.BuildConfig
import com.example.smartrestaurant.data.remote.api.AdditionApi
import com.example.smartrestaurant.data.remote.api.CategoryApi
import com.example.smartrestaurant.data.remote.api.DishApi
import com.example.smartrestaurant.data.remote.api.DrinkApi
import com.example.smartrestaurant.data.remote.api.InventoryApi
import com.example.smartrestaurant.data.remote.api.ProductApi
import com.example.smartrestaurant.data.remote.api.SupplierApi
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) {
                    HttpLoggingInterceptor.Level.BODY
                } else {
                    HttpLoggingInterceptor.Level.NONE
                }
            })
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        json: Json
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    @Provides
    @Singleton
    fun provideProductApi(retrofit: Retrofit): ProductApi {
        return retrofit.create(ProductApi::class.java)
    }

    @Provides
    @Singleton
    fun provideSupplierApi(retrofit: Retrofit): SupplierApi {
        return retrofit.create(SupplierApi::class.java)
    }

    @Provides
    @Singleton
    fun provideCategoryApi(retrofit: Retrofit): CategoryApi {
        return retrofit.create(CategoryApi::class.java)
    }
    
    @Provides
    @Singleton
    fun provideDishApi(retrofit: Retrofit): DishApi {
        return retrofit.create(DishApi::class.java)
    }
    
    @Provides
    @Singleton
    fun provideDrinkApi(retrofit: Retrofit): DrinkApi {
        return retrofit.create(DrinkApi::class.java)
    }
    
    @Provides
    @Singleton
    fun provideAdditionApi(retrofit: Retrofit): AdditionApi {
        return retrofit.create(AdditionApi::class.java)
    }
    
    @Provides
    @Singleton
    fun provideInventoryApi(retrofit: Retrofit): InventoryApi {
        return retrofit.create(InventoryApi::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): com.example.smartrestaurant.data.remote.api.AuthApi {
        return retrofit.create(com.example.smartrestaurant.data.remote.api.AuthApi::class.java)
    }
}
