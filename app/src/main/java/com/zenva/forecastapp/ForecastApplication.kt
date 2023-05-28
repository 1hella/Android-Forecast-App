package com.zenva.forecastapp

import android.app.Application
import android.content.Context
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule

class ForecastApplication : Application() {
    // onCreate runs as soon as the app starts
    override fun onCreate() {
        super.onCreate()
        // Here, "this" references the actual ForecastApplication instance
        instance = this
    }

    companion object {
        // Store the instance inside the companion object
        private lateinit var instance: ForecastApplication

        // Use the instance to obtain the application context
        val context: Context
            get() = instance.applicationContext
    }
}

// Glide needs a custom AppGlideModule to function.
// Our app doesn't need any additional setup, so we can leave it to be an empty class.
@GlideModule
class ForecastGlideModule : AppGlideModule()