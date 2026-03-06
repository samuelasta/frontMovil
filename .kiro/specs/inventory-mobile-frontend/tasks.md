# Plan de Implementación: Inventory Mobile Frontend

## Descripción General

Este plan implementa una aplicación Android nativa con Kotlin y Jetpack Compose para el sistema de inventario de Smart Restaurant. La arquitectura sigue Clean Architecture + MVVM con tres capas: Presentation, Domain y Data.

**Stack Tecnológico:**
- Kotlin + Jetpack Compose + Material Design 3
- Retrofit + OkHttp para networking
- Hilt para inyección de dependencias
- Coil para carga de imágenes
- Navigation Compose para navegación
- Coroutines + Flow para asincronía
- Kotest para property-based testing

**Estrategia de Implementación:**
1. Setup inicial del proyecto y estructura base
2. Implementación módulo por módulo (Product → Supplier → Category → Dish → Drink → Addition → Inventory)
3. Cada módulo incluye las 3 capas completas antes de pasar al siguiente
4. Property-based tests para validar propiedades de corrección

## Tareas

- [x] 1. Setup inicial del proyecto
  - Crear proyecto Android con Kotlin y Jetpack Compose
  - Configurar dependencias en build.gradle.kts (Retrofit, Hilt, Coil, Navigation, Kotest)
  - Configurar estructura de paquetes según Clean Architecture
  - Crear módulos Hilt básicos (NetworkModule, RepositoryModule, UseCaseModule)
  - Configurar Retrofit con base URL y timeout de 30 segundos
  - _Requisitos: 13.6, 14.6_

- [x] 2. Implementar componentes comunes de UI
  - Crear tema Material Design 3 (Color.kt, Theme.kt, Type.kt)
  - Implementar LoadingIndicator composable
  - Implementar ErrorMessage composable con botón de retry
  - Implementar SearchBar composable
  - Implementar ConfirmationDialog composable
  - Implementar ImagePicker composable (cámara y galería)
  - Crear UiState sealed interface genérico
  - _Requisitos: 14.6, 13.1, 13.5, 15.1, 10.1_

- [x] 3. Implementar navegación base
  - Crear Screen sealed class con rutas para todas las pantallas
  - Implementar NavGraph con NavHost
  - Configurar bottom navigation o drawer menu con secciones principales
  - _Requisitos: 14.1, 14.2, 14.4_

- [x] 4. Módulo Product - Capa de Datos
  - [x] 4.1 Crear DTOs para Product y Supplier
    - Crear ProductDto con @Serializable
    - Crear SupplierDto con @Serializable
    - Crear ApiResponse<T> genérico
    - Crear StockRequest DTO
    - _Requisitos: 1.1, 1.3, 2.3_

  - [x] 4.2 Crear ProductApi interface con Retrofit
    - Definir endpoint GET /api/products con paginación
    - Definir endpoint GET /api/products/{id}
    - Definir endpoint POST /api/products
    - Definir endpoint PUT /api/products/{id}
    - Definir endpoint DELETE /api/products/{id}
    - Definir endpoint PUT /api/products/add-stock/{id}
    - Definir endpoint PUT /api/products/subtract-stock/{id}
    - _Requisitos: 1.1, 1.3, 1.6, 1.7, 2.3, 2.5_

  - [x] 4.3 Crear Mappers para Product
    - Implementar ProductMapper.toDomain()
    - Implementar ProductMapper.toDto()
    - Implementar SupplierMapper.toDomain()
    - Implementar SupplierMapper.toDto()
    - _Requisitos: 1.1, 1.4_

  - [x] 4.4 Implementar ProductRepositoryImpl
    - Implementar getProducts() con manejo de errores
    - Implementar getProductById() con manejo de errores
    - Implementar createProduct() con manejo de errores
    - Implementar updateProduct() con manejo de errores
    - Implementar deleteProduct() con manejo de errores
    - Implementar addStock() con manejo de errores
    - Implementar subtractStock() con manejo de errores
    - _Requisitos: 1.3, 1.6, 1.7, 1.8, 2.3, 2.5, 13.3, 13.4_

- [x] 5. Módulo Product - Capa de Dominio
  - [x] 5.1 Crear modelos de dominio
    - Crear Product data class con propiedad isLowStock
    - Crear Supplier data class
    - Crear ProductState enum
    - Crear SupplierState enum
    - _Requisitos: 1.4, 3.1_

  - [x] 5.2 Crear ProductRepository interface
    - Definir métodos suspend con Result<T>
    - _Requisitos: 1.1, 1.3, 1.6, 1.7, 2.3, 2.5_

  - [x] 5.3 Crear Use Cases para Product
    - Implementar GetProductsUseCase
    - Implementar GetProductByIdUseCase
    - Implementar CreateProductUseCase
    - Implementar UpdateProductUseCase
    - Implementar DeleteProductUseCase
    - Implementar AddStockUseCase con validación de cantidad positiva
    - Implementar SubtractStockUseCase con validación de cantidad positiva
    - _Requisitos: 1.1, 1.3, 1.6, 1.7, 2.3, 2.5, 12.3_

