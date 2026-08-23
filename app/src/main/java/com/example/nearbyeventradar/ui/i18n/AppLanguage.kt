package com.example.nearbyeventradar.ui.i18n

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf

enum class AppLanguage(
    val code: String,
    val displayName: String,
    val shortLabel: String,
    val flag: String
) {
    SPANISH("es", "Español", "ES", "🇪🇸"),
    ENGLISH("en", "English", "EN", "🇺🇸");

    val other: AppLanguage
        get() = if (this == SPANISH) ENGLISH else SPANISH
}

val LocalAppLanguage = compositionLocalOf { AppLanguage.SPANISH }
val LocalAppStrings = compositionLocalOf { AppStrings.Spanish }

object LocalizationProvider {
    fun getStrings(language: AppLanguage): AppStrings {
        return when (language) {
            AppLanguage.SPANISH -> AppStrings.Spanish
            AppLanguage.ENGLISH -> AppStrings.English
        }
    }
}
