package com.example.smartrestaurant.presentation.common.util

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream

object ImageUploadUtil {
    
    suspend fun uploadImage(
        context: Context,
        imageUri: Uri,
        uploadUrl: String = "https://api.cloudinary.com/v1_1/YOUR_CLOUD_NAME/image/upload",
        uploadPreset: String = "YOUR_UPLOAD_PRESET"
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            // Convert URI to File
            val file = uriToFile(context, imageUri)
            
            // Create multipart request
            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.name, file.asRequestBody("image/*".toMediaTypeOrNull()))
                .addFormDataPart("upload_preset", uploadPreset)
                .build()
            
            val request = Request.Builder()
                .url(uploadUrl)
                .post(requestBody)
                .build()
            
            val client = OkHttpClient()
            val response = client.newCall(request).execute()
            
            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                val jsonObject = JSONObject(responseBody ?: "")
                val imageUrl = jsonObject.getString("secure_url")
                Result.success(imageUrl)
            } else {
                Result.failure(Exception("Error al subir imagen: ${response.code}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun uploadImageWithRetry(
        context: Context,
        imageUri: Uri,
        uploadUrl: String = "https://api.cloudinary.com/v1_1/YOUR_CLOUD_NAME/image/upload",
        uploadPreset: String = "YOUR_UPLOAD_PRESET",
        maxRetries: Int = 3
    ): Result<String> {
        var lastException: Exception? = null
        
        repeat(maxRetries) { attempt ->
            val result = uploadImage(context, imageUri, uploadUrl, uploadPreset)
            if (result.isSuccess) {
                return result
            }
            lastException = result.exceptionOrNull() as? Exception
        }
        
        return Result.failure(lastException ?: Exception("Error al subir imagen después de $maxRetries intentos"))
    }
    
    private fun uriToFile(context: Context, uri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(uri)
        val tempFile = File.createTempFile("upload_", ".jpg", context.cacheDir)
        
        inputStream?.use { input ->
            FileOutputStream(tempFile).use { output ->
                input.copyTo(output)
            }
        }
        
        return tempFile
    }
}
