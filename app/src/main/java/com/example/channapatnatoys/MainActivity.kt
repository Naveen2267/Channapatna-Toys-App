package com.example.channapatnatoys

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.channapatnatoys.navigation.AppNavigation
import com.example.channapatnatoys.ui.theme.ChannapatnaToysTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ChannapatnaToysTheme {
                AppNavigation()
            }
        }
    }
}