- [x] 6. Módulo Product - Capa de Presentación
  - [x] 6.1 Crear UI States para Product
    - Crear ProductListUiState con filtros y paginación
    - Crear ProductDetailUiState
    - Crear ProductFormUiState con campos y errores de validación
    - _Requisitos: 1.1, 1.4, 1.5, 12.1, 12.5_

  - [x] 6.2 Implementar ProductViewModel
    - Implementar loadProducts() con StateFlow
    - Implementar loadProductById() con StateFlow
    - Implementar onSearchQueryChange() con filtrado local
    - Implementar toggleLowStockFilter()
    - Implementar submitProduct() con validación
    - Implementar deleteProduct() con confirmación
    - Implementar addStock() con validación
    - Implementar subtractStock() con validación
    - Implementar validateForm() para campos requeridos y numéricos
    - Implementar retryLastOperation()
    - _Requisitos: 1.1, 1.3, 1.6, 1.7, 2.3, 2.5, 3.2, 12.1, 12.3, 12.4, 12.6, 13.5, 15.2_

  - [x] 6.3 Crear ProductListScreen composable
    - Implementar Scaffold con TopAppBar y FAB
    - Implementar SearchBar integrado
    - Implementar filtro de low stock
    - Implementar lista con LazyColumn
    - Implementar ProductItem con indicador visual de low stock
    - Implementar infinite scroll para paginación
    - Implementar estados de loading, error y éxito
    - Implementar contador de productos con low stock
    - _Requisitos: 1.1, 3.1, 3.2, 3.3, 3.4, 11.3, 13.1, 15.2, 15.6_

  - [x] 6.4 Crear ProductDetailScreen composable
    - Mostrar todos los atributos del producto
    - Mostrar información del proveedor asociado
    - Mostrar stock actual y mínimo
    - Implementar botones para editar y eliminar
    - Implementar botones para añadir/descontar stock
    - Implementar diálogos para modificación de stock
    - _Requisitos: 1.4, 2.1, 2.2, 2.4_

  - [x] 6.5 Crear ProductFormScreen composable
    - Implementar formulario con todos los campos
    - Implementar validación en tiempo real
    - Implementar selector de proveedor
    - Implementar ImagePicker integrado con preview
    - Implementar pre-población de datos en modo edición
    - Implementar botón submit con estado de carga
    - Implementar mensajes de error por campo
    - Implementar deshabilitación de submit con errores
    - _Requisitos: 1.2, 1.3, 1.5, 1.6, 10.1, 10.2, 12.1, 12.5, 12.6_

  - [ ]* 6.6 Property tests para Product
    - **Property 1: Entity List Display Completeness - Products**
    - **Valida: Requisitos 1.1**
    - **Property 2: Form Submission Triggers Correct API Request - Products**
    - **Valida: Requisitos 1.3**
    - **Property 3: Entity Detail Display Completeness - Products**
    - **Valida: Requisitos 1.4**
    - **Property 4: Edit Form Pre-population - Products**
    - **Valida: Requisitos 1.5**
    - **Property 5: Update Submission Triggers Correct API Request - Products**
    - **Valida: Requisitos 1.6**
    - **Property 6: Deletion Requires Confirmation - Products**
    - **Valida: Requisitos 1.7**
    - **Property 7: Error Response Display - Products**
    - **Valida: Requisitos 1.8, 13.3, 13.4**
    - **Property 8: Stock Modification Request**
    - **Valida: Requisitos 2.3, 2.5**
    - **Property 9: Stock Display Refresh**
    - **Valida: Requisitos 2.6**
    - **Property 10: Low Stock Visual Indication**
    - **Valida: Requisitos 3.1, 3.3**
    - **Property 11: Low Stock Filter Accuracy**
    - **Valida: Requisitos 3.2**
    - **Property 12: Low Stock Count Accuracy**
    - **Valida: Requisitos 3.4**
    - **Property 22: Required Field Validation - Products**
    - **Valida: Requisitos 12.1**
    - **Property 24: Positive Numeric Validation - Products**
    - **Valida: Requisitos 12.3**
    - **Property 25: Non-Negative Stock Validation**
    - **Valida: Requisitos 12.4**

- [x] 7. Checkpoint - Módulo Product completo
  - Verificar que todos los tests pasen
  - Verificar navegación entre pantallas de Product
  - Verificar integración con API
  - Preguntar al usuario si hay dudas o ajustes necesarios

- [x] 8. Módulo Supplier - Capa de Datos
  - [x] 8.1 Crear SupplierApi interface con Retrofit
    - Definir endpoint GET /api/supliers
    - Definir endpoint GET /api/supliers/{id}
    - Definir endpoint POST /api/supliers
    - Definir endpoint PUT /api/supliers/{id}
    - Definir endpoint DELETE /api/supliers/{id}
    - _Requisitos: 4.1, 4.3, 4.6, 4.7_

  - [x] 8.2 Implementar SupplierRepositoryImpl
    - Implementar getSuppliers() con manejo de errores
    - Implementar getSupplierById() con manejo de errores
    - Implementar createSupplier() con manejo de errores
    - Implementar updateSupplier() con manejo de errores
    - Implementar deleteSupplier() con manejo de errores
    - _Requisitos: 4.1, 4.3, 4.6, 4.7, 13.3, 13.4_

