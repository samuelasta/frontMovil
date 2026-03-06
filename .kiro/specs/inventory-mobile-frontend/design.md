# Design Document: Inventory Mobile Frontend

## Overview

Este documento describe el diseño técnico del frontend móvil Android para el sistema de inventario de Smart Restaurant. La aplicación es nativa Android construida con Kotlin y Jetpack Compose, siguiendo arquitectura MVVM y Clean Architecture, que consume una API REST en Spring Boot.

### Objetivos del Diseño

- Proporcionar una interfaz móvil nativa y fluida para gestión de inventario
- Implementar arquitectura escalable y mantenible siguiendo principios SOLID
- Garantizar experiencia de usuario consistente con Material Design 3
- Manejar estados de carga, error y éxito de forma clara
- Optimizar rendimiento con paginación y caché local
- Validar datos en cliente antes de enviar al servidor

### Stack Tecnológico

- **Lenguaje**: Kotlin
- **UI Framework**: Jetpack Compose con Material Design 3
- **Arquitectura**: MVVM + Clean Architecture
- **Networking**: Retrofit + OkHttp
- **Serialización**: Kotlinx Serialization
- **Imágenes**: Coil
- **Inyección de Dependencias**: Hilt/Dagger
- **Navegación**: Navigation Compose
- **Asincronía**: Kotlin Coroutines + Flow
- **Persistencia Local**: Room (opcional para caché)
- **Testing**: JUnit, MockK, Turbine (para Flow testing)

## Architecture

### Clean Architecture Layers

La aplicación sigue Clean Architecture con tres capas principales:

```
┌─────────────────────────────────────────┐
│         Presentation Layer (UI)         │
│  - Composables                          │
│  - ViewModels                           │
│  - UI State                             │
└─────────────────┬───────────────────────┘
                  │
┌─────────────────▼───────────────────────┐
│          Domain Layer                   │
│  - Use Cases                            │
│  - Domain Models                        │
│  - Repository Interfaces                │
└─────────────────┬───────────────────────┘
                  │
┌─────────────────▼───────────────────────┐
│           Data Layer                    │
│  - Repository Implementations           │
│  - API Service (Retrofit)               │
│  - DTOs (Data Transfer Objects)         │
│  - Mappers                              │
│  - Local Cache (Room - opcional)        │
└─────────────────────────────────────────┘
```

### MVVM Pattern

Cada pantalla sigue el patrón MVVM:

- **View (Composable)**: Renderiza UI y captura eventos del usuario
- **ViewModel**: Maneja lógica de presentación y estado de UI
- **Model**: Datos del dominio y lógica de negocio

### Flujo de Datos

```
User Action → Composable → ViewModel → Use Case → Repository → API Service
                ↑                                                    ↓
                └────────────── StateFlow ←──────────────────────────┘
```

### Estructura de Paquetes

```
com.smartrestaurant.inventory/
├── di/                          # Dependency Injection modules
│   ├── NetworkModule.kt
│   ├── RepositoryModule.kt
│   └── UseCaseModule.kt
├── data/
│   ├── remote/
│   │   ├── api/
│   │   │   ├── ProductApi.kt
│   │   │   ├── SupplierApi.kt
│   │   │   ├── CategoryApi.kt
│   │   │   ├── DishApi.kt
│   │   │   ├── DrinkApi.kt
│   │   │   ├── AdditionApi.kt
│   │   │   └── InventoryApi.kt
│   │   ├── dto/
│   │   │   ├── ApiResponse.kt
│   │   │   ├── ProductDto.kt
│   │   │   ├── SupplierDto.kt
│   │   │   ├── CategoryDto.kt
│   │   │   ├── DishDto.kt
│   │   │   ├── DrinkDto.kt
│   │   │   ├── AdditionDto.kt
│   │   │   └── InventoryMovementDto.kt
│   │   └── interceptor/
│   │       └── AuthInterceptor.kt
│   ├── local/                   # Optional cache layer
│   │   ├── dao/
│   │   └── entity/
│   ├── repository/
│   │   ├── ProductRepositoryImpl.kt
│   │   ├── SupplierRepositoryImpl.kt
│   │   ├── CategoryRepositoryImpl.kt
│   │   ├── DishRepositoryImpl.kt
│   │   ├── DrinkRepositoryImpl.kt
│   │   ├── AdditionRepositoryImpl.kt
│   │   └── InventoryRepositoryImpl.kt
│   └── mapper/
│       ├── ProductMapper.kt
│       ├── SupplierMapper.kt
│       └── ...
├── domain/
│   ├── model/
│   │   ├── Product.kt
│   │   ├── Supplier.kt
│   │   ├── Category.kt
│   │   ├── Dish.kt
│   │   ├── Drink.kt
│   │   ├── Addition.kt
│   │   ├── InventoryMovement.kt
│   │   └── Recipe.kt
│   ├── repository/
│   │   ├── ProductRepository.kt
│   │   ├── SupplierRepository.kt
│   │   └── ...
│   └── usecase/
│       ├── product/
│       │   ├── GetProductsUseCase.kt
│       │   ├── GetProductByIdUseCase.kt
│       │   ├── CreateProductUseCase.kt
│       │   ├── UpdateProductUseCase.kt
│       │   ├── DeleteProductUseCase.kt
│       │   ├── AddStockUseCase.kt
│       │   └── SubtractStockUseCase.kt
│       ├── supplier/
│       ├── category/
│       ├── dish/
│       ├── drink/
│       ├── addition/
│       └── inventory/
└── presentation/
    ├── navigation/
    │   ├── NavGraph.kt
    │   └── Screen.kt
    ├── theme/
    │   ├── Color.kt
    │   ├── Theme.kt
    │   └── Type.kt
    ├── common/
    │   ├── components/
    │   │   ├── LoadingIndicator.kt
    │   │   ├── ErrorMessage.kt
    │   │   ├── SearchBar.kt
    │   │   ├── ImagePicker.kt
    │   │   └── ConfirmationDialog.kt
    │   └── util/
    │       ├── UiState.kt
    │       └── ValidationUtils.kt
    ├── product/
    │   ├── ProductListScreen.kt
    │   ├── ProductDetailScreen.kt
    │   ├── ProductFormScreen.kt
    │   ├── ProductViewModel.kt
    │   └── ProductUiState.kt
    ├── supplier/
    │   ├── SupplierListScreen.kt
    │   ├── SupplierDetailScreen.kt
    │   ├── SupplierFormScreen.kt
    │   ├── SupplierViewModel.kt
    │   └── SupplierUiState.kt
    ├── category/
    ├── dish/
    ├── drink/
    ├── addition/
    └── inventory/
```

