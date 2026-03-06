package com.example.smartrestaurant.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

enum class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    PRODUCTS("products", "Productos", Icons.Default.Inventory),
    SUPPLIERS("suppliers", "Proveedores", Icons.Default.LocalShipping),
    CATEGORIES("categories", "Categorías", Icons.Default.Category),
    DISHES("dishes", "Platos", Icons.Default.Restaurant),
    DRINKS("drinks", "Bebidas", Icons.Default.LocalBar),
    ADDITIONS("additions", "Adiciones", Icons.Default.Add),
    INVENTORY("inventory", "Movimientos", Icons.Default.History)
}

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar {
                BottomNavItem.entries.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title) },
                        selected = currentRoute?.startsWith(item.route) == true,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.ProductList.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            // Product routes
            composable(Screen.ProductList.route) {
                // ProductListScreen will be implemented in Task 6.3
                Text("Product List Screen - To be implemented")
            }
            
            // Supplier routes
            composable(Screen.SupplierList.route) {
                Text("Supplier List Screen - To be implemented")
            }
            
            // Category routes
            composable(Screen.CategoryList.route) {
                Text("Category List Screen - To be implemented")
            }
            
            // Dish routes
            composable(Screen.DishList.route) {
                Text("Dish List Screen - To be implemented")
            }
            
            // Drink routes
            composable(Screen.DrinkList.route) {
                Text("Drink List Screen - To be implemented")
            }
            
            // Addition routes
            composable(Screen.AdditionList.route) {
                Text("Addition List Screen - To be implemented")
            }
            
            // Inventory routes
            composable(Screen.InventoryMovementList.route) {
                Text("Inventory Movement List Screen - To be implemented")
            }
        }
    }
}