- [x] 9. Módulo Supplier - Capa de Dominio
  - [x] 9.1 Crear SupplierRepository interface
    - Definir métodos suspend con Result<T>
    - _Requisitos: 4.1, 4.3, 4.6, 4.7_

  - [x] 9.2 Crear Use Cases para Supplier
    - Implementar GetSuppliersUseCase
    - Implementar GetSupplierByIdUseCase
    - Implementar CreateSupplierUseCase
    - Implementar UpdateSupplierUseCase
    - Implementar DeleteSupplierUseCase
    - _Requisitos: 4.1, 4.3, 4.6, 4.7_

- [x] 10. Módulo Supplier - Capa de Presentación
  - [x] 10.1 Crear UI States para Supplier
    - Crear SupplierListUiState
    - Crear SupplierDetailUiState con lista de productos asociados
    - Crear SupplierFormUiState con validación de email
    - _Requisitos: 4.1, 4.4, 4.5, 12.2_

  - [x] 10.2 Implementar SupplierViewModel
    - Implementar loadSuppliers() con StateFlow
    - Implementar loadSupplierById() con productos asociados
    - Implementar onSearchQueryChange()
    - Implementar submitSupplier() con validación de email
    - Implementar deleteSupplier()
    - Implementar validateForm() con validación de email format
    - _Requisitos: 4.1, 4.3, 4.4, 4.6, 4.7, 12.2, 15.2_

  - [x] 10.3 Crear SupplierListScreen composable
    - Implementar lista con nombre, email y teléfono
    - Implementar SearchBar
    - Implementar navegación a detalle y creación
    - _Requisitos: 4.1, 15.1, 15.2_

  - [x] 10.4 Crear SupplierDetailScreen composable
    - Mostrar información completa del proveedor
    - Mostrar lista de productos asociados
    - Implementar botones para editar y eliminar
    - _Requisitos: 4.4_

  - [x] 10.5 Crear SupplierFormScreen composable
    - Implementar formulario con name, email, phone, address
    - Implementar validación de email format
    - Implementar pre-población en modo edición
    - _Requisitos: 4.2, 4.3, 4.5, 4.6, 12.2_

  - [ ]* 10.6 Property tests para Supplier
    - **Property 1: Entity List Display Completeness - Suppliers**
    - **Valida: Requisitos 4.1**
    - **Property 2: Form Submission Triggers Correct API Request - Suppliers**
    - **Valida: Requisitos 4.3**
    - **Property 3: Entity Detail Display Completeness - Suppliers**
    - **Valida: Requisitos 4.4**
    - **Property 4: Edit Form Pre-population - Suppliers**
    - **Valida: Requisitos 4.5**
    - **Property 5: Update Submission Triggers Correct API Request - Suppliers**
    - **Valida: Requisitos 4.6**
    - **Property 6: Deletion Requires Confirmation - Suppliers**
    - **Valida: Requisitos 4.7**
    - **Property 23: Email Format Validation**
    - **Valida: Requisitos 12.2**

- [x] 11. Checkpoint - Módulo Supplier completo
  - Verificar que todos los tests pasen
  - Verificar integración con módulo Product
  - Preguntar al usuario si hay dudas o ajustes necesarios

- [x] 12. Módulo Category - Capa de Datos
  - [x] 12.1 Crear DTOs para Category
    - Crear CategoryDto con @Serializable
    - _Requisitos: 5.1_

  - [x] 12.2 Crear CategoryApi interface con Retrofit
    - Definir endpoint GET /api/categories
    - Definir endpoint GET /api/categories/{id}
    - Definir endpoint POST /api/categories
    - Definir endpoint PUT /api/categories/{id}
    - Definir endpoint DELETE /api/categories/{id}
    - _Requisitos: 5.1, 5.3, 5.6, 5.7_

  - [x] 12.3 Crear CategoryMapper
    - Implementar CategoryMapper.toDomain()
    - Implementar CategoryMapper.toDto()
    - _Requisitos: 5.1, 5.4_

  - [x] 12.4 Implementar CategoryRepositoryImpl
    - Implementar getCategories() con manejo de errores
    - Implementar getCategoryById() con manejo de errores
    - Implementar createCategory() con manejo de errores
    - Implementar updateCategory() con manejo de errores
    - Implementar deleteCategory() con manejo de errores
    - _Requisitos: 5.1, 5.3, 5.6, 5.7, 13.3, 13.4_

- [x] 13. Módulo Category - Capa de Dominio
  - [x] 13.1 Crear modelos de dominio
    - Crear Category data class
    - Crear CategoryState enum
    - _Requisitos: 5.4_

  - [x] 13.2 Crear CategoryRepository interface
    - Definir métodos suspend con Result<T>
    - _Requisitos: 5.1, 5.3, 5.6, 5.7_

  - [x] 13.3 Crear Use Cases para Category
    - Implementar GetCategoriesUseCase
    - Implementar GetCategoryByIdUseCase
    - Implementar CreateCategoryUseCase
    - Implementar UpdateCategoryUseCase
    - Implementar DeleteCategoryUseCase
    - _Requisitos: 5.1, 5.3, 5.6, 5.7_