## Components and Interfaces

### Data Layer Components

#### API Services (Retrofit)

Cada servicio define endpoints para una entidad:

```kotlin
interface ProductApi {
    @GET("api/products")
    suspend fun getProducts(@Query("page") page: Int): ApiResponse<List<ProductDto>>
    
    @GET("api/products/{id}")
    suspend fun getProductById(@Path("id") id: Long): ApiResponse<ProductDto>
    
    @POST("api/products")
    suspend fun createProduct(@Body product: ProductDto): ApiResponse<ProductDto>
    
    @PUT("api/products/{id}")
    suspend fun updateProduct(@Path("id") id: Long, @Body product: ProductDto): ApiResponse<ProductDto>
    
    @DELETE("api/products/{id}")
    suspend fun deleteProduct(@Path("id") id: Long): ApiResponse<Unit>
    
    @PUT("api/products/add-stock/{id}")
    suspend fun addStock(@Path("id") id: Long, @Body request: StockRequest): ApiResponse<ProductDto>
    
    @PUT("api/products/subtract-stock/{id}")
    suspend fun subtractStock(@Path("id") id: Long, @Body request: StockRequest): ApiResponse<ProductDto>
}
```

#### DTOs (Data Transfer Objects)

Representan la estructura JSON del backend:

```kotlin
@Serializable
data class ApiResponse<T>(
    val message: T,
    val error: Boolean
)

@Serializable
data class ProductDto(
    val id: Long? = null,
    val name: String,
    val description: String,
    val weight: Double,
    val unit: String,
    val price: Double,
    val stock: Double,
    val minimumStock: Double,
    val imageUrl: String? = null,
    val state: String = "ACTIVE",
    val suplier: SupplierDto
)

@Serializable
data class SupplierDto(
    val id: Long? = null,
    val name: String,
    val email: String,
    val phone: String,
    val address: String,
    val state: String = "ACTIVE"
)

@Serializable
data class DishDto(
    val id: Long? = null,
    val name: String,
    val description: String,
    val price: Double,
    val preparationTime: Int,
    val imageUrl: String? = null,
    val state: String = "ACTIVE",
    val category: CategoryDto,
    val recipe: List<RecipeItemDto>
)

@Serializable
data class RecipeItemDto(
    val product: ProductDto,
    val quantity: Double
)
```

#### Repository Implementations

Implementan interfaces del dominio y manejan llamadas API:

```kotlin
class ProductRepositoryImpl @Inject constructor(
    private val api: ProductApi,
    private val mapper: ProductMapper
) : ProductRepository {
    
    override suspend fun getProducts(page: Int): Result<List<Product>> = try {
        val response = api.getProducts(page)
        if (response.error) {
            Result.failure(Exception(response.message.toString()))
        } else {
            Result.success(response.message.map { mapper.toDomain(it) })
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
    
    override suspend fun getProductById(id: Long): Result<Product> = try {
        val response = api.getProductById(id)
        if (response.error) {
            Result.failure(Exception(response.message.toString()))
        } else {
            Result.success(mapper.toDomain(response.message))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
    
    // ... otros métodos
}
```

### Domain Layer Components

#### Domain Models

Representan entidades del negocio sin dependencias externas:

```kotlin
data class Product(
    val id: Long?,
    val name: String,
    val description: String,
    val weight: Double,
    val unit: String,
    val price: Double,
    val stock: Double,
    val minimumStock: Double,
    val imageUrl: String?,
    val state: ProductState,
    val supplier: Supplier
) {
    val isLowStock: Boolean
        get() = stock < minimumStock
}

enum class ProductState {
    ACTIVE, INACTIVE
}

data class Supplier(
    val id: Long?,
    val name: String,
    val email: String,
    val phone: String,
    val address: String,
    val state: SupplierState
)

data class Dish(
    val id: Long?,
    val name: String,
    val description: String,
    val price: Double,
    val preparationTime: Int,
    val imageUrl: String?,
    val state: DishState,
    val category: Category,
    val recipe: List<RecipeItem>
)

data class RecipeItem(
    val product: Product,
    val quantity: Double
)
```

#### Repository Interfaces

Definen contratos para acceso a datos:

