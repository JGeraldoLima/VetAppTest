package com.example.petmedtracker.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.petmedtracker.presentation.navigation.PetMedTrackerNavGraph
import com.example.petmedtracker.presentation.theme.PetMedTrackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PetMedTrackerTheme {
                PetMedTrackerNavGraph()
            }
        }
    }
}