- [x] 14. Módulo Category - Capa de Presentación
  - [x] 14.1 Crear UI States para Category
    - Crear CategoryListUiState
    - Crear CategoryDetailUiState con lista de dishes/drinks asociados
    - Crear CategoryFormUiState
    - _Requisitos: 5.1, 5.4, 5.5_

  - [x] 14.2 Implementar CategoryViewModel
    - Implementar loadCategories() con StateFlow
    - Implementar loadCategoryById() con items asociados
    - Implementar onSearchQueryChange()
    - Implementar submitCategory() con validación
    - Implementar deleteCategory()
    - _Requisitos: 5.1, 5.3, 5.4, 5.6, 5.7, 15.2_

  - [x] 14.3 Crear CategoryListScreen composable
    - Implementar lista con nombre y descripción
    - Implementar SearchBar
    - Implementar navegación a detalle y creación
    - _Requisitos: 5.1, 15.1, 15.2_

  - [x] 14.4 Crear CategoryDetailScreen composable
    - Mostrar información de la categoría
    - Mostrar lista de platos o bebidas asociados
    - Implementar botones para editar y eliminar
    - _Requisitos: 5.4_

  - [x] 14.5 Crear CategoryFormScreen composable
    - Implementar formulario con name y description
    - Implementar validación de campos requeridos
    - Implementar pre-población en modo edición
    - _Requisitos: 5.2, 5.3, 5.5, 5.6_

  - [ ]* 14.6 Property tests para Category
    - **Property 1: Entity List Display Completeness - Categories**
    - **Valida: Requisitos 5.1**
    - **Property 2: Form Submission Triggers Correct API Request - Categories**
    - **Valida: Requisitos 5.3**
    - **Property 3: Entity Detail Display Completeness - Categories**
    - **Valida: Requisitos 5.4**
    - **Property 4: Edit Form Pre-population - Categories**
    - **Valida: Requisitos 5.5**
    - **Property 5: Update Submission Triggers Correct API Request - Categories**
    - **Valida: Requisitos 5.6**
    - **Property 6: Deletion Requires Confirmation - Categories**
    - **Valida: Requisitos 5.7**

- [x] 15. Checkpoint - Módulo Category completo
  - Verificar que todos los tests pasen
  - Verificar navegación entre pantallas de Category
  - Preguntar al usuario si hay dudas o ajustes necesarios

- [x] 16. Módulo Dish - Capa de Datos
  - [x] 16.1 Crear DTOs para Dish
    - Crear DishDto con @Serializable
    - Crear RecipeItemDto con @Serializable
    - _Requisitos: 6.1, 6.3_

  - [x] 16.2 Crear DishApi interface con Retrofit
    - Definir endpoint GET /api/dishes con paginación
    - Definir endpoint GET /api/dishes/{id}
    - Definir endpoint POST /api/dishes
    - Definir endpoint PUT /api/dishes/{id}
    - Definir endpoint DELETE /api/dishes/{id}
    - _Requisitos: 6.1, 6.4, 6.7, 6.8, 11.1_

  - [x] 16.3 Crear DishMapper
    - Implementar DishMapper.toDomain()
    - Implementar DishMapper.toDto()
    - Implementar RecipeItemMapper
    - _Requisitos: 6.1, 6.5_

  - [x] 16.4 Implementar DishRepositoryImpl
    - Implementar getDishes() con paginación y manejo de errores
    - Implementar getDishById() con manejo de errores
    - Implementar createDish() con manejo de errores
    - Implementar updateDish() con manejo de errores
    - Implementar deleteDish() con manejo de errores
    - _Requisitos: 6.1, 6.4, 6.7, 6.8, 11.1, 13.3, 13.4_

- [x] 17. Módulo Dish - Capa de Dominio
  - [x] 17.1 Crear modelos de dominio
    - Crear Dish data class
    - Crear RecipeItem data class
    - Crear DishState enum
    - _Requisitos: 6.5_

  - [x] 17.2 Crear DishRepository interface
    - Definir métodos suspend con Result<T> y paginación
    - _Requisitos: 6.1, 6.4, 6.7, 6.8_

  - [x] 17.3 Crear Use Cases para Dish
    - Implementar GetDishesUseCase con paginación
    - Implementar GetDishByIdUseCase
    - Implementar CreateDishUseCase con validación de receta no vacía
    - Implementar UpdateDishUseCase
    - Implementar DeleteDishUseCase
    - _Requisitos: 6.1, 6.4, 6.7, 6.8, 6.9_

