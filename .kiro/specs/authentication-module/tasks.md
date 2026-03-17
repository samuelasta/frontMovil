# Plan de Implementación: Authentication Module

## Descripción General

Este plan implementa el módulo de autenticación para la aplicación Android SmartRestaurant. El módulo incluye gestión de sesiones con JWT, perfil de usuario, cambio de contraseña, administración de empleados (solo ADMIN) y auditoría de seguridad (solo ADMIN). La arquitectura sigue Clean Architecture + MVVM con tres capas: Presentation, Domain y Data.

**Stack Tecnológico:**
- Kotlin + Jetpack Compose + Material Design 3
- Retrofit + OkHttp para networking
- Hilt para inyección de dependencias
- EncryptedSharedPreferences para almacenamiento seguro de tokens
- Navigation Compose para navegación
- Coroutines + Flow para asincronía
- Kotest para property-based testing

**Estrategia de Implementación:**
1. Implementar infraestructura de autenticación (TokenManager, AuthInterceptor)
2. Módulo Auth (verificación 2FA, perfil, cambio de contraseña)
3. Módulo User Management (solo ADMIN)
4. Módulo Audit Logs (solo ADMIN)
5. Integración con navegación y control de acceso por roles

## Tareas

- [ ] 1. Infraestructura de Autenticación
  - [ ] 1.1 Implementar TokenManager
    - Crear interface TokenManager
    - Implementar TokenManagerImpl con EncryptedSharedPreferences
    - Métodos: saveTokens(), getAccessToken(), getRefreshToken(), clearTokens()
    - Usar Android Keystore para encriptación
    - _Requisitos: 25.1, 25.2, 25.3_

  - [ ] 1.2 Implementar AuthInterceptor
    - Crear AuthInterceptor para OkHttp
    - Agregar Access Token automáticamente a headers
    - Detectar respuestas 401 Unauthorized
    - Intentar refresh token automáticamente
    - Reintentar request original con nuevo token
    - Limpiar tokens y redirigir a login si refresh falla
    - _Requisitos: 2.1, 2.4, 2.5, 26.1_

  - [ ] 1.3 Configurar Hilt modules
    - Actualizar NetworkModule con AuthInterceptor
    - Crear AuthModule para TokenManager
    - Proveer instancias singleton
    - _Requisitos: 13.6_


- [ ] 2. Módulo Auth - Capa de Datos
  - [ ] 2.1 Crear DTOs para Auth
    - Crear Verify2FARequest con @Serializable
    - Crear AuthResponse con @Serializable
    - Crear ChangePasswordRequest con @Serializable
    - _Requisitos: 1.2, 2.1, 6.4_

  - [ ] 2.2 Crear AuthApi interface con Retrofit
    - Definir endpoint POST /api/auth/verify-2fa
    - Definir endpoint POST /api/auth/refresh-token
    - Definir endpoint POST /api/auth/logout
    - Definir endpoint GET /api/auth/me
    - Definir endpoint POST /api/auth/request-password-change
    - Definir endpoint POST /api/auth/change-password
    - Definir endpoint POST /api/auth/unlock-account
    - _Requisitos: 1.2, 2.1, 3.2, 4.1, 5.2, 6.4, 7.3_

  - [ ] 2.3 Crear UserMapper
    - Implementar UserMapper.toDomain()
    - Implementar UserMapper.toDto()
    - Manejar conversión de enums (UserRole, UserStatus)
    - _Requisitos: 4.2_

  - [ ] 2.4 Implementar AuthRepositoryImpl
    - Implementar verify2FA() con almacenamiento de tokens
    - Implementar refreshToken() con actualización de tokens
    - Implementar logout() con limpieza de tokens
    - Implementar getCurrentUser() con manejo de errores
    - Implementar requestPasswordChange() con manejo de errores
    - Implementar changePassword() con limpieza de tokens
    - Implementar unlockAccount() con manejo de errores
    - _Requisitos: 1.2, 1.3, 2.1, 2.2, 3.2, 3.3, 4.1, 5.2, 6.4, 6.5, 7.3, 7.4, 13.3, 13.4_


