# Diseño Técnico: Módulo de Autenticación

## Overview

El módulo de autenticación proporciona funcionalidades completas de gestión de usuarios, autenticación, autorización y auditoría para la aplicación móvil Android SmartRestaurant. Este módulo implementa Clean Architecture con tres capas claramente separadas (Data, Domain, Presentation) y sigue los patrones arquitectónicos establecidos en el proyecto.

### Alcance del Módulo

El módulo incluye:
- Verificación 2FA y gestión de sesiones con tokens JWT
- Renovación automática de tokens de acceso
- Perfil de usuario y cambio de contraseña con OTP
- Administración de empleados (registro, edición, cambio de rol, activación/desactivación) - Solo ADMIN
- Sistema de auditoría con múltiples filtros - Solo ADMIN
- Control de acceso basado en roles (RBAC)
- Almacenamiento seguro de tokens con Android Keystore

### Tecnologías

- **Lenguaje**: Kotlin
- **UI Framework**: Jetpack Compose con Material Design 3
- **Arquitectura**: Clean Architecture (Data, Domain, Presentation)
- **Inyección de Dependencias**: Hilt
- **Networking**: Retrofit + OkHttp
- **Manejo de Estado**: StateFlow
- **Almacenamiento Seguro**: EncryptedSharedPreferences / Android Keystore
- **Navegación**: Jetpack Navigation Compose
- **Testing**: JUnit 4, Mockito, Kotest para property-based testing

## Architecture

### Estructura de Capas

```
app/src/main/java/com/example/smartrestaurant/
├── data/
│   ├── remote/
│   │   ├── api/
│   │   │   ├── AuthApi.kt
│   │   │   └── AdminApi.kt
│   │   └── dto/
│   │       ├── Verify2FARequest.kt
│   │       ├── AuthResponse.kt
│   │       ├── ChangePasswordRequest.kt
│   │       ├── RegisterEmployeeRequest.kt
│   │       ├── RegisterEmployeeResponse.kt
│   │       ├── UpdateUserRequest.kt
│   │       ├── ChangeRoleRequest.kt
│   │       ├── UserDto.kt
│   │       └── AuditLogDto.kt
│   ├── local/
│   │   └── TokenManager.kt
│   ├── mapper/
│   │   ├── UserMapper.kt
│   │   └── AuditLogMapper.kt
│   └── repository/
│       ├── AuthRepositoryImpl.kt
│       ├── UserRepositoryImpl.kt
│       └── AuditLogRepositoryImpl.kt
├── domain/
│   ├── model/
│   │   ├── User.kt
│   │   ├── UserRole.kt
│   │   ├── UserStatus.kt
│   │   └── AuditLog.kt
│   ├── repository/
│   │   ├── AuthRepository.kt
│   │   ├── UserRepository.kt
│   │   └── AuditLogRepository.kt
│   └── usecase/
│       ├── auth/
│       │   ├── Verify2FAUseCase.kt
│       │   ├── RefreshTokenUseCase.kt
│       │   ├── LogoutUseCase.kt
│       │   ├── GetCurrentUserUseCase.kt
│       │   ├── RequestPasswordChangeUseCase.kt
│       │   ├── ChangePasswordUseCase.kt
│       │   └── UnlockAccountUseCase.kt
│       ├── user/
│       │   ├── RegisterEmployeeUseCase.kt
│       │   ├── GetUsersUseCase.kt
│       │   ├── GetUserByIdUseCase.kt
│       │   ├── UpdateUserUseCase.kt
│       │   ├── ChangeUserRoleUseCase.kt
│       │   ├── ActivateUserUseCase.kt
│       │   └── DeactivateUserUseCase.kt
│       └── audit/
│           ├── GetAuditLogsUseCase.kt
│           ├── GetAuditLogsByUserUseCase.kt
│           ├── GetAuditLogsByEventTypeUseCase.kt
│           ├── GetAuditLogsByDateRangeUseCase.kt
│           ├── GetFailedAuditLogsUseCase.kt
│           ├── GetCriticalAuditLogsUseCase.kt
│           ├── GetRecentAuditLogsByUserUseCase.kt
│           └── GetAuditLogsByIpUseCase.kt
└── presentation/
    ├── auth/
    │   ├── Verify2FAScreen.kt
    │   ├── ProfileScreen.kt
    │   ├── ChangePasswordScreen.kt
    │   ├── AuthViewModel.kt
    │   └── AuthUiState.kt
    ├── user/
    │   ├── UserListScreen.kt
    │   ├── UserDetailScreen.kt
    │   ├── UserFormScreen.kt
    │   ├── UserViewModel.kt
    │   └── UserUiState.kt
    └── audit/
        ├── AuditLogListScreen.kt
        ├── AuditLogDetailScreen.kt
        ├── AuditViewModel.kt
        └── AuditUiState.kt
```


### Flujo de Datos

1. **Presentation Layer**: Composables capturan eventos del usuario y observan StateFlow del ViewModel
2. **ViewModel**: Procesa eventos, invoca Use Cases y actualiza UI State
3. **Use Cases**: Implementan lógica de negocio y coordinan llamadas al Repository
4. **Repository**: Abstrae fuentes de datos (API, almacenamiento local)
5. **Data Sources**: API (Retrofit) y TokenManager (EncryptedSharedPreferences)
6. **Mappers**: Convierten entre DTOs y modelos de dominio

### Interceptor de Autenticación

Se implementará un `AuthInterceptor` para:
- Agregar automáticamente el Access Token a todas las peticiones API
- Detectar respuestas 401 Unauthorized
- Intentar renovación de token automáticamente
- Reintentar la petición original con el nuevo token
- Redirigir a login si la renovación falla

## Components and Interfaces

### Data Layer

#### API Interfaces