```kotlin
interface ProductRepository {
    suspend fun getProducts(page: Int): Result<List<Product>>
    suspend fun getProductById(id: Long): Result<Product>
    suspend fun createProduct(product: Product): Result<Product>
    suspend fun updateProduct(id: Long, product: Product): Result<Product>
    suspend fun deleteProduct(id: Long): Result<Unit>
    suspend fun addStock(id: Long, quantity: Double, reason: String?): Result<Product>
    suspend fun subtractStock(id: Long, quantity: Double, reason: String?): Result<Product>
}
```

#### Use Cases

Encapsulan lógica de negocio específica:

```kotlin
class GetProductsUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    suspend operator fun invoke(page: Int): Result<List<Product>> {
        return repository.getProducts(page)
    }
}

class AddStockUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    suspend operator fun invoke(
        productId: Long,
        quantity: Double,
        reason: String?
    ): Result<Product> {
        if (quantity <= 0) {
            return Result.failure(IllegalArgumentException("Quantity must be positive"))
        }
        return repository.addStock(productId, quantity, reason)
    }
}
```

### Presentation Layer Components

#### UI State

Representa el estado de la UI de forma inmutable:

```kotlin
sealed interface UiState<out T> {
    object Idle : UiState<Nothing>
    object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}

data class ProductListUiState(
    val products: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val selectedSupplier: Supplier? = null,
    val showLowStockOnly: Boolean = false,
    val currentPage: Int = 0,
    val hasMorePages: Boolean = true
)

data class ProductFormUiState(
    val product: Product? = null,
    val name: String = "",
    val description: String = "",
    val weight: String = "",
    val unit: String = "",
    val price: String = "",
    val minimumStock: String = "",
    val selectedSupplier: Supplier? = null,
    val imageUri: String? = null,
    val nameError: String? = null,
    val weightError: String? = null,
    val priceError: String? = null,
    val supplierError: String? = null,
    val isSubmitting: Boolean = false,
    val submitError: String? = null,
    val submitSuccess: Boolean = false
)
```

#### ViewModels

Manejan lógica de presentación y estado:

```kotlin
@HiltViewModel
class ProductViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase,
    private val getProductByIdUseCase: GetProductByIdUseCase,
    private val createProductUseCase: CreateProductUseCase,
    private val updateProductUseCase: UpdateProductUseCase,
    private val deleteProductUseCase: DeleteProductUseCase,
    private val addStockUseCase: AddStockUseCase,
    private val subtractStockUseCase: SubtractStockUseCase
) : ViewModel() {
    
    private val _listState = MutableStateFlow(ProductListUiState())
    val listState: StateFlow<ProductListUiState> = _listState.asStateFlow()
    
    private val _formState = MutableStateFlow(ProductFormUiState())
    val formState: StateFlow<ProductFormUiState> = _formState.asStateFlow()
    
    fun loadProducts(page: Int = 0) {
        viewModelScope.launch {
            _listState.update { it.copy(isLoading = true, error = null) }
            
            getProductsUseCase(page).fold(
                onSuccess = { products ->
                    _listState.update {
                        it.copy(
                            products = if (page == 0) products else it.products + products,
                            isLoading = false,
                            currentPage = page,
                            hasMorePages = products.size == 10
                        )
                    }
                },
                onFailure = { error ->
                    _listState.update {
                        it.copy(isLoading = false, error = error.message)
                    }
                }
            )
        }
    }
    
    fun onSearchQueryChange(query: String) {
        _listState.update { it.copy(searchQuery = query) }
        // Filtrar localmente o hacer nueva petición
    }
    
    fun submitProduct() {
        viewModelScope.launch {
            if (!validateForm()) return@launch
            
            _formState.update { it.copy(isSubmitting = true, submitError = null) }
            
            val product = buildProductFromForm()
            val result = if (formState.value.product == null) {
                createProductUseCase(product)
            } else {
                updateProductUseCase(formState.value.product!!.id!!, product)
            }
            
            result.fold(
                onSuccess = {
                    _formState.update {
                        it.copy(isSubmitting = false, submitSuccess = true)
                    }
                },
                onFailure = { error ->
                    _formState.update {
                        it.copy(isSubmitting = false, submitError = error.message)
                    }
                }
            )
        }
    }
    
    private fun validateForm(): Boolean {
        // Validación de campos
        return true
    }
}
```

#### Composables (Screens)

Definen la UI declarativa:

```kotlin
@Composable
fun ProductListScreen(
    viewModel: ProductViewModel = hiltViewModel(),
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToCreate: () -> Unit
) {
    val state by viewModel.listState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Products") },
                actions = {
                    IconButton(onClick = { /* Filter */ }) {
                        Icon(Icons.Default.FilterList, "Filter")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToCreate) {
                Icon(Icons.Default.Add, "Add Product")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            SearchBar(
                query = state.searchQuery,
                onQueryChange = viewModel::onSearchQueryChange
            )
            
            when {
                state.isLoading && state.products.isEmpty() -> {
                    LoadingIndicator()
                }
                state.error != null && state.products.isEmpty() -> {
                    ErrorMessage(
                        message = state.error!!,
                        onRetry = { viewModel.loadProducts() }
                    )
                }
                else -> {
                    ProductList(
                        products = state.filteredProducts,
                        onProductClick = onNavigateToDetail,
                        onLoadMore = { viewModel.loadProducts(state.currentPage + 1) },
                        hasMorePages = state.hasMorePages
                    )
                }
            }
        }
    }
}

@Composable
fun ProductFormScreen(
    productId: Long? = null,
    viewModel: ProductViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.formState.collectAsState()
    
    LaunchedEffect(productId) {
        if (productId != null) {
            viewModel.loadProductForEdit(productId)
        }
    }
    
    LaunchedEffect(state.submitSuccess) {
        if (state.submitSuccess) {
            onNavigateBack()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (productId == null) "New Product" else "Edit Product") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = state.name,
                onValueChange = viewModel::onNameChange,
                label = { Text("Name") },
                isError = state.nameError != null,
                supportingText = state.nameError?.let { { Text(it) } },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // ... más campos
            
            ImagePicker(
                imageUri = state.imageUri,
                onImageSelected = viewModel::onImageSelected
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = viewModel::submitProduct,
                enabled = !state.isSubmitting,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (state.isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(if (productId == null) "Create" else "Update")
                }
            }
            
            state.submitError?.let { error ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
```