- [ ] 3. Módulo Auth - Capa de Dominio
  - [ ] 3.1 Crear modelos de dominio
    - Crear User data class
    - Crear UserRole enum (ADMIN, KITCHEN, WAITER, CUSTOMER)
    - Crear UserStatus enum (ACTIVE, INACTIVE, PENDING, BANNED)
    - _Requisitos: 4.2_

  - [ ] 3.2 Crear AuthRepository interface
    - Definir métodos suspend con Result<T>
    - _Requisitos: 1.2, 2.1, 3.2, 4.1, 5.2, 6.4, 7.3_

  - [ ] 3.3 Crear Use Cases para Auth
    - Implementar Verify2FAUseCase con validación de código no vacío
    - Implementar RefreshTokenUseCase
    - Implementar LogoutUseCase
    - Implementar GetCurrentUserUseCase
    - Implementar RequestPasswordChangeUseCase
    - Implementar ChangePasswordUseCase con validación de contraseña
    - Implementar UnlockAccountUseCase
    - _Requisitos: 1.2, 2.1, 3.2, 4.1, 5.2, 6.4, 7.3_


- [ ] 4. Módulo Auth - Capa de Presentación
  - [ ] 4.1 Crear UI States para Auth
    - Crear Verify2FAUiState con código y errores
    - Crear ProfileUiState con usuario y loading
    - Crear ChangePasswordUiState con campos, validación y PasswordStrength enum
    - _Requisitos: 1.1, 4.2, 6.1, 6.2, 6.3, 28.5_

  - [ ] 4.2 Implementar AuthViewModel
    - Implementar verify2FA() con StateFlow
    - Implementar loadCurrentUser() con StateFlow
    - Implementar requestPasswordChange() con StateFlow
    - Implementar changePassword() con validación completa
    - Implementar validatePasswordForm() con todas las reglas
    - Implementar calculatePasswordStrength()
    - Implementar logout() con limpieza de estado
    - _Requisitos: 1.2, 1.4, 3.2, 4.1, 5.2, 6.2, 6.3, 6.4, 28.1, 28.2, 28.3, 28.4, 28.5_

  - [ ] 4.3 Crear Verify2FAScreen composable
    - Implementar formulario con campo de código
    - Implementar botón de verificación con loading
    - Implementar opción de reenviar código
    - Implementar manejo de errores
    - _Requisitos: 1.1, 1.2, 1.5_

  - [ ] 4.4 Crear ProfileScreen composable
    - Mostrar información completa del usuario
    - Mostrar alerta si requiresPasswordChange es true
    - Implementar botón de cambio de contraseña
    - Implementar botón de logout
    - Mostrar indicador de email verificado
    - _Requisitos: 4.2, 4.3, 4.4, 4.5, 27.1_

  - [ ] 4.5 Crear ChangePasswordScreen composable
    - Implementar formulario con todos los campos
    - Implementar validación en tiempo real
    - Implementar indicador de fortaleza de contraseña
    - Implementar mensajes de error específicos por campo
    - Implementar botón de solicitar OTP
    - Implementar botón de submit con loading
    - _Requisitos: 5.1, 5.3, 5.4, 6.1, 6.2, 6.3, 6.5, 28.5, 28.6_

  - [ ]* 4.6 Property tests para Auth
    - **Property 1: Token Storage Security**
    - **Valida: Requisitos 1.3, 2.2, 25.1**
    - **Property 2: Token Refresh on 401**
    - **Valida: Requisitos 2.5**
    - **Property 3: Token Cleanup on Logout**
    - **Valida: Requisitos 3.3, 26.2**
    - **Property 4: User Profile Display Completeness**
    - **Valida: Requisitos 4.2**
    - **Property 5: Password Confirmation Match**
    - **Valida: Requisitos 6.2**
    - **Property 6: Password Strength Validation**
    - **Valida: Requisitos 6.3, 28.1, 28.2, 28.3, 28.4**


- [ ] 5. Checkpoint - Módulo Auth completo
  - Verificar que todos los tests pasen
  - Verificar almacenamiento seguro de tokens
  - Verificar flujo de cambio de contraseña
  - Preguntar al usuario si hay dudas o ajustes necesarios

