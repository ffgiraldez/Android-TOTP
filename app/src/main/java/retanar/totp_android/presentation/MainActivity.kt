package retanar.totp_android.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import dagger.hilt.android.AndroidEntryPoint
import retanar.totp_android.presentation.home.HomeScreen
import retanar.totp_android.presentation.theme.TOTPTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TOTPTheme {
                Surface(color = MaterialTheme.colors.background) {
                    HomeScreen()
                }
            }
        }
    }
}
