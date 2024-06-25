package com.worksheetportiflio

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
//import androidx.activity.enableEdgeToEdge
import com.worksheetportiflio.systemsettings.Navigation
import com.worksheetportiflio.ui.theme.WorkSHeet_PortifólioTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContent {
            WorkSHeet_PortifólioTheme {
                Navigation().Create()
            }
        }
    }
}
