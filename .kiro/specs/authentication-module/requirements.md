# Requirements Document

## Introduction

Este documento especifica los requisitos para el módulo de autenticación del frontend móvil Android de Smart Restaurant. La aplicación permitirá al personal del restaurante gestionar la autenticación, perfiles de usuario, administración de empleados y auditoría de seguridad mediante una interfaz nativa construida con Kotlin y Jetpack Compose que consume una API REST existente en Spring Boot.

El módulo NO incluye funcionalidades de registro público, login público, verificación de email ni recuperación de contraseña pública, ya que estas están implementadas únicamente en el backend. El enfoque está en la gestión interna de empleados y seguridad.

## Glossary

- **Mobile_App**: Aplicación móvil Android nativa construida con Kotlin y Jetpack Compose
- **Backend_API**: API REST en Spring Boot que proporciona los servicios de autenticación y administración
- **User**: Usuario del sistema con rol específico (ADMIN, KITCHEN, WAITER, CUSTOMER)
- **Access_Token**: Token JWT de corta duración para autenticar solicitudes API
- **Refresh_Token**: Token JWT de larga duración para renovar el Access_Token
- **Two_Factor_Code**: Código de verificación 2FA enviado al usuario para autenticación adicional
- **OTP**: One-Time Password usado para cambio de contraseña
- **Audit_Log**: Registro de eventos de seguridad y acciones importantes del sistema
- **Role**: Rol del usuario que determina permisos (ADMIN, KITCHEN, WAITER, CUSTOMER)
- **User_Status**: Estado del usuario (ACTIVE, INACTIVE, PENDING, BANNED)
- **Session**: Sesión activa de usuario autenticado con tokens válidos
- **Employee**: Usuario interno del restaurante registrado por un administrador
- **Temporary_Password**: Contraseña temporal asignada a empleados nuevos que requiere cambio obligatorio

## Requirements

### Requirement 1: Verificación de Código 2FA

**User Story:** Como usuario del sistema, quiero verificar mi código 2FA después del login, para completar la autenticación de dos factores.

#### Acceptance Criteria

1. WHEN the user receives a 2FA code, THE Mobile_App SHALL display a form to enter the verification code
2. WHEN the user submits a valid 2FA code, THE Mobile_App SHALL send a POST request to `/api/auth/verify-2fa` with the code
3. WHEN the Backend_API validates the 2FA code successfully, THE Mobile_App SHALL store the Access_Token and Refresh_Token
4. WHEN 2FA verification succeeds, THE Mobile_App SHALL navigate to the main application screen
5. IF the 2FA code is invalid or expired, THEN THE Mobile_App SHALL display the error message and allow retry
6. THE Mobile_App SHALL provide an option to resend the 2FA code

### Requirement 2: Renovación de Token de Acceso

**User Story:** Como usuario autenticado, quiero que mi sesión se renueve automáticamente, para no tener que iniciar sesión repetidamente.

#### Acceptance Criteria

1. WHEN the Access_Token expires or is about to expire, THE Mobile_App SHALL automatically send a POST request to `/api/auth/refresh-token` with the Refresh_Token
2. WHEN the Backend_API returns a new Access_Token, THE Mobile_App SHALL store it and continue the user session
3. IF the Refresh_Token is invalid or expired, THEN THE Mobile_App SHALL clear stored tokens and redirect to login screen
4. THE Mobile_App SHALL implement token refresh logic transparently without user interaction
5. WHEN any API request returns 401 Unauthorized, THE Mobile_App SHALL attempt token refresh before retrying the request

### Requirement 3: Cierre de Sesión

**User Story:** Como usuario autenticado, quiero cerrar sesión de forma segura, para proteger mi cuenta cuando no uso la aplicación.

#### Acceptance Criteria

