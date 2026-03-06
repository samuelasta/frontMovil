package com.example.smartrestaurant.presentation.common.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter

@Composable
fun ImagePicker(
    imageUri: String?,
    onImageSelected: (Uri) -> Unit,
    modifier: Modifier = Modifier
) {
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { onImageSelected(it) }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (imageUri != null) {
            // Show image preview
            Card(
                modifier = Modifier
                    .size(200.dp)
                    .clickable { galleryLauncher.launch("image/*") },
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Image(
                    painter = rememberAsyncImagePainter(imageUri),
                    contentDescription = "Imagen seleccionada",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            TextButton(onClick = { galleryLauncher.launch("image/*") }) {
                Icon(Icons.Default.Image, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cambiar imagen")
            }
        } else {
            // Show placeholder
            Card(
                modifier = Modifier
                    .size(200.dp)
                    .clickable { galleryLauncher.launch("image/*") }
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.outline,
                        shape = MaterialTheme.shapes.medium
                    ),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AddAPhoto,
                        contentDescription = "Seleccionar imagen",
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Seleccionar imagen",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