### Navigation

```kotlin
sealed class Screen(val route: String) {
    object ProductList : Screen("products")
    object ProductDetail : Screen("products/{id}") {
        fun createRoute(id: Long) = "products/$id"
    }
    object ProductForm : Screen("products/form?id={id}") {
        fun createRoute(id: Long? = null) = "products/form${id?.let { "?id=$it" } ?: ""}"
    }
    // ... otras pantallas
}

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.ProductList.route
    ) {
        composable(Screen.ProductList.route) {
            ProductListScreen(
                onNavigateToDetail = { id ->
                    navController.navigate(Screen.ProductDetail.createRoute(id))
                },
                onNavigateToCreate = {
                    navController.navigate(Screen.ProductForm.createRoute())
                }
            )
        }
        
        composable(
            route = Screen.ProductDetail.route,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("id")!!
            ProductDetailScreen(
                productId = id,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEdit = { navController.navigate(Screen.ProductForm.createRoute(id)) }
            )
        }
        
        // ... otras rutas
    }
}
```

## Data Models

### Domain Models

```kotlin
// Product
data class Product(
    val id: Long?,
    val name: String,
    val description: String,
    val weight: Double,
    val unit: String,
    val price: Double,
    val stock: Double,
    val minimumStock: Double,
    val imageUrl: String?,
    val state: ProductState,
    val supplier: Supplier
) {
    val isLowStock: Boolean get() = stock < minimumStock
}

enum class ProductState { ACTIVE, INACTIVE }

// Supplier
data class Supplier(
    val id: Long?,
    val name: String,
    val email: String,
    val phone: String,
    val address: String,
    val state: SupplierState
)

enum class SupplierState { ACTIVE, INACTIVE }

// Category
data class Category(
    val id: Long?,
    val name: String,
    val description: String,
    val state: CategoryState
)

enum class CategoryState { ACTIVE, INACTIVE }

// Dish
data class Dish(
    val id: Long?,
    val name: String,
    val description: String,
    val price: Double,
    val preparationTime: Int,
    val imageUrl: String?,
    val state: DishState,
    val category: Category,
    val recipe: List<RecipeItem>
)

data class RecipeItem(
    val product: Product,
    val quantity: Double
)

enum class DishState { ACTIVE, INACTIVE }

// Drink
data class Drink(
    val id: Long?,
    val name: String,
    val description: String,
    val price: Double,
    val stockUnits: Int,
    val imageUrl: String?,
    val state: DrinkState,
    val category: Category
)

enum class DrinkState { ACTIVE, INACTIVE }

// Addition
data class Addition(
    val id: Long?,
    val name: String,
    val description: String,
    val price: Double,
    val imageUrl: String?,
    val state: AdditionState
)

enum class AdditionState { ACTIVE, INACTIVE }

// Inventory Movement
data class InventoryMovement(
    val id: Long?,
    val product: Product,
    val movementType: MovementType,
    val quantity: Double,
    val reason: String?,
    val timestamp: LocalDateTime
)

enum class MovementType { ENTRY, EXIT }
```

### Mappers

```kotlin
class ProductMapper @Inject constructor(
    private val supplierMapper: SupplierMapper
) {
    fun toDomain(dto: ProductDto): Product = Product(
        id = dto.id,
        name = dto.name,
        description = dto.description,
        weight = dto.weight,
        unit = dto.unit,
        price = dto.price,
        stock = dto.stock,
        minimumStock = dto.minimumStock,
        imageUrl = dto.imageUrl,
        state = ProductState.valueOf(dto.state),
        supplier = supplierMapper.toDomain(dto.suplier)
    )
    
    fun toDto(domain: Product): ProductDto = ProductDto(
        id = domain.id,
        name = domain.name,
        description = domain.description,
        weight = domain.weight,
        unit = domain.unit,
        price = domain.price,
        stock = domain.stock,
        minimumStock = domain.minimumStock,
        imageUrl = domain.imageUrl,
        state = domain.state.name,
        suplier = supplierMapper.toDto(domain.supplier)
    )
}
```


## Correctness Properties

*A property is a characteristic or behavior that should hold true across all valid executions of a system-essentially, a formal statement about what the system should do. Properties serve as the bridge between human-readable specifications and machine-verifiable correctness guarantees.*

### Property 1: Entity List Display Completeness

*For any* list of entities (products, suppliers, categories, dishes, drinks, additions) returned from the API, the rendered UI SHALL display all required fields for that entity type as specified in the requirements.

**Validates: Requirements 1.1, 4.1, 5.1, 6.1, 7.1, 8.1, 9.1**

### Property 2: Form Submission Triggers Correct API Request