1. THE Mobile_App SHALL provide a logout option in the user profile or settings menu
2. WHEN the user requests logout, THE Mobile_App SHALL send a POST request to `/api/auth/logout` with the current Access_Token
3. WHEN logout is successful, THE Mobile_App SHALL clear all stored tokens from secure storage
4. WHEN logout is successful, THE Mobile_App SHALL navigate to the login screen
5. THE Mobile_App SHALL clear any cached user data after logout

### Requirement 4: Obtener Información del Usuario Actual

**User Story:** Como usuario autenticado, quiero ver mi información de perfil, para verificar mis datos personales y rol en el sistema.

#### Acceptance Criteria

1. WHEN the user accesses their profile, THE Mobile_App SHALL send a GET request to `/api/auth/me` with the Access_Token
2. WHEN the Backend_API returns user information, THE Mobile_App SHALL display firstName, lastName, email, role, roleDisplayName, status, and statusDisplayName
3. THE Mobile_App SHALL display if the user requires password change
4. THE Mobile_App SHALL display if the user's email is verified
5. THE Mobile_App SHALL display account creation date and last update date

### Requirement 5: Solicitar Cambio de Contraseña

**User Story:** Como usuario autenticado, quiero solicitar un cambio de contraseña, para recibir un OTP que me permita establecer una nueva contraseña.

#### Acceptance Criteria

1. THE Mobile_App SHALL provide a "Change Password" option in the user profile
2. WHEN the user requests password change, THE Mobile_App SHALL send a POST request to `/api/auth/request-password-change` with the Access_Token
3. WHEN the Backend_API sends the OTP successfully, THE Mobile_App SHALL display a confirmation message indicating the OTP was sent to the user's email
4. THE Mobile_App SHALL navigate to the password change form after OTP is sent
5. IF the request fails, THEN THE Mobile_App SHALL display the error message

### Requirement 6: Cambiar Contraseña con OTP

**User Story:** Como usuario que solicitó cambio de contraseña, quiero ingresar mi contraseña actual, el OTP recibido y mi nueva contraseña, para actualizar mis credenciales de forma segura.

#### Acceptance Criteria

1. THE Mobile_App SHALL display a form with fields for current password, OTP code, new password, and confirm new password
2. THE Mobile_App SHALL validate that new password and confirm password match before submission
3. THE Mobile_App SHALL validate that new password meets minimum security requirements
4. WHEN the user submits the form, THE Mobile_App SHALL send a POST request to `/api/auth/change-password` with currentPassword, otp, and newPassword
5. WHEN password change is successful, THE Mobile_App SHALL display a success message and navigate to login screen
6. IF the OTP is invalid or expired, THEN THE Mobile_App SHALL display the error message and allow retry
7. IF the current password is incorrect, THEN THE Mobile_App SHALL display the error message

### Requirement 7: Desbloquear Cuenta

**User Story:** Como usuario con cuenta bloqueada, quiero desbloquear mi cuenta, para recuperar el acceso al sistema.

#### Acceptance Criteria

1. WHEN a user account is locked, THE Mobile_App SHALL display information about the lock reason
2. THE Mobile_App SHALL provide an "Unlock Account" option for locked users
3. WHEN the user requests account unlock, THE Mobile_App SHALL send a POST request to `/api/auth/unlock-account`
4. WHEN unlock is successful, THE Mobile_App SHALL display a confirmation message
5. IF unlock fails, THEN THE Mobile_App SHALL display the error message with instructions

### Requirement 8: Registrar Nuevo Empleado (Solo ADMIN)

**User Story:** Como administrador, quiero registrar nuevos empleados en el sistema, para darles acceso a la aplicación con un rol específico.

#### Acceptance Criteria

1. WHEN a user with ADMIN role accesses employee management, THE Mobile_App SHALL provide an option to register new employee
2. THE Mobile_App SHALL display a form with fields for firstName, lastName, email, and role selection
3. THE Mobile_App SHALL validate email format before submission
4. WHEN the admin submits the form, THE Mobile_App SHALL send a POST request to `/api/admin/register-employee` with the employee data
5. WHEN registration is successful, THE Mobile_App SHALL display the temporary password generated for the new employee
6. THE Mobile_App SHALL inform the admin that the employee must change the temporary password on first login
7. IF the email already exists, THEN THE Mobile_App SHALL display the error message
8. IF the user role is not ADMIN, THEN THE Mobile_App SHALL not display the employee registration option

