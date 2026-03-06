# Requirements Document

## Introduction

Este documento especifica los requisitos para el frontend móvil Android del sistema de inventario de Smart Restaurant. La aplicación permitirá al personal del restaurante gestionar productos, proveedores, categorías, platos, bebidas, adiciones y consultar movimientos de inventario mediante una interfaz nativa construida con Kotlin y Jetpack Compose que consume una API REST existente en Spring Boot.

## Glossary

- **Mobile_App**: Aplicación móvil Android nativa construida con Kotlin y Jetpack Compose
- **Backend_API**: API REST en Spring Boot que proporciona los servicios de inventario
- **Product**: Materia prima del inventario con control de stock
- **Suplier**: Proveedor que suministra productos al restaurante
- **Category**: Clasificación para platos y bebidas del menú
- **Dish**: Plato del menú con receta (lista de productos/ingredientes)
- **Drink**: Bebida del menú con control de unidades
- **Addition**: Extra o complemento que se puede añadir a pedidos
- **InventoryMovement**: Registro de entrada o salida de productos en el inventario
- **Stock**: Cantidad disponible de un producto en el inventario
- **Minimum_Stock**: Nivel mínimo de stock que genera alerta
- **Recipe**: Lista de productos con cantidades necesarias para preparar un plato
- **Soft_Delete**: Eliminación lógica que marca registros como INACTIVE sin borrarlos físicamente

## Requirements

### Requirement 1: Gestión de Productos

**User Story:** Como gerente del restaurante, quiero gestionar los productos del inventario desde mi dispositivo móvil, para mantener actualizado el catálogo de materias primas.

#### Acceptance Criteria

1. THE Mobile_App SHALL display a paginated list of active products with name, current stock, minimum stock, and supplier
2. WHEN the user requests to create a product, THE Mobile_App SHALL display a form with fields for name, description, weight, unit, price, minimum stock, supplier, and image
3. WHEN the user submits a valid product form, THE Mobile_App SHALL send a POST request to `/api/products` and display success confirmation
4. WHEN the user selects a product, THE Mobile_App SHALL display detailed information including all product attributes and associated supplier
5. WHEN the user requests to edit a product, THE Mobile_App SHALL display a pre-filled form with current product data
6. WHEN the user submits updated product data, THE Mobile_App SHALL send a PUT request to `/api/products/{id}` and refresh the display
7. WHEN the user requests to delete a product, THE Mobile_App SHALL display a confirmation dialog before sending DELETE request to `/api/products/{id}`
8. WHEN the Backend_API returns an error response, THE Mobile_App SHALL display the error message to the user

### Requirement 2: Control de Stock de Productos

**User Story:** Como encargado de almacén, quiero añadir o descontar stock de productos, para mantener el inventario actualizado con las entradas y salidas.

#### Acceptance Criteria

1. WHEN the user views a product detail, THE Mobile_App SHALL display current stock quantity and minimum stock threshold
2. WHEN the user requests to add stock, THE Mobile_App SHALL display a form requesting quantity to add and optional reason
3. WHEN the user submits a valid add stock request, THE Mobile_App SHALL send a PUT request to `/api/products/add-stock/{id}` with the quantity
4. WHEN the user requests to subtract stock, THE Mobile_App SHALL display a form requesting quantity to subtract and optional reason
5. WHEN the user submits a valid subtract stock request, THE Mobile_App SHALL send a PUT request to `/api/products/subtract-stock/{id}` with the quantity
6. WHEN stock is modified successfully, THE Mobile_App SHALL refresh the product display showing updated stock quantity
7. IF the Backend_API returns a stock validation error, THEN THE Mobile_App SHALL display the error message preventing negative stock

### Requirement 3: Alertas de Stock Mínimo

**User Story:** Como gerente del restaurante, quiero visualizar productos con stock bajo el mínimo, para realizar pedidos a proveedores oportunamente.

#### Acceptance Criteria

1. THE Mobile_App SHALL display a visual indicator on products where current stock is below minimum stock
2. THE Mobile_App SHALL provide a filter to display only products with low stock
3. WHEN a product has low stock, THE Mobile_App SHALL highlight the stock quantity in a warning color
4. THE Mobile_App SHALL display the count of products with low stock on the main inventory screen

### Requirement 4: Gestión de Proveedores

**User Story:** Como gerente del restaurante, quiero gestionar los proveedores desde mi dispositivo móvil, para mantener actualizada la información de contacto y productos suministrados.

#### Acceptance Criteria

1. THE Mobile_App SHALL display a list of active suppliers with name, email, and phone
2. WHEN the user requests to create a supplier, THE Mobile_App SHALL display a form with fields for name, email, phone, and address
3. WHEN the user submits a valid supplier form, THE Mobile_App SHALL send a POST request to `/api/supliers` and display success confirmation
4. WHEN the user selects a supplier, THE Mobile_App SHALL display detailed information and list of associated products
5. WHEN the user requests to edit a supplier, THE Mobile_App SHALL display a pre-filled form with current supplier data
6. WHEN the user submits updated supplier data, THE Mobile_App SHALL send a PUT request to `/api/supliers/{id}` and refresh the display
7. WHEN the user requests to delete a supplier, THE Mobile_App SHALL display a confirmation dialog before sending DELETE request to `/api/supliers/{id}`