*For any* valid entity data submitted through a creation form, the app SHALL send a POST request to the correct endpoint with the complete entity data.

**Validates: Requirements 1.3, 4.3, 5.3, 6.4, 7.3, 8.3**

### Property 3: Entity Detail Display Completeness

*For any* entity selected by the user, the detail view SHALL display all attributes of that entity including related entities (e.g., product with supplier, dish with recipe).

**Validates: Requirements 1.4, 4.4, 5.4, 6.5, 7.4, 8.4, 9.5**

### Property 4: Edit Form Pre-population

*For any* entity loaded for editing, the form SHALL be pre-populated with all current values of that entity.

**Validates: Requirements 1.5, 4.5, 5.5, 6.6, 7.5, 8.5**

### Property 5: Update Submission Triggers Correct API Request

*For any* valid updated entity data submitted through an edit form, the app SHALL send a PUT request to the correct endpoint with the entity ID and updated data.

**Validates: Requirements 1.6, 4.6, 5.6, 6.7, 7.6, 8.6**

### Property 6: Deletion Requires Confirmation

*For any* entity deletion request, the app SHALL display a confirmation dialog before sending the DELETE request to the API.

**Validates: Requirements 1.7, 4.7, 5.7, 6.8, 7.7, 8.7**

### Property 7: Error Response Display

*For any* API response with `error: true` or network failure, the app SHALL display an error message to the user.

**Validates: Requirements 1.8, 13.3, 13.4**

### Property 8: Stock Modification Request

*For any* valid stock quantity (positive for add, positive for subtract), submitting a stock modification SHALL send a PUT request to the appropriate endpoint (`/add-stock` or `/subtract-stock`) with the product ID and quantity.

**Validates: Requirements 2.3, 2.5**

### Property 9: Stock Display Refresh

*For any* successful stock modification response, the displayed product stock SHALL be updated to reflect the new stock value returned by the API.

**Validates: Requirements 2.6**

### Property 10: Low Stock Visual Indication

*For any* product where current stock is less than minimum stock, the UI SHALL display a visual indicator (warning color or icon) on that product.

**Validates: Requirements 3.1, 3.3**

### Property 11: Low Stock Filter Accuracy

*For any* product list with low stock filter applied, all displayed products SHALL have stock less than their minimum stock threshold.

**Validates: Requirements 3.2**

### Property 12: Low Stock Count Accuracy

*For any* product list, the displayed low stock count SHALL equal the number of products where stock is less than minimum stock.

**Validates: Requirements 3.4**

### Property 13: Inventory Movement Sorting

*For any* list of inventory movements, the movements SHALL be sorted by timestamp in descending order (most recent first).

**Validates: Requirements 9.3**

### Property 14: Movement Type Visual Differentiation

*For any* inventory movement displayed, the UI SHALL apply different visual treatment based on whether the movement type is ENTRY or EXIT.

**Validates: Requirements 9.4**

### Property 15: Movement Filter Accuracy

*For any* inventory movement list with filters applied (date range or movement type), all displayed movements SHALL match the filter criteria.

**Validates: Requirements 9.6**

### Property 16: Image Preview Display

*For any* image selected by the user (from camera or gallery), the app SHALL display a preview of that image before form submission.

**Validates: Requirements 10.2**

### Property 17: Image Upload on Form Submission

*For any* form submission that includes an image, the app SHALL upload the image to the Backend API as part of the submission process.

**Validates: Requirements 10.3**

### Property 18: Image URL Storage and Display

*For any* successful image upload response containing a Cloudinary URL, the app SHALL store that URL and display the image in the UI.

**Validates: Requirements 10.4, 10.5**

### Property 19: Pagination Page Size

*For any* paginated API request (products, dishes, drinks, additions), the request SHALL specify a page size of 10 elements.

**Validates: Requirements 11.1**

### Property 20: Next Page Request on Scroll

*For any* paginated list where the user scrolls to the end and more pages exist, the app SHALL automatically request the next page.

**Validates: Requirements 11.3**

### Property 21: Loading Indicator During Requests

*For any* API request in progress, the app SHALL display a loading indicator until the response is received or the request fails.

**Validates: Requirements 11.4, 13.1**

### Property 22: Required Field Validation

*For any* form with required fields, the app SHALL prevent submission and display validation errors when any required field is empty.

**Validates: Requirements 12.1**

### Property 23: Email Format Validation

*For any* supplier form, if the email field contains a value that doesn't match email format, the app SHALL display a validation error and prevent submission.

**Validates: Requirements 12.2**

### Property 24: Positive Numeric Validation

*For any* numeric field for prices, weights, or quantities, if the value is zero or negative, the app SHALL display a validation error and prevent submission.

**Validates: Requirements 12.3**

### Property 25: Non-Negative Stock Validation

*For any* stock quantity field, if the value is negative, the app SHALL display a validation error and prevent submission.

**Validates: Requirements 12.4**

### Property 26: Validation Error Display

*For any* field with a validation error, the app SHALL display an error message adjacent to or below that field.

**Validates: Requirements 12.5**

### Property 27: Submit Button State

*For any* form with validation errors, the submit button SHALL be disabled until all validations pass.

**Validates: Requirements 12.6**

### Property 28: Success Message Display

*For any* successful API response (create, update, delete), the app SHALL display a success message or confirmation to the user.

**Validates: Requirements 13.2**

### Property 29: Retry Option on Network Error

*For any* network error or failed API request, the app SHALL provide a retry option to the user.

**Validates: Requirements 13.5**

### Property 30: Request Timeout

