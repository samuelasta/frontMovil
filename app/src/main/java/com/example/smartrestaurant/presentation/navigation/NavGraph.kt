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
                com.example.smartrestaurant.presentation.product.ProductListScreen(
                    onNavigateToDetail = { id ->
                        navController.navigate(Screen.ProductDetail.createRoute(id))
                    },
                    onNavigateToCreate = {
                        navController.navigate(Screen.ProductForm.createRoute())
                    }
                )
            }
            
            composable(Screen.ProductDetail.route) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id") ?: return@composable
                com.example.smartrestaurant.presentation.product.ProductDetailScreen(
                    productId = id,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToEdit = { navController.navigate(Screen.ProductForm.createRoute(id)) }
                )
            }
            
            composable(Screen.ProductForm.route) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id")
                com.example.smartrestaurant.presentation.product.ProductFormScreen(
                    productId = id,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            // Supplier routes
            composable(Screen.SupplierList.route) {
                com.example.smartrestaurant.presentation.supplier.SupplierListScreen(
                    onNavigateToDetail = { id ->
                        navController.navigate(Screen.SupplierDetail.createRoute(id))
                    },
                    onNavigateToCreate = {
                        navController.navigate(Screen.SupplierForm.createRoute())
                    }
                )
            }
            
            composable(Screen.SupplierDetail.route) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id") ?: return@composable
                com.example.smartrestaurant.presentation.supplier.SupplierDetailScreen(
                    supplierId = id,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToEdit = { navController.navigate(Screen.SupplierForm.createRoute(id)) }
                )
            }
            
            composable(Screen.SupplierForm.route) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id")
                com.example.smartrestaurant.presentation.supplier.SupplierFormScreen(
                    supplierId = id,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            // Category routes
            composable(Screen.CategoryList.route) {
                com.example.smartrestaurant.presentation.category.CategoryListScreen(
                    onNavigateToDetail = { id ->
                        navController.navigate(Screen.CategoryDetail.createRoute(id))
                    },
                    onNavigateToCreate = {
                        navController.navigate(Screen.CategoryForm.createRoute())
                    }
                )
            }
            
            composable(Screen.CategoryDetail.route) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id") ?: return@composable
                com.example.smartrestaurant.presentation.category.CategoryDetailScreen(
                    categoryId = id,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToEdit = { navController.navigate(Screen.CategoryForm.createRoute(id)) }
                )
            }
            
            composable(Screen.CategoryForm.route) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id")
                com.example.smartrestaurant.presentation.category.CategoryFormScreen(
                    categoryId = id,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            // Dish routes
            composable(Screen.DishList.route) {
                com.example.smartrestaurant.presentation.dish.DishListScreen(
                    onNavigateToDetail = { id ->
                        navController.navigate(Screen.DishDetail.createRoute(id))
                    },
                    onNavigateToCreate = {
                        navController.navigate(Screen.DishForm.createRoute())
                    }
                )
            }
            
            composable(Screen.DishDetail.route) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id") ?: return@composable
                com.example.smartrestaurant.presentation.dish.DishDetailScreen(
                    dishId = id,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToEdit = { navController.navigate(Screen.DishForm.createRoute(id)) }
                )
            }
            
            composable(Screen.DishForm.route) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id")
                com.example.smartrestaurant.presentation.dish.DishFormScreen(
                    dishId = id,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            // Drink routes
            composable(Screen.DrinkList.route) {
                com.example.smartrestaurant.presentation.drink.DrinkListScreen(
                    onNavigateToDetail = { id ->
                        navController.navigate(Screen.DrinkDetail.createRoute(id))
                    },
                    onNavigateToCreate = {
                        navController.navigate(Screen.DrinkForm.createRoute())
                    }
                )
            }
            
            composable(Screen.DrinkDetail.route) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id") ?: return@composable
                com.example.smartrestaurant.presentation.drink.DrinkDetailScreen(
                    drinkId = id,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToEdit = { navController.navigate(Screen.DrinkForm.createRoute(id)) }
                )
            }
            
            composable(Screen.DrinkForm.route) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id")
                com.example.smartrestaurant.presentation.drink.DrinkFormScreen(
                    drinkId = id,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            // Addition routes
            composable(Screen.AdditionList.route) {
                com.example.smartrestaurant.presentation.addition.AdditionListScreen(
                    onNavigateToDetail = { id ->
                        navController.navigate(Screen.AdditionDetail.createRoute(id))
                    },
                    onNavigateToCreate = {
                        navController.navigate(Screen.AdditionForm.createRoute())
                    }
                )
            }
            
            composable(Screen.AdditionDetail.route) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id") ?: return@composable
                com.example.smartrestaurant.presentation.addition.AdditionDetailScreen(
                    additionId = id,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToEdit = { navController.navigate(Screen.AdditionForm.createRoute(id)) }
                )
            }
            
            composable(Screen.AdditionForm.route) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id")
                com.example.smartrestaurant.presentation.addition.AdditionFormScreen(
                    additionId = id,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            // Inventory routes
            composable(Screen.InventoryMovementList.route) {
                com.example.smartrestaurant.presentation.inventory.InventoryMovementListScreen(
                    onNavigateToDetail = { id ->
                        navController.navigate(Screen.InventoryMovementDetail.createRoute(id))
                    }
                )
            }
            
            composable(Screen.InventoryMovementDetail.route) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id") ?: return@composable
                com.example.smartrestaurant.presentation.inventory.InventoryMovementDetailScreen(
                    movementId = id,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
