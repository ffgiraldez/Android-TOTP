package retanar.totp_android.presentation.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun PopupMenuDialog(onDismiss: () -> Unit, vararg items: PopupMenuTextItem) {
    Dialog(onDismiss) {
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(4.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                items.forEach { item ->
                    TextButton(modifier = Modifier.fillMaxWidth(), onClick = item.onClick) {
                        Text(item.text, textAlign = TextAlign.Center, style = MaterialTheme.typography.button)
                    }
                }
            }
        }
    }
}

class PopupMenuTextItem(
    val text: String,
    val onClick: () -> Unit,
)