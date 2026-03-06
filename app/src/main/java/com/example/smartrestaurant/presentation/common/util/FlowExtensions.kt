package com.example.smartrestaurant.presentation.common.util

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.debounce

/**
 * Debounce extension for search queries
 * Delays emission of values for the specified timeout
 */
@OptIn(FlowPreview::class)
fun <T> Flow<T>.debounceSearch(timeoutMillis: Long = 300L): Flow<T> {
    return this.debounce(timeoutMillis)
}
