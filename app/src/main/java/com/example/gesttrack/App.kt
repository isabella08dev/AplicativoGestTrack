package com.example.gesttrack

import android.app.Application
import android.content.res.Configuration
import android.util.DisplayMetrics

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        adjustDensity()
    }

    private fun adjustDensity() {
        val displayMetrics: DisplayMetrics = resources.displayMetrics
        val config = Configuration(resources.configuration)

        // Detecta automaticamente se é tablet ou celular
        val baseWidth = if (displayMetrics.widthPixels > 1200) 600f else 411f
        val scale = displayMetrics.widthPixels / baseWidth

        // Calcula densidade proporcional
        val newDensityDpi = (160 * scale).toInt()

        // Atualiza de forma segura
        @Suppress("DEPRECATION")
        config.densityDpi = newDensityDpi
        @Suppress("DEPRECATION")
        resources.updateConfiguration(config, displayMetrics)
    }
}
