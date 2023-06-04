package retanar.totp_android.presentation.importexport

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

val exportOptions = listOf("No encryption", "Encrypt only keys", "Encrypt everything")

@Preview
@Composable
fun ExportScreen() {
    var currentExportChoice by remember { mutableStateOf(0) }
    Scaffold(topBar = {
        TopAppBar(title = { Text("Export", fontWeight = FontWeight.Bold) })
    }) {
        Column(
            Modifier
                .padding(it)
                .fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(8.dp))
            ExportTypeMenu(currentExportChoice, { currentExportChoice = it })

        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ExportTypeMenu(
    currentChoice: Int,
    onChange: (Int) -> Unit,
) {
    var isExpanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(expanded = isExpanded, onExpandedChange = { isExpanded = it }) {
        TextField(
            value = exportOptions[currentChoice],
            readOnly = true,
            maxLines = 1,
            trailingIcon = { Icon(Icons.Filled.ArrowDropDown, contentDescription = "Show other options") },
            onValueChange = {},
        )
        ExposedDropdownMenu(expanded = isExpanded, onDismissRequest = { isExpanded = false }) {
            exportOptions.forEachIndexed { index, s ->
                DropdownMenuItem(
                    onClick = {
                        onChange(index)
                        isExpanded = false
                    },
                ) {
                    Text(s)
                }
            }
        }
    }
}