### Requirement 9: Listar Usuarios con Filtros (Solo ADMIN)

**User Story:** Como administrador, quiero ver la lista de usuarios del sistema con opciones de filtrado, para gestionar las cuentas de empleados.

#### Acceptance Criteria

1. WHEN a user with ADMIN role accesses user management, THE Mobile_App SHALL send a GET request to `/api/admin/users`
2. THE Mobile_App SHALL display a paginated list of users showing firstName, lastName, email, role, and status
3. THE Mobile_App SHALL provide filter options for role (ADMIN, KITCHEN, WAITER, CUSTOMER)
4. THE Mobile_App SHALL provide filter options for status (ACTIVE, INACTIVE, PENDING, BANNED)
5. WHEN filters are applied, THE Mobile_App SHALL send the filter parameters as query strings to the Backend_API
6. THE Mobile_App SHALL implement pagination with page size of 10 users
7. IF the user role is not ADMIN, THEN THE Mobile_App SHALL not display the user management section

### Requirement 10: Obtener Usuario por ID (Solo ADMIN)

**User Story:** Como administrador, quiero ver los detalles completos de un usuario específico, para revisar su información y estado.

#### Acceptance Criteria

1. WHEN an admin selects a user from the list, THE Mobile_App SHALL send a GET request to `/api/admin/users/{id}`
2. THE Mobile_App SHALL display all user information including firstName, lastName, email, role, roleDisplayName, status, statusDisplayName, isEmailVerified, requiresPasswordChange, failedLoginAttempts, lockReason, lockedAt, createdAt, and updatedAt
3. THE Mobile_App SHALL provide options to edit, change role, activate, or deactivate the user from the detail view
4. IF the user is locked, THE Mobile_App SHALL display the lock reason and locked timestamp

### Requirement 11: Actualizar Usuario (Solo ADMIN)

**User Story:** Como administrador, quiero actualizar la información de un usuario, para mantener los datos actualizados.

#### Acceptance Criteria

1. WHEN an admin requests to edit a user, THE Mobile_App SHALL display a form pre-filled with current user data
2. THE Mobile_App SHALL allow editing of firstName, lastName, and email fields
3. THE Mobile_App SHALL validate email format before submission
4. WHEN the admin submits the form, THE Mobile_App SHALL send a PUT request to `/api/admin/users/{id}` with updated data
5. WHEN update is successful, THE Mobile_App SHALL refresh the user detail view with updated information
6. IF the email already exists for another user, THEN THE Mobile_App SHALL display the error message

### Requirement 12: Cambiar Rol de Usuario (Solo ADMIN)

**User Story:** Como administrador, quiero cambiar el rol de un usuario, para ajustar sus permisos en el sistema.

#### Acceptance Criteria

1. WHEN an admin views user details, THE Mobile_App SHALL provide a "Change Role" option
2. THE Mobile_App SHALL display a dialog with role options (ADMIN, KITCHEN, WAITER, CUSTOMER)
3. WHEN the admin selects a new role, THE Mobile_App SHALL send a PATCH request to `/api/admin/users/{id}/role` with the new role
4. WHEN role change is successful, THE Mobile_App SHALL refresh the user detail view showing the updated role
5. THE Mobile_App SHALL display a confirmation dialog before changing the role
6. IF role change fails, THEN THE Mobile_App SHALL display the error message

### Requirement 13: Desactivar Usuario (Solo ADMIN)

**User Story:** Como administrador, quiero desactivar una cuenta de usuario, para revocar temporalmente el acceso sin eliminar la cuenta.

#### Acceptance Criteria