- [x] 18. Módulo Dish - Capa de Presentación
  - [x] 18.1 Crear UI States para Dish
    - Crear DishListUiState con paginación y filtro por categoría
    - Crear DishDetailUiState con receta completa
    - Crear DishFormUiState con lista de ingredientes editable
    - _Requisitos: 6.1, 6.3, 6.5, 6.6, 15.5_

  - [x] 18.2 Implementar DishViewModel
    - Implementar loadDishes() con paginación y StateFlow
    - Implementar loadDishById() con receta
    - Implementar onSearchQueryChange()
    - Implementar filterByCategory()
    - Implementar submitDish() con validación de receta no vacía
    - Implementar addIngredient() y removeIngredient()
    - Implementar deleteDisH()
    - _Requisitos: 6.1, 6.4, 6.7, 6.8, 6.9, 11.2, 15.2, 15.5_

  - [x] 18.3 Crear DishListScreen composable
    - Implementar lista paginada con nombre, precio, categoría e imagen
    - Implementar infinite scroll
    - Implementar SearchBar
    - Implementar filtro por categoría
    - Implementar loading indicator durante paginación
    - _Requisitos: 6.1, 11.2, 11.3, 11.4, 15.1, 15.2, 15.5_

  - [x] 18.4 Crear DishDetailScreen composable
    - Mostrar información completa del plato
    - Mostrar receta completa con nombres de productos y cantidades
    - Implementar botones para editar y eliminar
    - _Requisitos: 6.5_

  - [x] 18.5 Crear DishFormScreen composable
    - Implementar formulario con todos los campos
    - Implementar selector de categoría
    - Implementar interfaz para añadir/quitar ingredientes a la receta
    - Implementar validación de al menos un ingrediente
    - Implementar ImagePicker con preview
    - Implementar pre-población en modo edición con receta
    - _Requisitos: 6.2, 6.3, 6.4, 6.6, 6.7, 6.9, 10.1, 10.2_

  - [ ]* 18.6 Property tests para Dish
    - **Property 1: Entity List Display Completeness - Dishes**
    - **Valida: Requisitos 6.1**
    - **Property 2: Form Submission Triggers Correct API Request - Dishes**
    - **Valida: Requisitos 6.4**
    - **Property 3: Entity Detail Display Completeness - Dishes**
    - **Valida: Requisitos 6.5**
    - **Property 4: Edit Form Pre-population - Dishes**
    - **Valida: Requisitos 6.6**
    - **Property 5: Update Submission Triggers Correct API Request - Dishes**
    - **Valida: Requisitos 6.7**
    - **Property 6: Deletion Requires Confirmation - Dishes**
    - **Valida: Requisitos 6.8**
    - **Property 19: Pagination Page Size - Dishes**
    - **Valida: Requisitos 11.1**
    - **Property 20: Next Page Request on Scroll - Dishes**
    - **Valida: Requisitos 11.3**

- [x] 19. Checkpoint - Módulo Dish completo
  - Verificar que todos los tests pasen
  - Verificar paginación funciona correctamente
  - Verificar gestión de recetas con múltiples ingredientes
  - Preguntar al usuario si hay dudas o ajustes necesarios

- [x] 20. Módulo Drink - Capa de Datos
  - [x] 20.1 Crear DTOs para Drink
    - Crear DrinkDto con @Serializable
    - _Requisitos: 7.1_

  - [x] 20.2 Crear DrinkApi interface con Retrofit
    - Definir endpoint GET /api/drinks con paginación
    - Definir endpoint GET /api/drinks/{id}
    - Definir endpoint POST /api/drinks
    - Definir endpoint PUT /api/drinks/{id}
    - Definir endpoint DELETE /api/drinks/{id}
    - _Requisitos: 7.1, 7.3, 7.6, 7.7, 11.1_

  - [x] 20.3 Crear DrinkMapper
    - Implementar DrinkMapper.toDomain()
    - Implementar DrinkMapper.toDto()
    - _Requisitos: 7.1, 7.4_

  - [x] 20.4 Implementar DrinkRepositoryImpl
    - Implementar getDrinks() con paginación y manejo de errores
    - Implementar getDrinkById() con manejo de errores
    - Implementar createDrink() con manejo de errores
    - Implementar updateDrink() con manejo de errores
    - Implementar deleteDrink() con manejo de errores
    - _Requisitos: 7.1, 7.3, 7.6, 7.7, 11.1, 13.3, 13.4_

- [x] 21. Módulo Drink - Capa de Dominio
  - [x] 21.1 Crear modelos de dominio
    - Crear Drink data class
    - Crear DrinkState enum
    - _Requisitos: 7.4_

  - [x] 21.2 Crear DrinkRepository interface
    - Definir métodos suspend con Result<T> y paginación
    - _Requisitos: 7.1, 7.3, 7.6, 7.7_

  - [x] 21.3 Crear Use Cases para Drink
    - Implementar GetDrinksUseCase con paginación
    - Implementar GetDrinkByIdUseCase
    - Implementar CreateDrinkUseCase
    - Implementar UpdateDrinkUseCase
    - Implementar DeleteDrinkUseCase
    - _Requisitos: 7.1, 7.3, 7.6, 7.7_