- [ ] 6. Módulo User Management - Capa de Datos
  - [ ] 6.1 Crear DTOs para User Management
    - Crear RegisterEmployeeRequest con @Serializable
    - Crear RegisterEmployeeResponse con @Serializable
    - Crear UpdateUserRequest con @Serializable
    - Crear ChangeRoleRequest con @Serializable
    - Crear UserDto con @Serializable (si no existe)
    - _Requisitos: 8.4, 11.4_

  - [ ] 6.2 Crear AdminApi interface con Retrofit
    - Definir endpoint POST /api/admin/register-employee
    - Definir endpoint GET /api/admin/users con query params
    - Definir endpoint GET /api/admin/users/{id}
    - Definir endpoint PUT /api/admin/users/{id}
    - Definir endpoint PATCH /api/admin/users/{id}/role
    - Definir endpoint PATCH /api/admin/users/{id}/activate
    - Definir endpoint PATCH /api/admin/users/{id}/deactivate
    - _Requisitos: 8.4, 9.1, 10.1, 11.4, 12.3, 13.3, 14.3_

  - [ ] 6.3 Implementar UserRepositoryImpl
    - Implementar registerEmployee() con manejo de errores
    - Implementar getUsers() con filtros y paginación
    - Implementar getUserById() con manejo de errores
    - Implementar updateUser() con manejo de errores
    - Implementar changeUserRole() con manejo de errores
    - Implementar activateUser() con manejo de errores
    - Implementar deactivateUser() con manejo de errores
    - _Requisitos: 8.4, 8.7, 9.1, 9.5, 10.1, 11.4, 11.6, 12.3, 13.3, 14.3, 13.3, 13.4_


- [ ] 7. Módulo User Management - Capa de Dominio
  - [ ] 7.1 Crear UserRepository interface
    - Definir métodos suspend con Result<T>
    - Incluir parámetros de filtro y paginación
    - _Requisitos: 8.4, 9.1, 10.1, 11.4, 12.3, 13.3, 14.3_

  - [ ] 7.2 Crear Use Cases para User Management
    - Implementar RegisterEmployeeUseCase con validación de email
    - Implementar GetUsersUseCase con validación de página
    - Implementar GetUserByIdUseCase
    - Implementar UpdateUserUseCase con validación de email
    - Implementar ChangeUserRoleUseCase
    - Implementar ActivateUserUseCase
    - Implementar DeactivateUserUseCase
    - _Requisitos: 8.4, 9.1, 10.1, 11.4, 12.3, 13.3, 14.3_


- [ ] 8. Módulo User Management - Capa de Presentación
  - [ ] 8.1 Crear UI States para User Management
    - Crear UserListUiState con paginación y filtros
    - Crear UserDetailUiState con diálogos de confirmación
    - Crear UserFormUiState con validación de email
    - _Requisitos: 9.2, 9.3, 9.4, 10.2, 10.3, 11.1, 11.2, 12.1, 12.4, 13.1, 13.4, 14.1, 14.4_

  - [ ] 8.2 Implementar UserViewModel
    - Implementar loadUsers() con paginación y StateFlow
    - Implementar loadNextPage() para infinite scroll
    - Implementar loadUserById() con actividad reciente
    - Implementar onSearchQueryChange() con debounce
    - Implementar onRoleFilterChange()
    - Implementar onStatusFilterChange()
    - Implementar registerEmployee() con validación
    - Implementar updateUser() con validación
    - Implementar changeUserRole() con confirmación
    - Implementar activateUser() con confirmación
    - Implementar deactivateUser() con confirmación
    - Implementar validateForm() con email format
    - _Requisitos: 8.4, 9.1, 9.2, 9.5, 10.1, 10.2, 11.4, 12.3, 12.5, 13.3, 13.5, 14.3, 14.5, 30.2, 30.3, 30.5_

  - [ ] 8.3 Crear UserListScreen composable
    - Implementar Scaffold con TopAppBar y FAB
    - Implementar SearchBar con debounce
    - Implementar FilterChips para role y status
    - Implementar lista con LazyColumn
    - Implementar UserListItem con role badge y status indicator
    - Implementar infinite scroll para paginación
    - Implementar estados de loading, error y empty
    - Implementar protección por rol (solo ADMIN)
    - _Requisitos: 8.1, 9.1, 9.2, 9.3, 9.4, 9.7, 24.1, 24.3, 30.1, 30.2, 30.5_

  - [ ] 8.4 Crear UserDetailScreen composable
    - Mostrar todos los atributos del usuario
    - Mostrar indicadores visuales de estado
    - Mostrar información de bloqueo si aplica
    - Implementar botones para editar, cambiar rol, activar/desactivar
    - Implementar diálogo de cambio de rol
    - Implementar diálogos de confirmación
    - Mostrar sección de actividad reciente (últimos 10 logs)
    - Implementar protección por rol (solo ADMIN)
    - _Requisitos: 10.2, 10.3, 12.1, 12.4, 12.5, 13.1, 13.2, 13.4, 13.5, 14.1, 14.2, 14.4, 14.5, 21.1, 21.3, 24.1_

  - [ ] 8.5 Crear UserFormScreen composable
    - Implementar formulario con firstName, lastName, email, role
    - Implementar validación de email en tiempo real
    - Implementar selector de rol con opciones
    - Implementar pre-población en modo edición
    - Implementar botón submit con loading
    - Mostrar contraseña temporal después de registro exitoso
    - Implementar mensajes de error por campo
    - Implementar protección por rol (solo ADMIN)
    - _Requisitos: 8.1, 8.2, 8.3, 8.4, 8.5, 8.6, 11.1, 11.2, 11.3, 11.5, 24.1_

  - [ ]* 8.6 Property tests para User Management
    - **Property 7: Email Format Validation**
    - **Valida: Requisitos 8.3, 11.3**
    - **Property 8: Admin-Only Access Control**
    - **Valida: Requisitos 8.8, 24.1, 24.3, 24.4**
    - **Property 9: User List Display Completeness**
    - **Valida: Requisitos 9.2**
    - **Property 10: Filter Parameters in API Calls**
    - **Valida: Requisitos 9.5**
    - **Property 11: Pagination Consistency**
    - **Valida: Requisitos 9.6**
    - **Property 12: User Detail Display Completeness**
    - **Valida: Requisitos 10.2**
    - **Property 13: Form Pre-population**
    - **Valida: Requisitos 11.1**
    - **Property 23: User Search Filtering**
    - **Valida: Requisitos 30.2**
    - **Property 24: Combined Filters**
    - **Valida: Requisitos 30.5**


