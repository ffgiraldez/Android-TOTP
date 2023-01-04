package retanar.totp_android.presentation.composables

import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

@Composable
fun LaunchedSnackbar(
    text: String,
    snackbarHostState: SnackbarHostState,
    showSnackbar: Boolean,
    onDismiss: () -> Unit,
    duration: SnackbarDuration = SnackbarDuration.Short,
) {
    if (showSnackbar) {
        LaunchedEffect(snackbarHostState) {
            when (snackbarHostState.showSnackbar(text, duration = duration)) {
                SnackbarResult.Dismissed, SnackbarResult.ActionPerformed -> onDismiss()
            }
        }
    }
}