**AuthApi.kt**
```kotlin
interface AuthApi {
    @POST("/api/auth/verify-2fa")
    suspend fun verify2FA(@Body request: Verify2FARequest): ApiResponse<AuthResponse>
    
    @POST("/api/auth/refresh-token")
    suspend fun refreshToken(): ApiResponse<AuthResponse>
    
    @POST("/api/auth/logout")
    suspend fun logout(): ApiResponse<Unit>
    
    @GET("/api/auth/me")
    suspend fun getCurrentUser(): ApiResponse<UserDto>
    
    @POST("/api/auth/request-password-change")
    suspend fun requestPasswordChange(): ApiResponse<Unit>
    
    @POST("/api/auth/change-password")
    suspend fun changePassword(@Body request: ChangePasswordRequest): ApiResponse<Unit>
    
    @POST("/api/auth/unlock-account")
    suspend fun unlockAccount(): ApiResponse<Unit>
}
```

**AdminApi.kt**
```kotlin
interface AdminApi {
    @POST("/api/admin/register-employee")
    suspend fun registerEmployee(@Body request: RegisterEmployeeRequest): ApiResponse<RegisterEmployeeResponse>
    
    @GET("/api/admin/users")
    suspend fun getUsers(
        @Query("page") page: Int,
        @Query("role") role: String?,
        @Query("status") status: String?,
        @Query("search") search: String?
    ): ApiResponse<List<UserDto>>
    
    @GET("/api/admin/users/{id}")
    suspend fun getUserById(@Path("id") id: String): ApiResponse<UserDto>
    
    @PUT("/api/admin/users/{id}")
    suspend fun updateUser(@Path("id") id: String, @Body request: UpdateUserRequest): ApiResponse<UserDto>
    
    @PATCH("/api/admin/users/{id}/role")
    suspend fun changeUserRole(@Path("id") id: String, @Body request: ChangeRoleRequest): ApiResponse<UserDto>
    
    @PATCH("/api/admin/users/{id}/activate")
    suspend fun activateUser(@Path("id") id: String): ApiResponse<UserDto>
    
    @PATCH("/api/admin/users/{id}/deactivate")
    suspend fun deactivateUser(@Path("id") id: String): ApiResponse<UserDto>
    
    @GET("/api/admin/audit-logs")
    suspend fun getAuditLogs(@Query("page") page: Int): ApiResponse<List<AuditLogDto>>
    
    @GET("/api/admin/audit-logs/by-user")
    suspend fun getAuditLogsByUser(@Query("userId") userId: String, @Query("page") page: Int): ApiResponse<List<AuditLogDto>>
    
    @GET("/api/admin/audit-logs/by-event-type")
    suspend fun getAuditLogsByEventType(@Query("eventType") eventType: String, @Query("page") page: Int): ApiResponse<List<AuditLogDto>>
    
    @GET("/api/admin/audit-logs/by-date-range")
    suspend fun getAuditLogsByDateRange(
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String,
        @Query("page") page: Int
    ): ApiResponse<List<AuditLogDto>>
    
    @GET("/api/admin/audit-logs/failed")
    suspend fun getFailedAuditLogs(@Query("page") page: Int): ApiResponse<List<AuditLogDto>>
    
    @GET("/api/admin/audit-logs/critical")
    suspend fun getCriticalAuditLogs(@Query("page") page: Int): ApiResponse<List<AuditLogDto>>
    
    @GET("/api/admin/audit-logs/recent-by-user")
    suspend fun getRecentAuditLogsByUser(@Query("userId") userId: String): ApiResponse<List<AuditLogDto>>
    
    @GET("/api/admin/audit-logs/by-ip")
    suspend fun getAuditLogsByIp(@Query("ipAddress") ipAddress: String, @Query("page") page: Int): ApiResponse<List<AuditLogDto>>
}
```

#### TokenManager

**TokenManager.kt**
```kotlin
interface TokenManager {
    suspend fun saveTokens(accessToken: String, refreshToken: String)
    suspend fun getAccessToken(): String?
    suspend fun getRefreshToken(): String?
    suspend fun clearTokens()
}

class TokenManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : TokenManager {
    private val encryptedPrefs = EncryptedSharedPreferences.create(
        context,
        "auth_prefs",
        MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
    
    override suspend fun saveTokens(accessToken: String, refreshToken: String) {
        encryptedPrefs.edit()
            .putString(KEY_ACCESS_TOKEN, accessToken)
            .putString(KEY_REFRESH_TOKEN, refreshToken)
            .apply()
    }
    
    override suspend fun getAccessToken(): String? = 
        encryptedPrefs.getString(KEY_ACCESS_TOKEN, null)
    
    override suspend fun getRefreshToken(): String? = 
        encryptedPrefs.getString(KEY_REFRESH_TOKEN, null)
    
    override suspend fun clearTokens() {
        encryptedPrefs.edit().clear().apply()
    }
    
    companion object {
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
    }
}
```


#### Repository Implementations