- [ ] 9. Checkpoint - Módulo User Management completo
  - Verificar que todos los tests pasen
  - Verificar control de acceso por rol funciona
  - Verificar filtros y búsqueda funcionan correctamente
  - Preguntar al usuario si hay dudas o ajustes necesarios

- [ ] 10. Módulo Audit Logs - Capa de Datos
  - [ ] 10.1 Crear DTOs para Audit Logs
    - Crear AuditLogDto con @Serializable
    - Incluir todos los campos del log
    - _Requisitos: 15.2, 23.1_

  - [ ] 10.2 Crear AuditApi interface con Retrofit
    - Definir endpoint GET /api/admin/audit-logs
    - Definir endpoint GET /api/admin/audit-logs/by-user
    - Definir endpoint GET /api/admin/audit-logs/by-event-type
    - Definir endpoint GET /api/admin/audit-logs/by-date-range
    - Definir endpoint GET /api/admin/audit-logs/failed
    - Definir endpoint GET /api/admin/audit-logs/critical
    - Definir endpoint GET /api/admin/audit-logs/recent-by-user
    - Definir endpoint GET /api/admin/audit-logs/by-ip
    - _Requisitos: 15.1, 16.2, 17.2, 18.2, 19.2, 20.2, 21.2, 22.2_

  - [ ] 10.3 Crear AuditLogMapper
    - Implementar AuditLogMapper.toDomain()
    - Manejar conversión de timestamp
    - _Requisitos: 15.2, 23.1_

  - [ ] 10.4 Implementar AuditLogRepositoryImpl
    - Implementar getAuditLogs() con paginación
    - Implementar getAuditLogsByUser() con paginación
    - Implementar getAuditLogsByEventType() con paginación
    - Implementar getAuditLogsByDateRange() con paginación
    - Implementar getFailedAuditLogs() con paginación
    - Implementar getCriticalAuditLogs() con paginación
    - Implementar getRecentAuditLogsByUser() sin paginación
    - Implementar getAuditLogsByIp() con paginación
    - Todos con manejo de errores
    - _Requisitos: 15.1, 16.2, 17.2, 18.2, 19.2, 20.2, 21.2, 22.2, 13.3, 13.4_


