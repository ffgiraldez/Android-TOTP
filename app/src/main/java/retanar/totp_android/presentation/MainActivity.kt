package retanar.totp_android.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import retanar.totp_android.presentation.home.HomeScreen
import retanar.totp_android.presentation.home.HomeViewModel
import retanar.totp_android.presentation.importexport.ExportScreen
import retanar.totp_android.presentation.theme.TOTPTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TOTPTheme {
                Surface(color = MaterialTheme.colors.background) {
                    val navController = rememberNavController()
                    NavHost(navController, startDestination = "home") {
                        composable("home") {
                            // proper way to create ViewModel with Hilt and Navigation Compose
                            val viewModel = hiltViewModel<HomeViewModel>()
                            HomeScreen(
                                viewModel,
                                navigateExport = { navController.navigate("export") },
                            )
                        }
                        composable("export") {
                            ExportScreen(onPopBack = { navController.popBackStack() })
                        }
                    }
                }
            }
        }
    }
}
