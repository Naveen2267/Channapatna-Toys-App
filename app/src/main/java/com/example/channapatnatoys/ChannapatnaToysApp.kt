package com.example.channapatnatoys

import android.app.Application
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapsSdkInitializedCallback
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ChannapatnaToysApp : Application(), OnMapsSdkInitializedCallback {
    override fun onCreate() {
        super.onCreate()
        // Initialize Maps SDK early to avoid blocking the main thread when the map is first displayed.
        MapsInitializer.initialize(applicationContext, MapsInitializer.Renderer.LATEST, this)
    }

    override fun onMapsSdkInitialized(renderer: MapsInitializer.Renderer) {
        when (renderer) {
            MapsInitializer.Renderer.LATEST -> android.util.Log.d("MapsInitializer", "The latest version of the renderer is used.")
            MapsInitializer.Renderer.LEGACY -> android.util.Log.d("MapsInitializer", "The legacy version of the renderer is used.")
        }
    }
}