- [x] 22. Módulo Drink - Capa de Presentación
  - [x] 22.1 Crear UI States para Drink
    - Crear DrinkListUiState con paginación y filtro por categoría
    - Crear DrinkDetailUiState con stock units
    - Crear DrinkFormUiState
    - _Requisitos: 7.1, 7.4, 7.5, 15.5_

  - [x] 22.2 Implementar DrinkViewModel
    - Implementar loadDrinks() con paginación y StateFlow
    - Implementar loadDrinkById()
    - Implementar onSearchQueryChange()
    - Implementar filterByCategory()
    - Implementar submitDrink() con validación
    - Implementar deleteDrink()
    - _Requisitos: 7.1, 7.3, 7.4, 7.6, 7.7, 11.2, 15.2, 15.5_

  - [x] 22.3 Crear DrinkListScreen composable
    - Implementar lista paginada con nombre, precio, categoría, stock e imagen
    - Implementar infinite scroll
    - Implementar SearchBar
    - Implementar filtro por categoría
    - Implementar loading indicator durante paginación
    - _Requisitos: 7.1, 11.2, 11.3, 11.4, 15.1, 15.2, 15.5_

  - [x] 22.4 Crear DrinkDetailScreen composable
    - Mostrar información completa de la bebida
    - Mostrar stock units actual
    - Implementar botones para editar y eliminar
    - _Requisitos: 7.4_

  - [x] 22.5 Crear DrinkFormScreen composable
    - Implementar formulario con todos los campos incluyendo stockUnits
    - Implementar selector de categoría
    - Implementar ImagePicker con preview
    - Implementar pre-población en modo edición
    - _Requisitos: 7.2, 7.3, 7.5, 7.6, 10.1, 10.2_

  - [ ]* 22.6 Property tests para Drink
    - **Property 1: Entity List Display Completeness - Drinks**
    - **Valida: Requisitos 7.1**
    - **Property 2: Form Submission Triggers Correct API Request - Drinks**
    - **Valida: Requisitos 7.3**
    - **Property 3: Entity Detail Display Completeness - Drinks**
    - **Valida: Requisitos 7.4**
    - **Property 4: Edit Form Pre-population - Drinks**
    - **Valida: Requisitos 7.5**
    - **Property 5: Update Submission Triggers Correct API Request - Drinks**
    - **Valida: Requisitos 7.6**
    - **Property 6: Deletion Requires Confirmation - Drinks**
    - **Valida: Requisitos 7.7**
    - **Property 19: Pagination Page Size - Drinks**
    - **Valida: Requisitos 11.1**
    - **Property 20: Next Page Request on Scroll - Drinks**
    - **Valida: Requisitos 11.3**

- [x] 23. Checkpoint - Módulo Drink completo
  - Verificar que todos los tests pasen
  - Verificar paginación funciona correctamente
  - Preguntar al usuario si hay dudas o ajustes necesarios

- [x] 24. Módulo Addition - Capa de Datos
  - [x] 24.1 Crear DTOs para Addition
    - Crear AdditionDto con @Serializable
    - _Requisitos: 8.1_

  - [x] 24.2 Crear AdditionApi interface con Retrofit
    - Definir endpoint GET /api/additions con paginación
    - Definir endpoint GET /api/additions/{id}
    - Definir endpoint POST /api/additions
    - Definir endpoint PUT /api/additions/{id}
    - Definir endpoint DELETE /api/additions/{id}
    - _Requisitos: 8.1, 8.3, 8.6, 8.7, 11.1_

  - [x] 24.3 Crear AdditionMapper
    - Implementar AdditionMapper.toDomain()
    - Implementar AdditionMapper.toDto()
    - _Requisitos: 8.1, 8.4_

  - [x] 24.4 Implementar AdditionRepositoryImpl
    - Implementar getAdditions() con paginación y manejo de errores
    - Implementar getAdditionById() con manejo de errores
    - Implementar createAddition() con manejo de errores
    - Implementar updateAddition() con manejo de errores
    - Implementar deleteAddition() con manejo de errores
    - _Requisitos: 8.1, 8.3, 8.6, 8.7, 11.1, 13.3, 13.4_

- [x] 25. Módulo Addition - Capa de Dominio
  - [x] 25.1 Crear modelos de dominio
    - Crear Addition data class
    - Crear AdditionState enum
    - _Requisitos: 8.4_

  - [x] 25.2 Crear AdditionRepository interface
    - Definir métodos suspend con Result<T> y paginación
    - _Requisitos: 8.1, 8.3, 8.6, 8.7_

  - [x] 25.3 Crear Use Cases para Addition
    - Implementar GetAdditionsUseCase con paginación
    - Implementar GetAdditionByIdUseCase
    - Implementar CreateAdditionUseCase
    - Implementar UpdateAdditionUseCase
    - Implementar DeleteAdditionUseCase
    - _Requisitos: 8.1, 8.3, 8.6, 8.7_

- [x] 26. Módulo Addition - Capa de Presentación
  - [x] 26.1 Crear UI States para Addition
    - Crear AdditionListUiState con paginación
    - Crear AdditionDetailUiState
    - Crear AdditionFormUiState
    - _Requisitos: 8.1, 8.4, 8.5_

  - [x] 26.2 Implementar AdditionViewModel
    - Implementar loadAdditions() con paginación y StateFlow
    - Implementar loadAdditionById()
    - Implementar onSearchQueryChange()
    - Implementar submitAddition() con validación
    - Implementar deleteAddition()
    - _Requisitos: 8.1, 8.3, 8.4, 8.6, 8.7, 11.2, 15.2_

  - [x] 26.3 Crear AdditionListScreen composable
    - Implementar lista paginada con nombre, precio e imagen
    - Implementar infinite scroll
    - Implementar SearchBar
    - Implementar loading indicator durante paginación
    - _Requisitos: 8.1, 11.2, 11.3, 11.4, 15.1, 15.2_

  - [x] 26.4 Crear AdditionDetailScreen composable
    - Mostrar información completa de la adición
    - Implementar botones para editar y eliminar
    - _Requisitos: 8.4_

  - [x] 26.5 Crear AdditionFormScreen composable
    - Implementar formulario con todos los campos
    - Implementar ImagePicker con preview
    - Implementar pre-población en modo edición
    - _Requisitos: 8.2, 8.3, 8.5, 8.6, 10.1, 10.2_

  - [ ]* 26.6 Property tests para Addition
    - **Property 1: Entity List Display Completeness - Additions**
    - **Valida: Requisitos 8.1**
    - **Property 2: Form Submission Triggers Correct API Request - Additions**
    - **Valida: Requisitos 8.3**
    - **Property 3: Entity Detail Display Completeness - Additions**
    - **Valida: Requisitos 8.4**
    - **Property 4: Edit Form Pre-population - Additions**
    - **Valida: Requisitos 8.5**
    - **Property 5: Update Submission Triggers Correct API Request - Additions**
    - **Valida: Requisitos 8.6**
    - **Property 6: Deletion Requires Confirmation - Additions**
    - **Valida: Requisitos 8.7**
    - **Property 19: Pagination Page Size - Additions**
    - **Valida: Requisitos 11.1**
    - **Property 20: Next Page Request on Scroll - Additions**
    - **Valida: Requisitos 11.3**