### Requirement 5: Gestión de Categorías

**User Story:** Como gerente del restaurante, quiero gestionar las categorías de platos y bebidas, para organizar el menú de forma coherente.

#### Acceptance Criteria

1. THE Mobile_App SHALL display a list of active categories with name and description
2. WHEN the user requests to create a category, THE Mobile_App SHALL display a form with fields for name and description
3. WHEN the user submits a valid category form, THE Mobile_App SHALL send a POST request to `/api/categories` and display success confirmation
4. WHEN the user selects a category, THE Mobile_App SHALL display category details and list of associated dishes or drinks
5. WHEN the user requests to edit a category, THE Mobile_App SHALL display a pre-filled form with current category data
6. WHEN the user submits updated category data, THE Mobile_App SHALL send a PUT request to `/api/categories/{id}` and refresh the display
7. WHEN the user requests to delete a category, THE Mobile_App SHALL display a confirmation dialog before sending DELETE request to `/api/categories/{id}`

### Requirement 6: Gestión de Platos

**User Story:** Como chef del restaurante, quiero gestionar los platos del menú con sus recetas, para definir qué productos se necesitan para cada preparación.

#### Acceptance Criteria

1. THE Mobile_App SHALL display a paginated list of active dishes with name, price, category, and image
2. WHEN the user requests to create a dish, THE Mobile_App SHALL display a form with fields for name, description, price, category, preparation time, and image
3. WHEN creating or editing a dish, THE Mobile_App SHALL provide an interface to add products to the recipe with specific quantities
4. WHEN the user submits a dish with at least one ingredient, THE Mobile_App SHALL send a POST request to `/api/dishes` with the complete recipe
5. WHEN the user selects a dish, THE Mobile_App SHALL display detailed information including the complete recipe with product names and quantities
6. WHEN the user requests to edit a dish, THE Mobile_App SHALL display a pre-filled form with current dish data and recipe
7. WHEN the user submits updated dish data, THE Mobile_App SHALL send a PUT request to `/api/dishes/{id}` and refresh the display
8. WHEN the user requests to delete a dish, THE Mobile_App SHALL display a confirmation dialog before sending DELETE request to `/api/dishes/{id}`
9. IF the user attempts to save a dish without ingredients, THEN THE Mobile_App SHALL display a validation error requiring at least one ingredient

### Requirement 7: Gestión de Bebidas

**User Story:** Como gerente del restaurante, quiero gestionar las bebidas del menú con control de stock, para mantener actualizado el inventario de bebidas.

#### Acceptance Criteria

1. THE Mobile_App SHALL display a paginated list of active drinks with name, price, category, stock units, and image
2. WHEN the user requests to create a drink, THE Mobile_App SHALL display a form with fields for name, description, price, category, stock units, and image
3. WHEN the user submits a valid drink form, THE Mobile_App SHALL send a POST request to `/api/drinks` and display success confirmation
4. WHEN the user selects a drink, THE Mobile_App SHALL display detailed information including current stock units
5. WHEN the user requests to edit a drink, THE Mobile_App SHALL display a pre-filled form with current drink data
6. WHEN the user submits updated drink data, THE Mobile_App SHALL send a PUT request to `/api/drinks/{id}` and refresh the display
7. WHEN the user requests to delete a drink, THE Mobile_App SHALL display a confirmation dialog before sending DELETE request to `/api/drinks/{id}`

### Requirement 8: Gestión de Adiciones

**User Story:** Como gerente del restaurante, quiero gestionar las adiciones disponibles, para ofrecer extras y complementos a los clientes.

#### Acceptance Criteria

1. THE Mobile_App SHALL display a paginated list of active additions with name, price, and image
2. WHEN the user requests to create an addition, THE Mobile_App SHALL display a form with fields for name, description, price, and image
3. WHEN the user submits a valid addition form, THE Mobile_App SHALL send a POST request to `/api/additions` and display success confirmation
4. WHEN the user selects an addition, THE Mobile_App SHALL display detailed information
5. WHEN the user requests to edit an addition, THE Mobile_App SHALL display a pre-filled form with current addition data
6. WHEN the user submits updated addition data, THE Mobile_App SHALL send a PUT request to `/api/additions/{id}` and refresh the display
7. WHEN the user requests to delete an addition, THE Mobile_App SHALL display a confirmation dialog before sending DELETE request to `/api/additions/{id}`

### Requirement 9: Consulta de Movimientos de Inventario

**User Story:** Como gerente del restaurante, quiero consultar el histórico de movimientos de inventario, para auditar las entradas y salidas de productos.

#### Acceptance Criteria