**AuthRepositoryImpl.kt**
```kotlin
class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val tokenManager: TokenManager
) : AuthRepository {
    
    override suspend fun verify2FA(code: String): Result<AuthResponse> = try {
        val response = authApi.verify2FA(Verify2FARequest(code))
        if (response.error) {
            Result.failure(Exception(response.message.toString()))
        } else {
            val authResponse = response.message
            tokenManager.saveTokens(authResponse.accessToken, authResponse.refreshToken)
            Result.success(authResponse)
        }
    } catch (e: IOException) {
        Result.failure(Exception("Network error: ${e.message}"))
    } catch (e: HttpException) {
        Result.failure(Exception("Server error: ${e.code()} - ${e.message()}"))
    } catch (e: Exception) {
        Result.failure(Exception("Unexpected error: ${e.message}"))
    }
    
    override suspend fun refreshToken(): Result<AuthResponse> = try {
        val response = authApi.refreshToken()
        if (response.error) {
            Result.failure(Exception(response.message.toString()))
        } else {
            val authResponse = response.message
            tokenManager.saveTokens(authResponse.accessToken, authResponse.refreshToken)
            Result.success(authResponse)
        }
    } catch (e: Exception) {
        tokenManager.clearTokens()
        Result.failure(e)
    }
    
    override suspend fun logout(): Result<Unit> = try {
        val response = authApi.logout()
        tokenManager.clearTokens()
        if (response.error) {
            Result.failure(Exception(response.message.toString()))
        } else {
            Result.success(Unit)
        }
    } catch (e: Exception) {
        tokenManager.clearTokens()
        Result.failure(e)
    }
    
    override suspend fun getCurrentUser(): Result<User> = try {
        val response = authApi.getCurrentUser()
        if (response.error) {
            Result.failure(Exception(response.message.toString()))
        } else {
            Result.success(UserMapper.toDomain(response.message))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
    
    override suspend fun requestPasswordChange(): Result<Unit> = try {
        val response = authApi.requestPasswordChange()
        if (response.error) {
            Result.failure(Exception(response.message.toString()))
        } else {
            Result.success(Unit)
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
    
    override suspend fun changePassword(
        currentPassword: String,
        otp: String,
        newPassword: String
    ): Result<Unit> = try {
        val response = authApi.changePassword(
            ChangePasswordRequest(currentPassword, otp, newPassword)
        )
        if (response.error) {
            Result.failure(Exception(response.message.toString()))
        } else {
            tokenManager.clearTokens()
            Result.success(Unit)
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
    
    override suspend fun unlockAccount(): Result<Unit> = try {
        val response = authApi.unlockAccount()
        if (response.error) {
            Result.failure(Exception(response.message.toString()))
        } else {
            Result.success(Unit)
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}
```

### Domain Layer

#### Domain Models

**User.kt**
```kotlin
data class User(
    val id: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val role: UserRole,
    val roleDisplayName: String,
    val status: UserStatus,
    val statusDisplayName: String,
    val isEmailVerified: Boolean,
    val requiresPasswordChange: Boolean,
    val failedLoginAttempts: Int,
    val lockReason: String?,
    val lockedAt: String?,
    val createdAt: String,
    val updatedAt: String
)

enum class UserRole {
    ADMIN, KITCHEN, WAITER, CUSTOMER
}

enum class UserStatus {
    ACTIVE, INACTIVE, PENDING, BANNED
}
```

**AuditLog.kt**
```kotlin
data class AuditLog(
    val id: String,
    val userId: String?,
    val userEmail: String?,
    val eventType: String,
    val eventTypeName: String,
    val timestamp: String,
    val ipAddress: String?,
    val userAgent: String?,
    val details: String?,
    val success: Boolean,
    val errorMessage: String?,
    val critical: Boolean
)
```

#### Use Cases

Los Use Cases siguen el patrón establecido en el proyecto. Ejemplo:

**Verify2FAUseCase.kt**
```kotlin
class Verify2FAUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(code: String): Result<AuthResponse> {
        if (code.isBlank()) {
            return Result.failure(Exception("2FA code cannot be empty"))
        }
        return authRepository.verify2FA(code)
    }
}
```

**GetUsersUseCase.kt**
```kotlin
class GetUsersUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(
        page: Int,
        role: UserRole? = null,
        status: UserStatus? = null,
        search: String? = null
    ): Result<List<User>> {
        if (page < 0) {
            return Result.failure(Exception("Page number must be non-negative"))
        }
        return userRepository.getUsers(page, role, status, search)
    }
}
```


### Presentation Layer

#### UI State

**AuthUiState.kt**
```kotlin
data class Verify2FAUiState(
    val code: String = "",
    val codeError: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

data class ProfileUiState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

data class ChangePasswordUiState(
    val currentPassword: String = "",
    val currentPasswordError: String? = null,
    val otp: String = "",
    val otpError: String? = null,
    val newPassword: String = "",
    val newPasswordError: String? = null,
    val confirmPassword: String = "",
    val confirmPasswordError: String? = null,
    val isOtpRequested: Boolean = false,
    val isSubmitting: Boolean = false,
    val submitError: String? = null,
    val isSuccess: Boolean = false,
    val passwordStrength: PasswordStrength = PasswordStrength.WEAK
)

enum class PasswordStrength {
    WEAK, MEDIUM, STRONG
}
```

**UserUiState.kt**
```kotlin
data class UserListUiState(
    val users: List<User> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentPage: Int = 0,
    val hasMorePages: Boolean = true,
    val selectedRole: UserRole? = null,
    val selectedStatus: UserStatus? = null,
    val searchQuery: String = ""
)

data class UserDetailUiState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val showRoleDialog: Boolean = false,
    val showActivateDialog: Boolean = false,
    val showDeactivateDialog: Boolean = false,
    val recentActivity: List<AuditLog> = emptyList()
)

data class UserFormUiState(
    val user: User? = null,
    val firstName: String = "",
    val firstNameError: String? = null,
    val lastName: String = "",
    val lastNameError: String? = null,
    val email: String = "",
    val emailError: String? = null,
    val selectedRole: UserRole = UserRole.WAITER,
    val isSubmitting: Boolean = false,
    val submitError: String? = null,
    val isSuccess: Boolean = false,
    val temporaryPassword: String? = null
)
```

**AuditUiState.kt**
```kotlin
data class AuditLogListUiState(
    val logs: List<AuditLog> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentPage: Int = 0,
    val hasMorePages: Boolean = true,
    val filterUserId: String? = null,
    val filterEventType: String? = null,
    val filterStartDate: String? = null,
    val filterEndDate: String? = null,
    val filterIpAddress: String? = null,
    val showFailedOnly: Boolean = false,
    val showCriticalOnly: Boolean = false
)

data class AuditLogDetailUiState(
    val log: AuditLog? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
```

#### ViewModels