1. WHEN an admin views an active user's details, THE Mobile_App SHALL provide a "Deactivate" option
2. THE Mobile_App SHALL display a confirmation dialog before deactivation
3. WHEN the admin confirms, THE Mobile_App SHALL send a PATCH request to `/api/admin/users/{id}/deactivate`
4. WHEN deactivation is successful, THE Mobile_App SHALL refresh the user detail view showing status as INACTIVE
5. THE Mobile_App SHALL display a success message after deactivation
6. IF deactivation fails, THEN THE Mobile_App SHALL display the error message

### Requirement 14: Activar Usuario (Solo ADMIN)

**User Story:** Como administrador, quiero activar una cuenta de usuario desactivada, para restaurar el acceso al sistema.

#### Acceptance Criteria

1. WHEN an admin views an inactive user's details, THE Mobile_App SHALL provide an "Activate" option
2. THE Mobile_App SHALL display a confirmation dialog before activation
3. WHEN the admin confirms, THE Mobile_App SHALL send a PATCH request to `/api/admin/users/{id}/activate`
4. WHEN activation is successful, THE Mobile_App SHALL refresh the user detail view showing status as ACTIVE
5. THE Mobile_App SHALL display a success message after activation
6. IF activation fails, THEN THE Mobile_App SHALL display the error message

### Requirement 15: Ver Todos los Logs de Auditoría (Solo ADMIN)

**User Story:** Como administrador, quiero ver todos los logs de auditoría del sistema, para monitorear la actividad y detectar problemas de seguridad.

#### Acceptance Criteria

1. WHEN a user with ADMIN role accesses audit logs, THE Mobile_App SHALL send a GET request to `/api/admin/audit-logs`
2. THE Mobile_App SHALL display a paginated list of audit logs showing timestamp, userEmail, eventType, success status, and critical flag
3. THE Mobile_App SHALL implement pagination with page size of 20 logs
4. THE Mobile_App SHALL display logs sorted by timestamp in descending order
5. THE Mobile_App SHALL visually differentiate between successful and failed events
6. THE Mobile_App SHALL highlight critical events with a distinct visual indicator
7. IF the user role is not ADMIN, THEN THE Mobile_App SHALL not display the audit logs section

### Requirement 16: Ver Logs por Usuario (Solo ADMIN)

**User Story:** Como administrador, quiero filtrar logs de auditoría por usuario específico, para revisar la actividad de un empleado.

#### Acceptance Criteria

1. THE Mobile_App SHALL provide a filter option to search logs by user email or ID
2. WHEN the admin applies a user filter, THE Mobile_App SHALL send a GET request to `/api/admin/audit-logs/by-user` with userId parameter
3. THE Mobile_App SHALL display filtered logs showing only events for the specified user
4. THE Mobile_App SHALL maintain pagination for filtered results

### Requirement 17: Ver Logs por Tipo de Evento (Solo ADMIN)

**User Story:** Como administrador, quiero filtrar logs por tipo de evento, para analizar eventos específicos como intentos de login fallidos.

#### Acceptance Criteria

1. THE Mobile_App SHALL provide a filter option to select event types (LOGIN_SUCCESS, LOGIN_FAILED, LOGOUT, USER_REGISTERED, USER_UPDATED, PASSWORD_CHANGED, ROLE_CHANGED, USER_ACTIVATED, USER_DEACTIVATED, etc.)
2. WHEN the admin applies an event type filter, THE Mobile_App SHALL send a GET request to `/api/admin/audit-logs/by-event-type` with eventType parameter
3. THE Mobile_App SHALL display filtered logs showing only the selected event type
4. THE Mobile_App SHALL maintain pagination for filtered results

### Requirement 18: Ver Logs por Rango de Fechas (Solo ADMIN)

**User Story:** Como administrador, quiero filtrar logs por rango de fechas, para analizar actividad en períodos específicos.

#### Acceptance Criteria