1. THE Mobile_App SHALL display a list of inventory movements with date, product name, movement type, quantity, and reason
2. WHEN the user accesses inventory movements, THE Mobile_App SHALL send a GET request to `/api/inventory/all`
3. THE Mobile_App SHALL display movements sorted by date in descending order
4. THE Mobile_App SHALL visually differentiate between entry movements and exit movements
5. WHEN the user selects a movement, THE Mobile_App SHALL display detailed information including timestamp, product, quantity, type, and reason
6. THE Mobile_App SHALL provide filters to view movements by date range or movement type

### Requirement 10: Manejo de Imágenes

**User Story:** Como usuario de la aplicación, quiero capturar o seleccionar imágenes para productos, platos, bebidas y adiciones, para enriquecer la información visual del inventario.

#### Acceptance Criteria

1. WHEN creating or editing an entity with image support, THE Mobile_App SHALL provide options to capture photo or select from gallery
2. WHEN the user selects an image, THE Mobile_App SHALL display a preview before submission
3. WHEN the user submits a form with an image, THE Mobile_App SHALL upload the image to the Backend_API
4. WHEN the Backend_API returns an image URL from Cloudinary, THE Mobile_App SHALL store and display the URL
5. THE Mobile_App SHALL display existing images when viewing or editing entities
6. IF image upload fails, THEN THE Mobile_App SHALL display an error message and allow retry

### Requirement 11: Paginación de Listados

**User Story:** Como usuario de la aplicación, quiero navegar por listados largos de forma eficiente, para no sobrecargar la interfaz ni el rendimiento.

#### Acceptance Criteria

1. WHEN displaying paginated resources, THE Mobile_App SHALL request pages of 10 elements from the Backend_API
2. THE Mobile_App SHALL implement infinite scroll or pagination controls for dishes, drinks, and additions
3. WHEN the user scrolls to the end of a list, THE Mobile_App SHALL automatically request the next page
4. THE Mobile_App SHALL display a loading indicator while fetching additional pages
5. THE Mobile_App SHALL cache loaded pages to improve navigation performance

### Requirement 12: Validación de Formularios

**User Story:** Como usuario de la aplicación, quiero recibir retroalimentación inmediata sobre errores en formularios, para corregirlos antes de enviar datos al servidor.

#### Acceptance Criteria

1. THE Mobile_App SHALL validate required fields before allowing form submission
2. THE Mobile_App SHALL validate email format in supplier forms
3. THE Mobile_App SHALL validate that numeric fields for prices, weights, and quantities contain positive values
4. THE Mobile_App SHALL validate that stock quantities are non-negative
5. WHEN a validation error occurs, THE Mobile_App SHALL display an error message next to the invalid field
6. THE Mobile_App SHALL disable the submit button until all validations pass

### Requirement 13: Manejo de Estados de Conexión

**User Story:** Como usuario de la aplicación, quiero recibir retroalimentación clara sobre el estado de las operaciones, para saber si mis acciones fueron exitosas o fallaron.

#### Acceptance Criteria

1. WHEN the Mobile_App sends a request to the Backend_API, THE Mobile_App SHALL display a loading indicator
2. WHEN the Backend_API returns a successful response, THE Mobile_App SHALL display a success message
3. WHEN the Backend_API returns an error response with `error: true`, THE Mobile_App SHALL display the error message from the response
4. IF the Backend_API is unreachable, THEN THE Mobile_App SHALL display a network error message
5. WHEN a network error occurs, THE Mobile_App SHALL provide a retry option
6. THE Mobile_App SHALL implement request timeout of 30 seconds for all API calls

### Requirement 14: Navegación y Arquitectura de UI

**User Story:** Como usuario de la aplicación, quiero navegar de forma intuitiva entre las diferentes secciones del inventario, para acceder rápidamente a la información que necesito.

#### Acceptance Criteria

1. THE Mobile_App SHALL implement a bottom navigation bar or drawer menu with sections for Products, Suppliers, Categories, Dishes, Drinks, Additions, and Inventory Movements
2. WHEN the user selects a section, THE Mobile_App SHALL navigate to the corresponding list view
3. WHEN the user selects an item from a list, THE Mobile_App SHALL navigate to the detail view
4. THE Mobile_App SHALL provide a back button to return to the previous screen
5. THE Mobile_App SHALL maintain navigation state when the app is backgrounded and restored
6. THE Mobile_App SHALL implement Material Design 3 guidelines for Android using Jetpack Compose

### Requirement 15: Búsqueda y Filtrado

**User Story:** Como usuario de la aplicación, quiero buscar y filtrar elementos en los listados, para encontrar rápidamente la información específica que necesito.

#### Acceptance Criteria

1. THE Mobile_App SHALL provide a search bar in list views for Products, Suppliers, Dishes, Drinks, and Additions
2. WHEN the user types in the search bar, THE Mobile_App SHALL filter the displayed list in real-time
3. THE Mobile_App SHALL search by name in all searchable lists
4. THE Mobile_App SHALL provide filter options for Products by supplier and low stock status
5. THE Mobile_App SHALL provide filter options for Dishes and Drinks by category
6. WHEN filters are applied, THE Mobile_App SHALL display a visual indicator showing active filters