- [x] 27. Checkpoint - Módulo Addition completo
  - Verificar que todos los tests pasen
  - Verificar paginación funciona correctamente
  - Preguntar al usuario si hay dudas o ajustes necesarios

- [x] 28. Módulo Inventory - Capa de Datos
  - [x] 28.1 Crear DTOs para InventoryMovement
    - Crear InventoryMovementDto con @Serializable
    - Crear MovementType enum
    - _Requisitos: 9.1_

  - [x] 28.2 Crear InventoryApi interface con Retrofit
    - Definir endpoint GET /api/inventory/all
    - _Requisitos: 9.2_

  - [x] 28.3 Crear InventoryMovementMapper
    - Implementar InventoryMovementMapper.toDomain()
    - Manejar conversión de timestamp a LocalDateTime
    - _Requisitos: 9.1, 9.5_

  - [x] 28.4 Implementar InventoryRepositoryImpl
    - Implementar getInventoryMovements() con manejo de errores
    - _Requisitos: 9.2, 13.3, 13.4_

- [x] 29. Módulo Inventory - Capa de Dominio
  - [x] 29.1 Crear modelos de dominio
    - Crear InventoryMovement data class
    - Crear MovementType enum (ENTRY, EXIT)
    - _Requisitos: 9.5_

  - [x] 29.2 Crear InventoryRepository interface
    - Definir método getInventoryMovements() con Result<T>
    - _Requisitos: 9.2_

  - [x] 29.3 Crear Use Cases para Inventory
    - Implementar GetInventoryMovementsUseCase
    - _Requisitos: 9.2_

- [x] 30. Módulo Inventory - Capa de Presentación
  - [x] 30.1 Crear UI States para Inventory
    - Crear InventoryMovementListUiState con filtros de fecha y tipo
    - _Requisitos: 9.1, 9.6_

  - [x] 30.2 Implementar InventoryViewModel
    - Implementar loadInventoryMovements() con StateFlow
    - Implementar sortByDateDescending()
    - Implementar filterByDateRange()
    - Implementar filterByMovementType()
    - _Requisitos: 9.2, 9.3, 9.6_

  - [x] 30.3 Crear InventoryMovementListScreen composable
    - Implementar lista con fecha, producto, tipo, cantidad y razón
    - Implementar ordenamiento por fecha descendente
    - Implementar diferenciación visual entre ENTRY y EXIT
    - Implementar filtros por rango de fechas y tipo de movimiento
    - Implementar indicador visual de filtros activos
    - _Requisitos: 9.1, 9.3, 9.4, 9.6, 15.6_

  - [x] 30.4 Crear InventoryMovementDetailScreen composable
    - Mostrar información completa del movimiento
    - Mostrar timestamp, producto, cantidad, tipo y razón
    - _Requisitos: 9.5_

  - [ ]* 30.5 Property tests para Inventory
    - **Property 13: Inventory Movement Sorting**
    - **Valida: Requisitos 9.3**
    - **Property 14: Movement Type Visual Differentiation**
    - **Valida: Requisitos 9.4**
    - **Property 15: Movement Filter Accuracy**
    - **Valida: Requisitos 9.6**
    - **Property 35: Active Filter Visual Indication - Inventory**
    - **Valida: Requisitos 15.6**

- [x] 31. Checkpoint - Módulo Inventory completo
  - Verificar que todos los tests pasen
  - Verificar filtros funcionan correctamente
  - Preguntar al usuario si hay dudas o ajustes necesarios

- [x] 32. Implementar funcionalidades transversales de imágenes
  - [x] 32.1 Implementar carga de imágenes
    - Configurar permisos de cámara y galería en AndroidManifest
    - Implementar ImagePicker con Coil para preview
    - Implementar upload de imagen al backend
    - Implementar manejo de respuesta con URL de Cloudinary
    - _Requisitos: 10.1, 10.2, 10.3, 10.4_

  - [x] 32.2 Implementar visualización de imágenes
    - Implementar AsyncImage con Coil en todas las pantallas
    - Implementar placeholder y error images
    - Implementar caché de imágenes
    - _Requisitos: 10.5_

  - [x] 32.3 Implementar manejo de errores de imágenes
    - Implementar retry en caso de fallo de upload
    - Implementar mensaje de error específico para imágenes
    - _Requisitos: 10.6_

  - [ ]* 32.4 Property tests para imágenes
    - **Property 16: Image Preview Display**
    - **Valida: Requisitos 10.2**
    - **Property 17: Image Upload on Form Submission**
    - **Valida: Requisitos 10.3**
    - **Property 18: Image URL Storage and Display**
    - **Valida: Requisitos 10.4, 10.5**

