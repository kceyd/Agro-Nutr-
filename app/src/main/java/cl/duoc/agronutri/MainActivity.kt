package cl.duoc.agronutri

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import cl.duoc.agronutri.ui.AgroNutriRoot
import cl.duoc.agronutri.ui.theme.AgroNutriTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AgroNutriTheme {
                AgroNutriRoot()
            }
        }
    }
}