1. THE Mobile_App SHALL provide date picker controls to select start date and end date
2. WHEN the admin applies a date range filter, THE Mobile_App SHALL send a GET request to `/api/admin/audit-logs/by-date-range` with startDate and endDate parameters
3. THE Mobile_App SHALL display filtered logs within the specified date range
4. THE Mobile_App SHALL validate that end date is not before start date
5. THE Mobile_App SHALL maintain pagination for filtered results

### Requirement 19: Ver Logs de Eventos Fallidos (Solo ADMIN)

**User Story:** Como administrador, quiero ver solo los eventos fallidos, para identificar rápidamente problemas de seguridad o errores.

#### Acceptance Criteria

1. THE Mobile_App SHALL provide a quick filter option for "Failed Events Only"
2. WHEN the admin activates this filter, THE Mobile_App SHALL send a GET request to `/api/admin/audit-logs/failed`
3. THE Mobile_App SHALL display only logs where success is false
4. THE Mobile_App SHALL display the error message for each failed event
5. THE Mobile_App SHALL maintain pagination for filtered results

### Requirement 20: Ver Logs Críticos (Solo ADMIN)

**User Story:** Como administrador, quiero ver solo los eventos críticos, para priorizar la revisión de incidentes importantes.

#### Acceptance Criteria

1. THE Mobile_App SHALL provide a quick filter option for "Critical Events Only"
2. WHEN the admin activates this filter, THE Mobile_App SHALL send a GET request to `/api/admin/audit-logs/critical`
3. THE Mobile_App SHALL display only logs where critical is true
4. THE Mobile_App SHALL highlight critical events prominently
5. THE Mobile_App SHALL maintain pagination for filtered results

### Requirement 21: Ver Últimos Logs de Usuario (Solo ADMIN)

**User Story:** Como administrador, quiero ver rápidamente los últimos 10 eventos de un usuario, para revisar su actividad reciente.

#### Acceptance Criteria

1. WHEN viewing user details, THE Mobile_App SHALL provide an option to view recent activity
2. WHEN the admin requests recent activity, THE Mobile_App SHALL send a GET request to `/api/admin/audit-logs/recent-by-user` with userId parameter
3. THE Mobile_App SHALL display the 10 most recent logs for the specified user
4. THE Mobile_App SHALL display logs sorted by timestamp in descending order

### Requirement 22: Ver Logs por Dirección IP (Solo ADMIN)

**User Story:** Como administrador, quiero filtrar logs por dirección IP, para detectar accesos sospechosos desde ubicaciones específicas.

#### Acceptance Criteria

1. THE Mobile_App SHALL provide a filter option to search logs by IP address
2. WHEN the admin applies an IP filter, THE Mobile_App SHALL send a GET request to `/api/admin/audit-logs/by-ip` with ipAddress parameter
3. THE Mobile_App SHALL display filtered logs showing only events from the specified IP address
4. THE Mobile_App SHALL maintain pagination for filtered results

### Requirement 23: Detalle de Log de Auditoría (Solo ADMIN)

**User Story:** Como administrador, quiero ver los detalles completos de un log de auditoría, para analizar información específica del evento.

#### Acceptance Criteria

1. WHEN an admin selects a log from the list, THE Mobile_App SHALL display detailed information including id, userId, userEmail, eventType, timestamp, ipAddress, userAgent, details, success, errorMessage, and critical flag
2. THE Mobile_App SHALL format the timestamp in a readable format
3. THE Mobile_App SHALL display the complete details field content
4. IF the event has an error message, THE Mobile_App SHALL display it prominently

### Requirement 24: Protección de Rutas por Rol

**User Story:** Como desarrollador del sistema, quiero que las rutas estén protegidas por rol, para garantizar que solo usuarios autorizados accedan a funcionalidades específicas.

#### Acceptance Criteria

1. THE Mobile_App SHALL verify user role before displaying admin-only screens
2. WHEN a non-admin user attempts to access admin functionality, THE Mobile_App SHALL display an access denied message
3. THE Mobile_App SHALL hide navigation options for admin features from non-admin users
4. THE Mobile_App SHALL validate role on every navigation to protected screens
5. THE Mobile_App SHALL implement role-based access control for Employee Management and Audit Logs sections