- [x] 33. Implementar funcionalidades transversales de validación
  - [x] 33.1 Crear ValidationUtils
    - Implementar validateRequired()
    - Implementar validateEmail()
    - Implementar validatePositiveNumber()
    - Implementar validateNonNegativeNumber()
    - _Requisitos: 12.1, 12.2, 12.3, 12.4_

  - [x] 33.2 Integrar validación en todos los ViewModels
    - Aplicar validación en tiempo real en todos los formularios
    - Implementar deshabilitación de submit con errores
    - Implementar display de errores por campo
    - _Requisitos: 12.5, 12.6_

  - [ ]* 33.3 Property tests para validación
    - **Property 22: Required Field Validation**
    - **Valida: Requisitos 12.1**
    - **Property 23: Email Format Validation**
    - **Valida: Requisitos 12.2**
    - **Property 24: Positive Numeric Validation**
    - **Valida: Requisitos 12.3**
    - **Property 25: Non-Negative Stock Validation**
    - **Valida: Requisitos 12.4**
    - **Property 26: Validation Error Display**
    - **Valida: Requisitos 12.5**
    - **Property 27: Submit Button State**
    - **Valida: Requisitos 12.6**

- [x] 34. Implementar funcionalidades transversales de estados y errores
  - [x] 34.1 Implementar manejo de estados de conexión
    - Implementar loading indicators en todas las operaciones
    - Implementar success messages con Snackbar
    - Implementar error messages con retry option
    - Implementar detección de red no disponible
    - _Requisitos: 13.1, 13.2, 13.3, 13.4, 13.5_

  - [x] 34.2 Configurar timeout de requests
    - Verificar configuración de OkHttpClient con timeout de 30 segundos
    - Implementar manejo de TimeoutException
    - _Requisitos: 13.6_

  - [ ]* 34.3 Property tests para estados y errores
    - **Property 21: Loading Indicator During Requests**
    - **Valida: Requisitos 11.4, 13.1**
    - **Property 28: Success Message Display**
    - **Valida: Requisitos 13.2**
    - **Property 29: Retry Option on Network Error**
    - **Valida: Requisitos 13.5**
    - **Property 30: Request Timeout**
    - **Valida: Requisitos 13.6**

- [x] 35. Implementar funcionalidades transversales de navegación
  - [x] 35.1 Completar NavGraph con todas las rutas
    - Agregar rutas para todas las pantallas de todos los módulos
    - Configurar argumentos de navegación
    - Implementar deep links si es necesario
    - _Requisitos: 14.1, 14.2, 14.3_

  - [x] 35.2 Implementar preservación de estado de navegación
    - Configurar SavedStateHandle en ViewModels
    - Implementar restauración de estado al volver de background
    - _Requisitos: 14.5_

  - [ ]* 35.3 Property tests para navegación
    - **Property 31: Navigation on Section Selection**
    - **Valida: Requisitos 14.2**
    - **Property 32: Navigation on Item Selection**
    - **Valida: Requisitos 14.3**
    - **Property 33: Navigation State Preservation**
    - **Valida: Requisitos 14.5**

- [x] 36. Implementar funcionalidades transversales de búsqueda y filtrado
  - [x] 36.1 Implementar búsqueda en tiempo real
    - Verificar implementación de SearchBar en todos los listados
    - Implementar filtrado local por nombre
    - Implementar debounce para búsqueda eficiente
    - _Requisitos: 15.1, 15.2, 15.3_

  - [x] 36.2 Implementar filtros específicos
    - Implementar filtro por proveedor en Products
    - Implementar filtro por low stock en Products
    - Implementar filtro por categoría en Dishes y Drinks
    - Implementar filtro por rango de fechas en Inventory
    - Implementar filtro por tipo de movimiento en Inventory
    - _Requisitos: 15.4, 15.5, 9.6_

  - [x] 36.3 Implementar indicadores visuales de filtros
    - Implementar chips o badges mostrando filtros activos
    - Implementar botón para limpiar filtros
    - _Requisitos: 15.6_

  - [ ]* 36.4 Property tests para búsqueda y filtrado
    - **Property 34: Search Filter Real-time Update**
    - **Valida: Requisitos 15.2, 15.3**
    - **Property 35: Active Filter Visual Indication**
    - **Valida: Requisitos 15.6**

- [x] 37. Checkpoint final - Integración completa
  - Ejecutar todos los tests (unit + property-based)
  - Verificar navegación entre todos los módulos
  - Verificar manejo de errores en todos los flujos
  - Verificar paginación en listados largos
  - Verificar validaciones en todos los formularios
  - Verificar carga y visualización de imágenes
  - Verificar búsqueda y filtros en todos los listados
  - Preguntar al usuario si hay ajustes finales necesarios

## Notas

- Las tareas marcadas con `*` son opcionales (property-based tests) y pueden omitirse para un MVP más rápido
- Cada tarea referencia los requisitos específicos que valida
- Los checkpoints permiten validación incremental con el usuario
- Los property tests validan propiedades universales de corrección
- La implementación sigue el orden: Data → Domain → Presentation para cada módulo
- Se implementa un módulo completo antes de pasar al siguiente para validar funcionalidad end-to-end