*For any* API request that exceeds 30 seconds without response, the app SHALL timeout the request and display an error message.

**Validates: Requirements 13.6**

### Property 31: Navigation on Section Selection

*For any* section selected from the navigation menu, the app SHALL navigate to the corresponding list view for that section.

**Validates: Requirements 14.2**

### Property 32: Navigation on Item Selection

*For any* item selected from a list view, the app SHALL navigate to the detail view for that specific item.

**Validates: Requirements 14.3**

### Property 33: Navigation State Preservation

*For any* app backgrounding and restoration, the navigation state (current screen and back stack) SHALL be preserved.

**Validates: Requirements 14.5**

### Property 34: Search Filter Real-time Update

*For any* search query entered by the user, the displayed list SHALL update in real-time to show only items whose names contain the search query.

**Validates: Requirements 15.2, 15.3**

### Property 35: Active Filter Visual Indication

*For any* filter applied to a list (supplier, category, low stock, date range, movement type), the app SHALL display a visual indicator showing which filters are currently active.

**Validates: Requirements 15.6**

## Error Handling

### Error Categories

La aplicación maneja cuatro categorías principales de errores:

1. **Network Errors**: Fallos de conectividad, timeouts
2. **API Errors**: Respuestas del backend con `error: true`
3. **Validation Errors**: Errores de validación client-side
4. **System Errors**: Errores inesperados de la aplicación

### Error Handling Strategy

```kotlin
sealed class AppError {
    data class NetworkError(val message: String) : AppError()
    data class ApiError(val message: String, val code: Int? = null) : AppError()
    data class ValidationError(val field: String, val message: String) : AppError()
    data class SystemError(val throwable: Throwable) : AppError()
}

// En Repository
suspend fun getProducts(page: Int): Result<List<Product>> = try {
    val response = api.getProducts(page)
    if (response.error) {
        Result.failure(AppError.ApiError(response.message.toString()))
    } else {
        Result.success(response.message.map { mapper.toDomain(it) })
    }
} catch (e: IOException) {
    Result.failure(AppError.NetworkError("Network connection failed"))
} catch (e: HttpException) {
    Result.failure(AppError.ApiError("Server error: ${e.code()}", e.code()))
} catch (e: Exception) {
    Result.failure(AppError.SystemError(e))
}

// En ViewModel
fun loadProducts() {
    viewModelScope.launch {
        _state.update { it.copy(isLoading = true, error = null) }
        
        getProductsUseCase(currentPage).fold(
            onSuccess = { products ->
                _state.update { it.copy(products = products, isLoading = false) }
            },
            onFailure = { error ->
                val errorMessage = when (error) {
                    is AppError.NetworkError -> "No internet connection. Please check your network."
                    is AppError.ApiError -> error.message
                    is AppError.SystemError -> "An unexpected error occurred. Please try again."
                    else -> "An error occurred"
                }
                _state.update { it.copy(isLoading = false, error = errorMessage) }
            }
        )
    }
}
```

### Validation Error Handling

```kotlin
data class ValidationResult(
    val isValid: Boolean,
    val errors: Map<String, String> = emptyMap()
)

fun validateProductForm(state: ProductFormUiState): ValidationResult {
    val errors = mutableMapOf<String, String>()
    
    if (state.name.isBlank()) {
        errors["name"] = "Name is required"
    }
    
    if (state.weight.toDoubleOrNull() == null || state.weight.toDouble() <= 0) {
        errors["weight"] = "Weight must be a positive number"
    }
    
    if (state.price.toDoubleOrNull() == null || state.price.toDouble() <= 0) {
        errors["price"] = "Price must be a positive number"
    }
    
    if (state.selectedSupplier == null) {
        errors["supplier"] = "Supplier is required"
    }
    
    return ValidationResult(
        isValid = errors.isEmpty(),
        errors = errors
    )
}
```

### Retry Mechanism

```kotlin
// En ViewModel
fun retryLastOperation() {
    when (lastFailedOperation) {
        is Operation.LoadProducts -> loadProducts()
        is Operation.CreateProduct -> submitProduct()
        is Operation.UpdateProduct -> submitProduct()
        // ... otras operaciones
    }
}

// En UI
if (state.error != null) {
    ErrorMessage(
        message = state.error,
        onRetry = { viewModel.retryLastOperation() }
    )
}
```

### Timeout Configuration

```kotlin
// En NetworkModule
@Provides
@Singleton
fun provideOkHttpClient(): OkHttpClient {
    return OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(AuthInterceptor())
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        })
        .build()
}
```

## Testing Strategy

### Testing Approach

La estrategia de testing combina dos enfoques complementarios:

1. **Unit Tests**: Verifican comportamientos específicos, casos edge, y condiciones de error
2. **Property-Based Tests**: Verifican propiedades universales a través de múltiples inputs generados

### Property-Based Testing

Utilizaremos **Kotest Property Testing** para implementar las propiedades de corrección definidas en este documento.

#### Configuration

```kotlin
// build.gradle.kts
dependencies {
    testImplementation("io.kotest:kotest-runner-junit5:5.8.0")
    testImplementation("io.kotest:kotest-assertions-core:5.8.0")
    testImplementation("io.kotest:kotest-property:5.8.0")
    testImplementation("io.mockk:mockk:1.13.8")
    testImplementation("app.cash.turbine:turbine:1.0.0")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
```

#### Property Test Structure

Cada property test debe:
- Ejecutar mínimo 100 iteraciones
- Referenciar la propiedad del documento de diseño
- Usar generadores apropiados para los datos de entrada