**AuthViewModel.kt**
```kotlin
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val verify2FAUseCase: Verify2FAUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val requestPasswordChangeUseCase: RequestPasswordChangeUseCase,
    private val changePasswordUseCase: ChangePasswordUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {
    
    private val _verify2FAState = MutableStateFlow(Verify2FAUiState())
    val verify2FAState: StateFlow<Verify2FAUiState> = _verify2FAState.asStateFlow()
    
    private val _profileState = MutableStateFlow(ProfileUiState())
    val profileState: StateFlow<ProfileUiState> = _profileState.asStateFlow()
    
    private val _changePasswordState = MutableStateFlow(ChangePasswordUiState())
    val changePasswordState: StateFlow<ChangePasswordUiState> = _changePasswordState.asStateFlow()
    
    fun onCodeChange(code: String) {
        _verify2FAState.update { it.copy(code = code, codeError = null) }
    }
    
    fun verify2FA(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _verify2FAState.update { it.copy(isLoading = true, error = null) }
            
            verify2FAUseCase(_verify2FAState.value.code).fold(
                onSuccess = {
                    _verify2FAState.update { it.copy(isLoading = false, isSuccess = true) }
                    onSuccess()
                },
                onFailure = { error ->
                    _verify2FAState.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Failed to verify 2FA code"
                        )
                    }
                }
            )
        }
    }
    
    fun loadCurrentUser() {
        viewModelScope.launch {
            _profileState.update { it.copy(isLoading = true, error = null) }
            
            getCurrentUserUseCase().fold(
                onSuccess = { user ->
                    _profileState.update { it.copy(user = user, isLoading = false) }
                },
                onFailure = { error ->
                    _profileState.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Failed to load profile"
                        )
                    }
                }
            )
        }
    }
    
    fun requestPasswordChange(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _changePasswordState.update { it.copy(isSubmitting = true, submitError = null) }
            
            requestPasswordChangeUseCase().fold(
                onSuccess = {
                    _changePasswordState.update {
                        it.copy(isSubmitting = false, isOtpRequested = true)
                    }
                    onSuccess()
                },
                onFailure = { error ->
                    _changePasswordState.update {
                        it.copy(
                            isSubmitting = false,
                            submitError = error.message ?: "Failed to request password change"
                        )
                    }
                }
            )
        }
    }
    
    fun changePassword(onSuccess: () -> Unit) {
        if (!validatePasswordForm()) return
        
        viewModelScope.launch {
            _changePasswordState.update { it.copy(isSubmitting = true, submitError = null) }
            
            val state = _changePasswordState.value
            changePasswordUseCase(
                state.currentPassword,
                state.otp,
                state.newPassword
            ).fold(
                onSuccess = {
                    _changePasswordState.update { it.copy(isSubmitting = false, isSuccess = true) }
                    onSuccess()
                },
                onFailure = { error ->
                    _changePasswordState.update {
                        it.copy(
                            isSubmitting = false,
                            submitError = error.message ?: "Failed to change password"
                        )
                    }
                }
            )
        }
    }
    
    private fun validatePasswordForm(): Boolean {
        val state = _changePasswordState.value
        var isValid = true
        
        if (state.currentPassword.isBlank()) {
            _changePasswordState.update { it.copy(currentPasswordError = "Current password is required") }
            isValid = false
        }
        
        if (state.otp.isBlank()) {
            _changePasswordState.update { it.copy(otpError = "OTP code is required") }
            isValid = false
        }
        
        if (!isPasswordValid(state.newPassword)) {
            _changePasswordState.update { it.copy(newPasswordError = "Password does not meet requirements") }
            isValid = false
        }
        
        if (state.newPassword != state.confirmPassword) {
            _changePasswordState.update { it.copy(confirmPasswordError = "Passwords do not match") }
            isValid = false
        }
        
        return isValid
    }
    
    private fun isPasswordValid(password: String): Boolean {
        return password.length >= 8 &&
               password.any { it.isUpperCase() } &&
               password.any { it.isLowerCase() } &&
               password.any { it.isDigit() }
    }
    
    fun logout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            logoutUseCase().fold(
                onSuccess = { onSuccess() },
                onFailure = { /* Log error but still navigate to login */ onSuccess() }
            )
        }
    }
}
```


#### Composable Screens

**Verify2FAScreen.kt**
```kotlin
@Composable
fun Verify2FAScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onSuccess: () -> Unit
) {
    val state by viewModel.verify2FAState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Verify 2FA Code") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            OutlinedTextField(
                value = state.code,
                onValueChange = viewModel::onCodeChange,
                label = { Text("2FA Code") },
                isError = state.codeError != null,
                supportingText = state.codeError?.let { { Text(it) } },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = { viewModel.verify2FA(onSuccess) },
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                } else {
                    Text("Verify")
                }
            }
            
            state.error?.let { error ->
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

**ProfileScreen.kt**
```kotlin
@Composable
fun ProfileScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onNavigateToChangePassword: () -> Unit,
    onLogout: () -> Unit
) {
    val state by viewModel.profileState.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.loadCurrentUser()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
                actions = {
                    IconButton(onClick = { viewModel.logout(onLogout) }) {
                        Icon(Icons.Default.Logout, "Logout")
                    }
                }
            )
        }
    ) { padding ->
        when {
            state.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            state.error != null -> {
                ErrorView(message = state.error!!)
            }
            state.user != null -> {
                ProfileContent(
                    user = state.user!!,
                    onChangePassword = onNavigateToChangePassword
                )
            }
        }
    }
}

