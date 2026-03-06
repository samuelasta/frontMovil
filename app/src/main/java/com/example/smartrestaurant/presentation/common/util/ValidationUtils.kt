package com.example.smartrestaurant.presentation.common.util

object ValidationUtils {
    
    /**
     * Validates that a field is not empty
     * @return Error message if validation fails, null if valid
     */
    fun validateRequired(value: String, fieldName: String = "Campo"): String? {
        return if (value.isBlank()) {
            "$fieldName es requerido"
        } else {
            null
        }
    }
    
    /**
     * Validates email format
     * @return Error message if validation fails, null if valid
     */
    fun validateEmail(email: String): String? {
        if (email.isBlank()) {
            return "Email es requerido"
        }
        
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        return if (!email.matches(emailRegex)) {
            "Email inválido"
        } else {
            null
        }
    }
    
    /**
     * Validates that a number is positive (> 0)
     * @return Error message if validation fails, null if valid
     */
    fun validatePositiveNumber(value: String, fieldName: String = "Valor"): String? {
        if (value.isBlank()) {
            return "$fieldName es requerido"
        }
        
        val number = value.toDoubleOrNull()
        return when {
            number == null -> "$fieldName debe ser un número válido"
            number <= 0 -> "$fieldName debe ser mayor a 0"
            else -> null
        }
    }
    
    /**
     * Validates that a number is non-negative (>= 0)
     * @return Error message if validation fails, null if valid
     */
    fun validateNonNegativeNumber(value: String, fieldName: String = "Valor"): String? {
        if (value.isBlank()) {
            return "$fieldName es requerido"
        }
        
        val number = value.toDoubleOrNull()
        return when {
            number == null -> "$fieldName debe ser un número válido"
            number < 0 -> "$fieldName no puede ser negativo"
            else -> null
        }
    }
    
    /**
     * Validates that a positive integer is valid
     * @return Error message if validation fails, null if valid
     */
    fun validatePositiveInteger(value: String, fieldName: String = "Valor"): String? {
        if (value.isBlank()) {
            return "$fieldName es requerido"
        }
        
        val number = value.toIntOrNull()
        return when {
            number == null -> "$fieldName debe ser un número entero válido"
            number <= 0 -> "$fieldName debe ser mayor a 0"
            else -> null
        }
    }
    
    /**
     * Validates phone number format (basic validation)
     * @return Error message if validation fails, null if valid
     */
    fun validatePhone(phone: String): String? {
        if (phone.isBlank()) {
            return "Teléfono es requerido"
        }
        
        val phoneRegex = "^[0-9]{7,15}$".toRegex()
        return if (!phone.matches(phoneRegex)) {
            "Teléfono inválido (7-15 dígitos)"
        } else {
            null
        }
    }
}