```kotlin
class ProductPropertiesTest : StringSpec({
    
    "Property 1: Entity List Display Completeness - Products" {
        // Feature: inventory-mobile-frontend, Property 1
        checkAll(100, Arb.list(Arb.product(), 1..20)) { products ->
            val displayedFields = renderProductList(products)
            
            products.forEach { product ->
                displayedFields shouldContain product.name
                displayedFields shouldContain product.stock.toString()
                displayedFields shouldContain product.minimumStock.toString()
                displayedFields shouldContain product.supplier.name
            }
        }
    }
    
    "Property 10: Low Stock Visual Indication" {
        // Feature: inventory-mobile-frontend, Property 10
        checkAll(100, Arb.product()) { product ->
            val uiState = renderProductItem(product)
            
            if (product.stock < product.minimumStock) {
                uiState.hasLowStockIndicator shouldBe true
            }
        }
    }
    
    "Property 22: Required Field Validation" {
        // Feature: inventory-mobile-frontend, Property 22
        checkAll(100, Arb.productFormWithMissingFields()) { formState ->
            val validationResult = validateProductForm(formState)
            
            if (formState.name.isBlank()) {
                validationResult.isValid shouldBe false
                validationResult.errors shouldContainKey "name"
            }
        }
    }
    
    "Property 24: Positive Numeric Validation" {
        // Feature: inventory-mobile-frontend, Property 24
        checkAll(100, Arb.numericString(includeNegative = true, includeZero = true)) { value ->
            val formState = ProductFormUiState(price = value)
            val validationResult = validateProductForm(formState)
            
            val numericValue = value.toDoubleOrNull()
            if (numericValue != null && numericValue <= 0) {
                validationResult.isValid shouldBe false
                validationResult.errors shouldContainKey "price"
            }
        }
    }
})
```

#### Custom Generators

```kotlin
object ProductGenerators {
    fun Arb.Companion.product(): Arb<Product> = arbitrary {
        Product(
            id = Arb.long(1..10000).bind(),
            name = Arb.string(5..50).bind(),
            description = Arb.string(10..200).bind(),
            weight = Arb.double(0.1..100.0).bind(),
            unit = Arb.of("kg", "g", "L", "ml", "unit").bind(),
            price = Arb.double(0.1..1000.0).bind(),
            stock = Arb.double(0.0..500.0).bind(),
            minimumStock = Arb.double(1.0..50.0).bind(),
            imageUrl = Arb.string().orNull().bind(),
            state = Arb.enum<ProductState>().bind(),
            supplier = Arb.supplier().bind()
        )
    }
    
    fun Arb.Companion.supplier(): Arb<Supplier> = arbitrary {
        Supplier(
            id = Arb.long(1..1000).bind(),
            name = Arb.string(5..50).bind(),
            email = Arb.email().bind(),
            phone = Arb.phoneNumber().bind(),
            address = Arb.string(10..100).bind(),
            state = Arb.enum<SupplierState>().bind()
        )
    }
    
    fun Arb.Companion.dish(): Arb<Dish> = arbitrary {
        Dish(
            id = Arb.long(1..1000).bind(),
            name = Arb.string(5..50).bind(),
            description = Arb.string(10..200).bind(),
            price = Arb.double(1.0..100.0).bind(),
            preparationTime = Arb.int(5..120).bind(),
            imageUrl = Arb.string().orNull().bind(),
            state = Arb.enum<DishState>().bind(),
            category = Arb.category().bind(),
            recipe = Arb.list(Arb.recipeItem(), 1..10).bind()
        )
    }
    
    fun Arb.Companion.productFormWithMissingFields(): Arb<ProductFormUiState> = arbitrary {
        ProductFormUiState(
            name = Arb.string().orNull(0.3).bind() ?: "",
            description = Arb.string().bind(),
            weight = Arb.numericString().orNull(0.2).bind() ?: "",
            price = Arb.numericString().orNull(0.2).bind() ?: "",
            selectedSupplier = Arb.supplier().orNull(0.3).bind()
        )
    }
    
    fun Arb.Companion.numericString(
        includeNegative: Boolean = false,
        includeZero: Boolean = false
    ): Arb<String> = arbitrary {
        val value = if (includeNegative) {
            Arb.double(-100.0..100.0).bind()
        } else {
            Arb.double(0.0..100.0).bind()
        }
        
        if (!includeZero && value == 0.0) {
            "0.1"
        } else {
            value.toString()
        }
    }
}
```

### Unit Testing

Los unit tests se enfocan en:
- Casos específicos de validación
- Integración entre componentes
- Casos edge conocidos
- Comportamiento de ViewModels con estados específicos