- [ ] 11. Módulo Audit Logs - Capa de Dominio
  - [ ] 11.1 Crear modelos de dominio
    - Crear AuditLog data class
    - Incluir todos los campos del log
    - _Requisitos: 15.2, 23.1_

  - [ ] 11.2 Crear AuditLogRepository interface
    - Definir métodos suspend con Result<T>
    - Incluir parámetros de filtro y paginación
    - _Requisitos: 15.1, 16.2, 17.2, 18.2, 19.2, 20.2, 21.2, 22.2_

  - [ ] 11.3 Crear Use Cases para Audit Logs
    - Implementar GetAuditLogsUseCase con validación de página
    - Implementar GetAuditLogsByUserUseCase
    - Implementar GetAuditLogsByEventTypeUseCase
    - Implementar GetAuditLogsByDateRangeUseCase con validación de fechas
    - Implementar GetFailedAuditLogsUseCase
    - Implementar GetCriticalAuditLogsUseCase
    - Implementar GetRecentAuditLogsByUserUseCase
    - Implementar GetAuditLogsByIpUseCase
    - _Requisitos: 15.1, 16.2, 17.2, 18.2, 18.4, 19.2, 20.2, 21.2, 22.2_


- [ ] 12. Módulo Audit Logs - Capa de Presentación
  - [ ] 12.1 Crear UI States para Audit Logs
    - Crear AuditLogListUiState con paginación y múltiples filtros
    - Crear AuditLogDetailUiState
    - _Requisitos: 15.2, 16.1, 17.1, 18.1, 19.1, 20.1, 22.1, 23.1_

  - [ ] 12.2 Implementar AuditViewModel
    - Implementar loadAuditLogs() con paginación y StateFlow
    - Implementar loadNextPage() para infinite scroll
    - Implementar applyUserFilter()
    - Implementar applyEventTypeFilter()
    - Implementar applyDateRangeFilter() con validación
    - Implementar applyIpFilter()
    - Implementar toggleFailedOnly()
    - Implementar toggleCriticalOnly()
    - Implementar clearFilter()
    - Implementar validateDateRange()
    - _Requisitos: 15.1, 15.4, 16.2, 17.2, 18.2, 18.4, 19.2, 20.2, 22.2_

  - [ ] 12.3 Crear AuditLogListScreen composable
    - Implementar Scaffold con TopAppBar y filtros
    - Implementar ActiveFiltersChips mostrando filtros activos
    - Implementar FilterDialog con todas las opciones
    - Implementar lista con LazyColumn
    - Implementar AuditLogListItem con diferenciación visual
    - Mostrar indicador de evento crítico
    - Mostrar indicador de evento fallido
    - Implementar infinite scroll para paginación
    - Implementar estados de loading, error y empty
    - Implementar protección por rol (solo ADMIN)
    - _Requisitos: 15.1, 15.2, 15.5, 15.6, 15.7, 16.1, 17.1, 18.1, 19.1, 20.1, 20.4, 22.1, 24.1_

  - [ ] 12.4 Crear AuditLogDetailScreen composable
    - Mostrar todos los campos del log
    - Formatear timestamp de forma legible
    - Mostrar details completo
    - Destacar errorMessage si existe
    - Mostrar indicador de evento crítico
    - Implementar protección por rol (solo ADMIN)
    - _Requisitos: 23.1, 23.2, 23.3, 23.4, 24.1_

  - [ ]* 12.5 Property tests para Audit Logs
    - **Property 10: Filter Parameters in API Calls**
    - **Valida: Requisitos 16.2, 17.2, 18.2, 19.2, 20.2, 22.2**
    - **Property 11: Pagination Consistency**
    - **Valida: Requisitos 15.3**
    - **Property 14: Audit Log Display Completeness**
    - **Valida: Requisitos 15.2**
    - **Property 15: Audit Log Sorting**
    - **Valida: Requisitos 15.4**
    - **Property 16: Recent Activity Limit**
    - **Valida: Requisitos 21.3**
    - **Property 17: Audit Log Detail Completeness**
    - **Valida: Requisitos 23.1**
    - **Property 18: Timestamp Formatting**
    - **Valida: Requisitos 23.2**
    - **Property 25: Date Range Validation**
    - **Valida: Requisitos 18.4**


