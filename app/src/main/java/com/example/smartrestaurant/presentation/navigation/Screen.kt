package com.example.smartrestaurant.presentation.navigation

sealed class Screen(val route: String) {
    // Auth screens
    object Login : Screen("login")
    object Register : Screen("register")
    object VerifyEmail : Screen("verify-email/{email}") {
        fun createRoute(email: String) = "verify-email/${java.net.URLEncoder.encode(email, "UTF-8")}"
    }

    // Product screens
    object ProductList : Screen("products")
    object ProductDetail : Screen("products/{id}") {
        fun createRoute(id: String) = "products/$id"
    }
    object ProductForm : Screen("products/form?id={id}") {
        fun createRoute(id: String? = null) = "products/form${id?.let { "?id=$it" } ?: ""}"
    }
    
    // Supplier screens
    object SupplierList : Screen("suppliers")
    object SupplierDetail : Screen("suppliers/{id}") {
        fun createRoute(id: String) = "suppliers/$id"
    }
    object SupplierForm : Screen("suppliers/form?id={id}") {
        fun createRoute(id: String? = null) = "suppliers/form${id?.let { "?id=$it" } ?: ""}"
    }
    
    // Category screens
    object CategoryList : Screen("categories")
    object CategoryDetail : Screen("categories/{id}") {
        fun createRoute(id: String) = "categories/$id"
    }
    object CategoryForm : Screen("categories/form?id={id}") {
        fun createRoute(id: String? = null) = "categories/form${id?.let { "?id=$it" } ?: ""}"
    }
    
    // Dish screens
    object DishList : Screen("dishes")
    object DishDetail : Screen("dishes/{id}") {
        fun createRoute(id: String) = "dishes/$id"
    }
    object DishForm : Screen("dishes/form?id={id}") {
        fun createRoute(id: String? = null) = "dishes/form${id?.let { "?id=$it" } ?: ""}"
    }
    
    // Drink screens
    object DrinkList : Screen("drinks")
    object DrinkDetail : Screen("drinks/{id}") {
        fun createRoute(id: String) = "drinks/$id"
    }
    object DrinkForm : Screen("drinks/form?id={id}") {
        fun createRoute(id: String? = null) = "drinks/form${id?.let { "?id=$it" } ?: ""}"
    }
    
    // Addition screens
    object AdditionList : Screen("additions")
    object AdditionDetail : Screen("additions/{id}") {
        fun createRoute(id: String) = "additions/$id"
    }
    object AdditionForm : Screen("additions/form?id={id}") {
        fun createRoute(id: String? = null) = "additions/form${id?.let { "?id=$it" } ?: ""}"
    }
    
    // Inventory screens
    object InventoryMovementList : Screen("inventory")
    object InventoryMovementDetail : Screen("inventory/{id}") {
        fun createRoute(id: String) = "inventory/$id"
    }
}