```kotlin
class ProductViewModelTest : StringSpec({
    
    lateinit var viewModel: ProductViewModel
    lateinit var getProductsUseCase: GetProductsUseCase
    lateinit var createProductUseCase: CreateProductUseCase
    
    beforeTest {
        getProductsUseCase = mockk()
        createProductUseCase = mockk()
        viewModel = ProductViewModel(
            getProductsUseCase,
            // ... otros use cases
        )
    }
    
    "should display loading state when loading products" {
        coEvery { getProductsUseCase(any()) } coAnswers {
            delay(100)
            Result.success(emptyList())
        }
        
        viewModel.loadProducts()
        
        viewModel.listState.test {
            awaitItem().isLoading shouldBe true
        }
    }
    
    "should display error when API returns error" {
        val errorMessage = "Network error"
        coEvery { getProductsUseCase(any()) } returns Result.failure(
            AppError.NetworkError(errorMessage)
        )
        
        viewModel.loadProducts()
        
        viewModel.listState.test {
            val state = awaitItem()
            state.error shouldNotBe null
            state.error shouldContain "network"
        }
    }
    
    "should prevent submission when required fields are empty" {
        viewModel.onNameChange("")
        viewModel.submitProduct()
        
        viewModel.formState.test {
            val state = awaitItem()
            state.nameError shouldNotBe null
            state.isSubmitting shouldBe false
        }
        
        coVerify(exactly = 0) { createProductUseCase(any()) }
    }
    
    "should validate email format in supplier form" {
        val invalidEmails = listOf(
            "notanemail",
            "@example.com",
            "user@",
            "user @example.com"
        )
        
        invalidEmails.forEach { email ->
            val result = validateEmail(email)
            result.isValid shouldBe false
        }
        
        val validEmails = listOf(
            "user@example.com",
            "test.user@domain.co",
            "admin@restaurant.com"
        )
        
        validEmails.forEach { email ->
            val result = validateEmail(email)
            result.isValid shouldBe true
        }
    }
})
```

### Integration Testing

```kotlin
class ProductRepositoryIntegrationTest : StringSpec({
    
    lateinit var mockWebServer: MockWebServer
    lateinit var api: ProductApi
    lateinit var repository: ProductRepository
    
    beforeTest {
        mockWebServer = MockWebServer()
        mockWebServer.start()
        
        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
            .build()
        
        api = retrofit.create(ProductApi::class.java)
        repository = ProductRepositoryImpl(api, ProductMapper(SupplierMapper()))
    }
    
    afterTest {
        mockWebServer.shutdown()
    }
    
    "should parse successful API response correctly" {
        val responseJson = """
            {
                "message": [
                    {
                        "id": 1,
                        "name": "Tomato",
                        "description": "Fresh tomato",
                        "weight": 1.0,
                        "unit": "kg",
                        "price": 2.5,
                        "stock": 50.0,
                        "minimumStock": 10.0,
                        "state": "ACTIVE",
                        "suplier": {
                            "id": 1,
                            "name": "Supplier A",
                            "email": "supplier@example.com",
                            "phone": "123456789",
                            "address": "Address",
                            "state": "ACTIVE"
                        }
                    }
                ],
                "error": false
            }
        """.trimIndent()
        
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(responseJson)
        )
        
        val result = repository.getProducts(0)
        
        result.isSuccess shouldBe true
        result.getOrNull()?.size shouldBe 1
        result.getOrNull()?.first()?.name shouldBe "Tomato"
    }
    
    "should handle API error response" {
        val errorJson = """
            {
                "message": "Product not found",
                "error": true
            }
        """.trimIndent()
        
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(404)
                .setBody(errorJson)
        )
        
        val result = repository.getProductById(999)
        
        result.isFailure shouldBe true
    }
})
```

### UI Testing (Composable Tests)

```kotlin
class ProductListScreenTest : StringSpec({
    
    @get:Rule
    val composeTestRule = createComposeRule()
    
    "should display products in list" {
        val products = listOf(
            Product(
                id = 1,
                name = "Tomato",
                stock = 50.0,
                minimumStock = 10.0,
                // ... otros campos
            ),
            Product(
                id = 2,
                name = "Onion",
                stock = 5.0,
                minimumStock = 10.0,
                // ... otros campos
            )
        )
        
        composeTestRule.setContent {
            ProductListScreen(
                state = ProductListUiState(products = products),
                onProductClick = {},
                onCreateClick = {}
            )
        }
        
        composeTestRule.onNodeWithText("Tomato").assertIsDisplayed()
        composeTestRule.onNodeWithText("Onion").assertIsDisplayed()
    }
    
    "should display low stock indicator for products below minimum" {
        val lowStockProduct = Product(
            id = 1,
            name = "Low Stock Item",
            stock = 5.0,
            minimumStock = 10.0,
            // ... otros campos
        )
        
        composeTestRule.setContent {
            ProductItem(product = lowStockProduct, onClick = {})
        }
        
        composeTestRule.onNodeWithContentDescription("Low stock warning")
            .assertIsDisplayed()
    }
    
    "should disable submit button when form has errors" {
        composeTestRule.setContent {
            ProductFormScreen(
                state = ProductFormUiState(
                    name = "",
                    nameError = "Name is required"
                ),
                onSubmit = {}
            )
        }
        
        composeTestRule.onNodeWithText("Create")
            .assertIsNotEnabled()
    }
})
```

### Test Coverage Goals

- **Unit Tests**: 80% code coverage mínimo
- **Property Tests**: Todas las propiedades de corrección implementadas
- **Integration Tests**: Todos los repositorios y APIs
- **UI Tests**: Pantallas principales y flujos críticos

### Continuous Integration

```yaml
# .github/workflows/test.yml
name: Run Tests

on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    
    steps:
      - uses: actions/checkout@v3
      
      - name: Set up JDK 17
        uses: actions/setup-java@v3
        with:
          java-version: '17'
          distribution: 'temurin'
      
      - name: Run Unit Tests
        run: ./gradlew test
      
      - name: Run Property Tests
        run: ./gradlew test --tests "*PropertiesTest"
      
      - name: Generate Coverage Report
        run: ./gradlew jacocoTestReport
      
      - name: Upload Coverage
        uses: codecov/codecov-action@v3
```

