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
import retanar.totp_android.presentation.importexport.ExportViewModel
import retanar.totp_android.presentation.importexport.ImportScreen
import retanar.totp_android.presentation.importexport.ImportViewModel
import retanar.totp_android.presentation.theme.TOTPTheme

enum class Routes {
    Home,
    Export,
    Import,
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TOTPTheme {
                Surface(color = MaterialTheme.colors.background) {
                    val navController = rememberNavController()
                    NavHost(navController, startDestination = Routes.Home.name) {
                        composable(Routes.Home.name) {
                            // proper way to create ViewModel with Hilt and Navigation Compose
                            val homeViewModel = hiltViewModel<HomeViewModel>()
                            HomeScreen(
                                homeViewModel,
                                navigateExport = { navController.navigate(Routes.Export.name) },
                                navigateImport = { navController.navigate(Routes.Import.name) },
                            )
                        }
                        composable(Routes.Export.name) {
                            val exportViewModel = hiltViewModel<ExportViewModel>()
                            ExportScreen(exportViewModel, onPopBack = { navController.popBackStack() })
                        }
                        composable(Routes.Import.name) {
                            val importViewModel = hiltViewModel<ImportViewModel>()
                            ImportScreen(importViewModel, onPopBack = { navController.popBackStack() })
                        }
                    }
                }
            }
        }
    }
}