### Requirement 25: Almacenamiento Seguro de Tokens

**User Story:** Como desarrollador del sistema, quiero almacenar tokens de forma segura, para proteger las credenciales de los usuarios.

#### Acceptance Criteria

1. THE Mobile_App SHALL store Access_Token and Refresh_Token using Android Keystore or EncryptedSharedPreferences
2. THE Mobile_App SHALL never store tokens in plain text
3. THE Mobile_App SHALL clear tokens from memory after logout
4. THE Mobile_App SHALL implement secure token retrieval for API requests
5. THE Mobile_App SHALL handle token storage errors gracefully

### Requirement 26: Manejo de Sesión Expirada

**User Story:** Como usuario autenticado, quiero recibir notificación clara cuando mi sesión expire, para entender por qué debo iniciar sesión nuevamente.

#### Acceptance Criteria

1. WHEN the Backend_API returns 401 Unauthorized and token refresh fails, THE Mobile_App SHALL display a session expired message
2. THE Mobile_App SHALL clear all stored tokens when session expires
3. THE Mobile_App SHALL redirect to login screen after displaying the session expired message
4. THE Mobile_App SHALL preserve the current screen path to redirect back after re-authentication

### Requirement 27: Indicadores de Contraseña Temporal

**User Story:** Como empleado nuevo con contraseña temporal, quiero ser notificado que debo cambiar mi contraseña, para establecer credenciales personales.

#### Acceptance Criteria

1. WHEN a user with requiresPasswordChange flag logs in, THE Mobile_App SHALL display a prominent notification
2. THE Mobile_App SHALL restrict access to other features until password is changed
3. THE Mobile_App SHALL provide a direct link to the password change flow
4. WHEN password change is completed, THE Mobile_App SHALL remove the restriction and allow full access

### Requirement 28: Validación de Contraseñas

**User Story:** Como usuario que cambia contraseña, quiero que el sistema valide la fortaleza de mi nueva contraseña, para garantizar seguridad adecuada.

#### Acceptance Criteria

1. THE Mobile_App SHALL validate that new password has minimum 8 characters
2. THE Mobile_App SHALL validate that new password contains at least one uppercase letter
3. THE Mobile_App SHALL validate that new password contains at least one lowercase letter
4. THE Mobile_App SHALL validate that new password contains at least one number
5. THE Mobile_App SHALL display password strength indicator in real-time
6. THE Mobile_App SHALL display specific validation errors for each unmet requirement

### Requirement 29: Navegación del Módulo de Autenticación

**User Story:** Como usuario de la aplicación, quiero navegar intuitivamente entre las pantallas de autenticación y perfil, para gestionar mi cuenta fácilmente.

#### Acceptance Criteria

1. THE Mobile_App SHALL implement navigation to Profile screen from main menu
2. THE Mobile_App SHALL implement navigation to Change Password screen from Profile
3. THE Mobile_App SHALL implement navigation to Employee Management screen for ADMIN users
4. THE Mobile_App SHALL implement navigation to Audit Logs screen for ADMIN users
5. THE Mobile_App SHALL implement back navigation maintaining proper navigation stack
6. THE Mobile_App SHALL follow Material Design 3 navigation patterns

### Requirement 30: Búsqueda y Filtrado de Usuarios

**User Story:** Como administrador, quiero buscar usuarios por nombre o email, para encontrar rápidamente cuentas específicas.

#### Acceptance Criteria

1. THE Mobile_App SHALL provide a search bar in the user list screen
2. WHEN the admin types in the search bar, THE Mobile_App SHALL filter users by firstName, lastName, or email
3. THE Mobile_App SHALL implement search with debounce to avoid excessive API calls
4. THE Mobile_App SHALL display "No results found" message when search returns empty
5. THE Mobile_App SHALL allow combining search with role and status filters