- [ ] 13. Checkpoint - Módulo Audit Logs completo
  - Verificar que todos los tests pasen
  - Verificar todos los filtros funcionan correctamente
  - Verificar diferenciación visual de eventos críticos y fallidos
  - Preguntar al usuario si hay dudas o ajustes necesarios

- [ ] 14. Integración y Navegación
  - [ ] 14.1 Actualizar NavGraph
    - Agregar rutas para todas las pantallas de autenticación
    - Agregar rutas para User Management (protegidas por rol)
    - Agregar rutas para Audit Logs (protegidas por rol)
    - Implementar navegación desde perfil a cambio de contraseña
    - Implementar navegación desde menú principal a gestión de usuarios
    - Implementar navegación desde menú principal a audit logs
    - _Requisitos: 29.1, 29.2, 29.3, 29.4_

  - [ ] 14.2 Implementar Role-Based Access Control
    - Crear RoleGuard composable
    - Verificar rol antes de mostrar opciones de navegación
    - Verificar rol antes de permitir navegación a pantallas protegidas
    - Mostrar mensaje de acceso denegado si rol insuficiente
    - Ocultar opciones de menú para usuarios no-admin
    - _Requisitos: 24.1, 24.2, 24.3, 24.4, 24.5_

  - [ ] 14.3 Implementar Session Management
    - Crear SessionManager para manejar estado de sesión
    - Detectar sesión expirada y mostrar mensaje
    - Guardar deep link de pantalla actual antes de logout
    - Redirigir a pantalla guardada después de re-autenticación
    - Implementar lógica de contraseña temporal obligatoria
    - Restringir navegación hasta cambio de contraseña
    - _Requisitos: 26.1, 26.3, 26.4, 27.1, 27.2, 27.3_

  - [ ] 14.4 Actualizar ValidationUtils
    - Agregar validateEmail() con regex
    - Agregar validatePassword() con todas las reglas
    - Agregar calculatePasswordStrength()
    - _Requisitos: 12.2, 28.1, 28.2, 28.3, 28.4, 28.5_


- [ ] 15. Testing Final
  - [ ] 15.1 Unit Tests
    - Tests para TokenManager (almacenamiento, recuperación, limpieza)
    - Tests para AuthInterceptor (agregar token, detectar 401, refresh)
    - Tests para todos los Repositories
    - Tests para todos los Use Cases
    - Tests para todos los ViewModels
    - Tests para todos los Mappers
    - _Requisitos: Todos_

  - [ ] 15.2 Property-Based Tests
    - Implementar todas las 25 propiedades identificadas
    - Configurar Kotest con mínimo 100 iteraciones
    - Agregar tags con referencia a propiedades
    - _Requisitos: Todos_

  - [ ] 15.3 Integration Tests
    - Test de flujo completo de autenticación
    - Test de flujo de cambio de contraseña
    - Test de flujo de registro de empleado
    - Test de flujo de filtrado de audit logs
    - Test de control de acceso por rol
    - _Requisitos: Todos_

- [ ] 16. Checkpoint Final
  - Verificar cobertura de tests (>85%)
  - Verificar que todas las propiedades están implementadas
  - Verificar integración con módulos existentes
  - Verificar que no hay regresiones en funcionalidad existente
  - Documentar cualquier decisión de diseño importante
  - Preguntar al usuario si hay ajustes finales necesarios

## Notas de Implementación

### Prioridades
1. **Seguridad primero**: TokenManager y AuthInterceptor son críticos
2. **Flujo básico**: Auth module debe funcionar antes de User Management
3. **Control de acceso**: RBAC debe estar sólido antes de Audit Logs
4. **Testing continuo**: Ejecutar tests después de cada módulo

### Dependencias entre Tareas
- Tarea 2-4 dependen de Tarea 1 (infraestructura)
- Tarea 6-8 dependen de Tarea 2-4 (Auth module completo)
- Tarea 10-12 dependen de Tarea 6-8 (User Management completo)
- Tarea 14 depende de todas las anteriores

### Consideraciones Especiales
- **EncryptedSharedPreferences**: Requiere API 23+, usar fallback para versiones anteriores
- **AuthInterceptor**: Debe manejar race conditions en refresh token
- **Deep Links**: Guardar como String serializado en SessionManager
- **Password Strength**: Implementar algoritmo robusto con múltiples criterios
- **Audit Logs**: Pueden ser muy grandes, optimizar paginación y filtros

