package com.example.smartrestaurant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.smartrestaurant.presentation.navigation.NavGraph
import com.example.smartrestaurant.ui.theme.SmartRestaurantTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SmartRestaurantTheme {
                NavGraph()
            }
        }
    }
}