@Composable
private fun ProfileContent(user: User, onChangePassword: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (user.requiresPasswordChange) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Password Change Required",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Text(
                        "You must change your temporary password",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = onChangePassword) {
                        Text("Change Password Now")
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        ProfileInfoCard(user)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = onChangePassword,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Change Password")
        }
    }
}
```

**UserListScreen.kt**
```kotlin
@Composable
fun UserListScreen(
    viewModel: UserViewModel = hiltViewModel(),
    onNavigateToDetail: (String) -> Unit,
    onNavigateToCreate: () -> Unit
) {
    val state by viewModel.listState.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.loadUsers()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Employee Management") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToCreate) {
                Icon(Icons.Default.Add, "Add Employee")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            SearchBar(
                query = state.searchQuery,
                onQueryChange = viewModel::onSearchQueryChange
            )
            
            FilterChips(
                selectedRole = state.selectedRole,
                selectedStatus = state.selectedStatus,
                onRoleSelected = viewModel::onRoleFilterChange,
                onStatusSelected = viewModel::onStatusFilterChange
            )
            
            when {
                state.isLoading && state.users.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                state.error != null && state.users.isEmpty() -> {
                    ErrorView(message = state.error!!)
                }
                state.users.isEmpty() -> {
                    EmptyView(message = "No users found")
                }
                else -> {
                    LazyColumn {
                        items(state.users) { user ->
                            UserListItem(
                                user = user,
                                onClick = { onNavigateToDetail(user.id) }
                            )
                        }
                        
                        if (state.hasMorePages) {
                            item {
                                LoadMoreButton(
                                    isLoading = state.isLoading,
                                    onClick = viewModel::loadNextPage
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
```


**AuditLogListScreen.kt**
```kotlin
@Composable
fun AuditLogListScreen(
    viewModel: AuditViewModel = hiltViewModel(),
    onNavigateToDetail: (String) -> Unit
) {
    val state by viewModel.listState.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.loadAuditLogs()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Audit Logs") },
                actions = {
                    IconButton(onClick = { viewModel.showFilterDialog() }) {
                        Icon(Icons.Default.FilterList, "Filters")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            ActiveFiltersChips(
                filterUserId = state.filterUserId,
                filterEventType = state.filterEventType,
                filterStartDate = state.filterStartDate,
                filterEndDate = state.filterEndDate,
                filterIpAddress = state.filterIpAddress,
                showFailedOnly = state.showFailedOnly,
                showCriticalOnly = state.showCriticalOnly,
                onClearFilter = viewModel::clearFilter
            )
            
            when {
                state.isLoading && state.logs.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                state.error != null && state.logs.isEmpty() -> {
                    ErrorView(message = state.error!!)
                }
                state.logs.isEmpty() -> {
                    EmptyView(message = "No audit logs found")
                }
                else -> {
                    LazyColumn {
                        items(state.logs) { log ->
                            AuditLogListItem(
                                log = log,
                                onClick = { onNavigateToDetail(log.id) }
                            )
                        }
                        
                        if (state.hasMorePages) {
                            item {
                                LoadMoreButton(
                                    isLoading = state.isLoading,
                                    onClick = viewModel::loadNextPage
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AuditLogListItem(log: AuditLog, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = when {
                log.critical -> MaterialTheme.colorScheme.errorContainer
                !log.success -> MaterialTheme.colorScheme.warningContainer
                else -> MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = log.eventTypeName,
                    style = MaterialTheme.typography.titleMedium
                )
                if (log.critical) {
                    Icon(
                        Icons.Default.Warning,
                        "Critical",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = log.userEmail ?: "Unknown user",
                style = MaterialTheme.typography.bodyMedium
            )
            
            Text(
                text = formatTimestamp(log.timestamp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            if (!log.success && log.errorMessage != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = log.errorMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
```

## Data Models

### DTOs (Data Transfer Objects)

**UserDto.kt**
```kotlin
data class UserDto(
    val id: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val role: String,
    val roleDisplayName: String,
    val status: String,
    val statusDisplayName: String,
    val isEmailVerified: Boolean,
    val requiresPasswordChange: Boolean,
    val failedLoginAttempts: Int,
    val lockReason: String?,
    val lockedAt: String?,
    val createdAt: String,
    val updatedAt: String
)
```

**AuditLogDto.kt**
```kotlin
data class AuditLogDto(
    val id: String,
    val userId: String?,
    val userEmail: String?,
    val eventType: String,
    val eventTypeName: String,
    val timestamp: String,
    val ipAddress: String?,
    val userAgent: String?,
    val details: String?,
    val success: Boolean,
    val errorMessage: String?,
    val critical: Boolean
)
```

**Request DTOs**
```kotlin
data class Verify2FARequest(val code: String)

data class AuthResponse(
    val accessToken: String,
    val refreshToken: String
)

data class ChangePasswordRequest(
    val currentPassword: String,
    val otp: String,
    val newPassword: String
)

data class RegisterEmployeeRequest(
    val firstName: String,
    val lastName: String,
    val email: String,
    val role: String
)

data class RegisterEmployeeResponse(
    val user: UserDto,
    val temporaryPassword: String
)

data class UpdateUserRequest(
    val firstName: String,
    val lastName: String,
    val email: String
)

data class ChangeRoleRequest(val role: String)
```

### Mappers

**UserMapper.kt**
```kotlin
object UserMapper {
    fun toDomain(dto: UserDto): User = User(
        id = dto.id,
        firstName = dto.firstName,
        lastName = dto.lastName,
        email = dto.email,
        role = UserRole.valueOf(dto.role),
        roleDisplayName = dto.roleDisplayName,
        status = UserStatus.valueOf(dto.status),
        statusDisplayName = dto.statusDisplayName,
        isEmailVerified = dto.isEmailVerified,
        requiresPasswordChange = dto.requiresPasswordChange,
        failedLoginAttempts = dto.failedLoginAttempts,
        lockReason = dto.lockReason,
        lockedAt = dto.lockedAt,
        createdAt = dto.createdAt,
        updatedAt = dto.updatedAt
    )
    
    fun toDto(domain: User): UserDto = UserDto(
        id = domain.id,
        firstName = domain.firstName,
        lastName = domain.lastName,
        email = domain.email,
        role = domain.role.name,
        roleDisplayName = domain.roleDisplayName,
        status = domain.status.name,
        statusDisplayName = domain.statusDisplayName,
        isEmailVerified = domain.isEmailVerified,
        requiresPasswordChange = domain.requiresPasswordChange,
        failedLoginAttempts = domain.failedLoginAttempts,
        lockReason = domain.lockReason,
        lockedAt = domain.lockedAt,
        createdAt = domain.createdAt,
        updatedAt = domain.updatedAt
    )
}
```


## Correctness Properties

*A property is a characteristic or behavior that should hold true across all valid executions of a system-essentially, a formal statement about what the system should do. Properties serve as the bridge between human-readable specifications and machine-verifiable correctness guarantees.*

### Property Reflection

Después de analizar todos los criterios de aceptación, se identificaron las siguientes redundancias y consolidaciones:

- **Filtros de Audit Logs**: Los criterios 16.2, 17.2, 18.2, 19.2, 20.2, 22.2 todos prueban que se hace la llamada API correcta con parámetros de filtro. Estos se consolidan en una propiedad general sobre filtros.
- **Validación de Email**: Los criterios 8.3 y 11.3 prueban lo mismo (validación de formato de email), se consolida en una sola propiedad.
- **Almacenamiento de Tokens**: Los criterios 1.3, 2.2 y 25.1 todos relacionados con almacenamiento seguro de tokens, se consolidan.
- **Limpieza de Tokens**: Los criterios 3.3 y 26.2 prueban limpieza de tokens en diferentes escenarios, se consolidan.
- **Control de Acceso por Rol**: Los criterios 8.8, 24.1, 24.3, 24.4 todos prueban RBAC, se consolidan en propiedades más generales.
- **Paginación**: Los criterios 9.6 y 15.3 prueban paginación con diferentes tamaños, se consolida en una propiedad general.
- **Validación de Contraseña**: Los criterios 28.1-28.4 prueban diferentes aspectos de validación de contraseña, se consolidan en una propiedad comprehensiva.

### Property 1: Token Storage Security

*For any* successful authentication response containing access and refresh tokens, the tokens SHALL be stored using encrypted storage (EncryptedSharedPreferences or Android Keystore) and SHALL NOT be stored in plain text.

**Validates: Requirements 1.3, 2.2, 25.1**

### Property 2: Token Refresh on 401

*For any* API request that returns 401 Unauthorized, the system SHALL attempt to refresh the access token using the refresh token before retrying the original request.

**Validates: Requirements 2.5**

### Property 3: Token Cleanup on Logout

*For any* logout operation (successful or failed), the system SHALL clear all stored tokens (access token and refresh token) from secure storage.

**Validates: Requirements 3.3, 26.2**

### Property 4: User Profile Display Completeness

*For any* user profile data returned from the API, the UI SHALL display all required fields: firstName, lastName, email, role, roleDisplayName, status, statusDisplayName, isEmailVerified, requiresPasswordChange, createdAt, and updatedAt.

**Validates: Requirements 4.2**

### Property 5: Password Confirmation Match

*For any* password change form submission, the system SHALL validate that the newPassword and confirmPassword fields match exactly before sending the request to the API.

**Validates: Requirements 6.2**

### Property 6: Password Strength Validation

*For any* new password input, the system SHALL validate that it meets ALL security requirements: minimum 8 characters, at least one uppercase letter, at least one lowercase letter, and at least one digit.

**Validates: Requirements 6.3, 28.1, 28.2, 28.3, 28.4**

### Property 7: Email Format Validation

*For any* email input in user registration or update forms, the system SHALL validate that the email follows a valid email format (contains @ symbol and domain) before submission.

**Validates: Requirements 8.3, 11.3**

### Property 8: Admin-Only Access Control

*For any* user with role other than ADMIN, the system SHALL NOT display navigation options or allow access to Employee Management and Audit Logs sections.

**Validates: Requirements 8.8, 24.1, 24.3, 24.4**

### Property 9: User List Display Completeness

*For any* user in the user list, the UI SHALL display all required fields: firstName, lastName, email, role, and status.

**Validates: Requirements 9.2**

### Property 10: Filter Parameters in API Calls

*For any* applied filter (role, status, search, eventType, dateRange, ipAddress, failedOnly, criticalOnly), the system SHALL include the corresponding query parameters in the API request.

**Validates: Requirements 9.5, 16.2, 17.2, 18.2, 19.2, 20.2, 22.2**

### Property 11: Pagination Consistency

*For any* paginated list (users with page size 10, audit logs with page size 20), the system SHALL request the correct page number and handle pagination state correctly (hasMorePages flag).

**Validates: Requirements 9.6, 15.3**

### Property 12: User Detail Display Completeness

*For any* user detail view, the UI SHALL display all user information fields including firstName, lastName, email, role, roleDisplayName, status, statusDisplayName, isEmailVerified, requiresPasswordChange, failedLoginAttempts, lockReason, lockedAt, createdAt, and updatedAt.

**Validates: Requirements 10.2**

### Property 13: Form Pre-population

*For any* user edit form, the form fields SHALL be pre-filled with the current user data (firstName, lastName, email).

**Validates: Requirements 11.1**

### Property 14: Audit Log Display Completeness

*For any* audit log in the list, the UI SHALL display all required fields: timestamp, userEmail, eventType, success status, and critical flag.

**Validates: Requirements 15.2**

### Property 15: Audit Log Sorting

*For any* list of audit logs, the logs SHALL be sorted by timestamp in descending order (most recent first).

**Validates: Requirements 15.4**

### Property 16: Recent Activity Limit

*For any* request for recent user activity, the system SHALL return exactly the 10 most recent audit logs for the specified user.

**Validates: Requirements 21.3**

### Property 17: Audit Log Detail Completeness

*For any* audit log detail view, the UI SHALL display all fields: id, userId, userEmail, eventType, timestamp, ipAddress, userAgent, details, success, errorMessage, and critical flag.

**Validates: Requirements 23.1**

### Property 18: Timestamp Formatting

*For any* timestamp displayed in the UI, the system SHALL format it in a human-readable format (e.g., "Jan 15, 2024 10:30 AM").

**Validates: Requirements 23.2**

### Property 19: Role Verification Before Navigation

*For any* navigation to a protected screen (Employee Management, Audit Logs), the system SHALL verify the user's role and only allow access if the role is ADMIN.

**Validates: Requirements 24.4**

### Property 20: Secure Token Retrieval

*For any* API request requiring authentication, the system SHALL retrieve the access token from encrypted storage and include it in the Authorization header.

**Validates: Requirements 25.4**

### Property 21: Deep Link Preservation

*For any* session expiration, the system SHALL preserve the current screen path to enable redirect back to that screen after re-authentication.

**Validates: Requirements 26.4**

### Property 22: Feature Restriction for Temporary Password

*For any* user with requiresPasswordChange flag set to true, the system SHALL restrict access to all features except the profile and password change screens until the password is changed.

**Validates: Requirements 27.2**

### Property 23: User Search Filtering

*For any* search query in the user list, the system SHALL filter users by matching the query against firstName, lastName, or email fields (case-insensitive).

**Validates: Requirements 30.2**

### Property 24: Combined Filters

*For any* combination of search query with role and status filters, the system SHALL apply all filters simultaneously and return only users matching all criteria.

**Validates: Requirements 30.5**

### Property 25: Date Range Validation

*For any* date range filter, the system SHALL validate that the end date is not before the start date before sending the API request.

**Validates: Requirements 18.4**


## Error Handling

### Error Categories

1. **Network Errors**: Connection failures, timeouts
2. **API Errors**: 4xx and 5xx HTTP status codes
3. **Validation Errors**: Client-side validation failures
4. **Authentication Errors**: Invalid tokens, expired sessions
5. **Authorization Errors**: Insufficient permissions
6. **Storage Errors**: Failed to save/retrieve tokens

### Error Handling Strategy

#### Repository Layer

Todos los métodos del Repository retornan `Result<T>` para encapsular éxito o fallo:

```kotlin
override suspend fun verify2FA(code: String): Result<AuthResponse> = try {
    val response = authApi.verify2FA(Verify2FARequest(code))
    if (response.error) {
        Result.failure(Exception(response.message.toString()))
    } else {
        Result.success(response.message)
    }
} catch (e: IOException) {
    Result.failure(Exception("Network error: ${e.message}"))
} catch (e: HttpException) {
    Result.failure(Exception("Server error: ${e.code()} - ${e.message()}"))
} catch (e: Exception) {
    Result.failure(Exception("Unexpected error: ${e.message}"))
}
```

#### Use Case Layer

Los Use Cases validan inputs y propagan errores del Repository:

```kotlin
suspend operator fun invoke(code: String): Result<AuthResponse> {
    if (code.isBlank()) {
        return Result.failure(Exception("2FA code cannot be empty"))
    }
    return authRepository.verify2FA(code)
}
```

#### ViewModel Layer

Los ViewModels manejan errores y actualizan el UI State:

```kotlin
verify2FAUseCase(code).fold(
    onSuccess = { response ->
        _state.update { it.copy(isLoading = false, isSuccess = true) }
    },
    onFailure = { error ->
        _state.update {
            it.copy(
                isLoading = false,
                error = error.message ?: "Failed to verify 2FA code"
            )
        }
    }
)
```

#### Presentation Layer

Los Composables muestran errores al usuario:

```kotlin
state.error?.let { error ->
    Text(
        text = error,
        color = MaterialTheme.colorScheme.error,
        style = MaterialTheme.typography.bodySmall
    )
}
```

### Specific Error Scenarios

#### Session Expiration

Cuando el Refresh Token expira:
1. AuthInterceptor detecta fallo en renovación
2. Limpia todos los tokens almacenados
3. Emite evento de sesión expirada
4. Guarda deep link de pantalla actual
5. Navega a pantalla de login
6. Muestra mensaje "Your session has expired. Please log in again."

#### Invalid 2FA Code

1. API retorna error con mensaje específico
2. ViewModel actualiza estado con error
3. UI muestra mensaje de error debajo del campo
4. Usuario puede reintentar sin recargar pantalla

#### Duplicate Email

1. API retorna error 409 Conflict
2. Repository convierte a Exception con mensaje
3. ViewModel actualiza formState con error
4. UI muestra error en el campo de email

#### Insufficient Permissions

1. ViewModel verifica rol antes de navegación
2. Si rol no es ADMIN, muestra diálogo de acceso denegado
3. No permite navegación a pantalla protegida

#### Network Timeout

1. Retrofit lanza IOException
2. Repository captura y convierte a mensaje amigable
3. ViewModel actualiza estado con error de red
4. UI muestra mensaje "Network error. Please check your connection."
5. Botón de "Retry" disponible para reintentar operación

### Retry Mechanism

Para operaciones fallidas por errores de red:

```kotlin
private var lastFailedOperation: (() -> Unit)? = null

fun loadUsers() {
    lastFailedOperation = { loadUsers() }
    // ... perform operation
}

fun retryLastOperation() {
    lastFailedOperation?.invoke()
}
```

## Testing Strategy

### Dual Testing Approach

El módulo implementará dos tipos complementarios de testing:

1. **Unit Tests**: Verifican ejemplos específicos, casos edge y condiciones de error
2. **Property-Based Tests**: Verifican propiedades universales a través de múltiples inputs generados

Ambos tipos son necesarios para cobertura comprehensiva:
- Unit tests capturan bugs concretos y casos específicos
- Property tests verifican corrección general a través de randomización

### Property-Based Testing Configuration

**Librería**: Kotest Property Testing (https://kotest.io/docs/proptest/property-based-testing.html)

**Configuración**:
- Mínimo 100 iteraciones por test de propiedad
- Cada test debe referenciar su propiedad del documento de diseño
- Formato de tag: `// Feature: authentication-module, Property {number}: {property_text}`

**Ejemplo de Property Test**:

```kotlin
class PasswordValidationPropertyTest : StringSpec({
    "Property 6: Password strength validation" {
        // Feature: authentication-module, Property 6: Password strength validation
        checkAll(100, Arb.string()) { password ->
            val isValid = PasswordValidator.isValid(password)
            val meetsRequirements = password.length >= 8 &&
                password.any { it.isUpperCase() } &&
                password.any { it.isLowerCase() } &&
                password.any { it.isDigit() }
            
            isValid shouldBe meetsRequirements
        }
    }
})
```

### Unit Testing Strategy

#### Repository Tests

Verificar:
- Llamadas API correctas con parámetros esperados
- Manejo de respuestas exitosas
- Manejo de errores de red (IOException)
- Manejo de errores HTTP (HttpException)
- Almacenamiento de tokens después de autenticación exitosa
- Limpieza de tokens en logout

**Ejemplo**:
```kotlin
@Test
fun `verify2FA stores tokens on success`() = runTest {
    val authResponse = AuthResponse("access123", "refresh456")
    coEvery { authApi.verify2FA(any()) } returns ApiResponse(
        error = false,
        message = authResponse
    )
    
    val result = repository.verify2FA("123456")
    
    assertTrue(result.isSuccess)
    coVerify { tokenManager.saveTokens("access123", "refresh456") }
}
```

#### Use Case Tests

Verificar:
- Validación de inputs
- Llamadas correctas al Repository
- Propagación de errores

**Ejemplo**:
```kotlin
@Test
fun `Verify2FAUseCase rejects blank code`() = runTest {
    val result = verify2FAUseCase("")
    
    assertTrue(result.isFailure)
    assertEquals("2FA code cannot be empty", result.exceptionOrNull()?.message)
}
```

#### ViewModel Tests

Verificar:
- Actualización correcta de UI State
- Manejo de loading states
- Manejo de errores
- Validación de formularios
- Navegación en callbacks de éxito

**Ejemplo**:
```kotlin
@Test
fun `verify2FA updates state on success`() = runTest {
    coEvery { verify2FAUseCase(any()) } returns Result.success(mockAuthResponse)
    
    viewModel.verify2FA { }
    advanceUntilIdle()
    
    val state = viewModel.verify2FAState.value
    assertFalse(state.isLoading)
    assertTrue(state.isSuccess)
    assertNull(state.error)
}
```

#### Mapper Tests

Verificar:
- Conversión correcta de DTO a Domain
- Conversión correcta de Domain a DTO
- Manejo de campos opcionales/nullable

**Ejemplo**:
```kotlin
@Test
fun `UserMapper converts DTO to Domain correctly`() {
    val dto = UserDto(
        id = "1",
        firstName = "John",
        lastName = "Doe",
        email = "john@example.com",
        role = "ADMIN",
        // ... otros campos
    )
    
    val domain = UserMapper.toDomain(dto)
    
    assertEquals("1", domain.id)
    assertEquals("John", domain.firstName)
    assertEquals(UserRole.ADMIN, domain.role)
}
```

#### UI Tests (Composable)

Verificar:
- Renderizado correcto de componentes
- Interacciones de usuario (clicks, input)
- Navegación
- Visualización de errores
- Estados de loading

**Ejemplo**:
```kotlin
@Test
fun `Verify2FAScreen displays error message`() {
    composeTestRule.setContent {
        val viewModel = mockk<AuthViewModel>()
        every { viewModel.verify2FAState } returns MutableStateFlow(
            Verify2FAUiState(error = "Invalid code")
        )
        
        Verify2FAScreen(viewModel = viewModel, onSuccess = {})
    }
    
    composeTestRule.onNodeWithText("Invalid code").assertIsDisplayed()
}
```

### Edge Cases to Test

1. **Empty/Whitespace Inputs**: Código 2FA vacío, contraseñas vacías, búsquedas vacías
2. **Invalid Formats**: Emails inválidos, fechas inválidas
3. **Boundary Values**: Contraseñas de exactamente 8 caracteres, página 0, última página
4. **Network Failures**: Timeout, sin conexión, respuestas malformadas
5. **Expired Tokens**: Access token expirado, refresh token expirado
6. **Duplicate Data**: Email duplicado en registro/actualización
7. **Locked Accounts**: Usuario bloqueado intentando acciones
8. **Role Restrictions**: Usuario no-admin intentando acceder a funciones admin
9. **Concurrent Operations**: Múltiples llamadas simultáneas
10. **Large Datasets**: Listas con muchos usuarios/logs

### Test Coverage Goals

- **Repository**: 90%+ cobertura de líneas
- **Use Cases**: 95%+ cobertura de líneas
- **ViewModels**: 85%+ cobertura de líneas
- **Mappers**: 100% cobertura de líneas
- **Property Tests**: Todas las 25 propiedades implementadas

### Continuous Integration

Los tests se ejecutarán automáticamente en CI/CD:
- Unit tests en cada commit
- Property tests en cada PR
- UI tests en builds de release
- Reporte de cobertura generado automáticamente


## Navigation Structure

### Navigation Graph

```kotlin
sealed class AuthRoute(val route: String) {
    object Verify2FA : AuthRoute("verify_2fa")
    object Profile : AuthRoute("profile")
    object ChangePassword : AuthRoute("change_password")
}

sealed class UserRoute(val route: String) {
    object UserList : AuthRoute("user_list")
    object UserDetail : AuthRoute("user_detail/{userId}") {
        fun createRoute(userId: String) = "user_detail/$userId"
    }
    object UserForm : AuthRoute("user_form?userId={userId}") {
        fun createRoute(userId: String? = null) = 
            if (userId != null) "user_form?userId=$userId" else "user_form"
    }
}

sealed class AuditRoute(val route: String) {
    object AuditLogList : AuthRoute("audit_log_list")
    object AuditLogDetail : AuthRoute("audit_log_detail/{logId}") {
        fun createRoute(logId: String) = "audit_log_detail/$logId"
    }
}
```

### Navigation Implementation

```kotlin
@Composable
fun AuthNavGraph(
    navController: NavHostController,
    startDestination: String = AuthRoute.Profile.route
) {
    NavHost(navController = navController, startDestination = startDestination) {
        comp