package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.ui.navigation.TokokuhAppNavHost
import com.example.ui.theme.TokokuhBackground
import com.example.ui.theme.TokokuhRestoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val appContainer = (application as TokokuhRestoApplication).container

        setContent {
            TokokuhRestoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = TokokuhBackground
                ) {
                    TokokuhAppNavHost(container = appContainer)
                }
            }
        }
    }
}
