package retanar.totp_android.presentation.importexport

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import retanar.totp_android.R

val exportOptions = listOf("No encryption", "Encrypt only keys", "Encrypt everything")

@Composable
fun ExportScreen(
    onPopBack: () -> Unit,
) {
    var currentExportChoice by remember { mutableStateOf(0) }
    var encryptionPassword by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    Scaffold(topBar = {
        TopAppBar(
            title = { Text("Export", fontWeight = FontWeight.Bold) },
            navigationIcon = {
                IconButton(onClick = onPopBack) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                }
            },
        )
    }) {
        Column(
            Modifier
                .padding(it)
                .fillMaxWidth()
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(16.dp))
            ExportTypeMenu(currentExportChoice, { currentExportChoice = it })

            if (currentExportChoice != 0) {
                Spacer(Modifier.height(32.dp))
                OutlinedTextField(
                    value = encryptionPassword,
                    onValueChange = { newPass -> encryptionPassword = newPass },
                    singleLine = true,
                    visualTransformation = if (showPassword) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    trailingIcon = {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(
                                painterResource(
                                    if (showPassword) {
                                        R.drawable.visibility_off_filled
                                    } else {
                                        R.drawable.visibility_filled
                                    }
                                ),
                                contentDescription = "Toggle visibility"
                            )
                        }
                    },
                    label = { Text("Password") },
                )
            }

            Spacer(Modifier.weight(1f))
            Button(
                onClick = { /*TODO: connect to ViewModel */ },
                Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("EXPORT")
            }
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

    ExposedDropdownMenuBox(expanded = isExpanded, onExpandedChange = { isExpanded = !isExpanded }) {
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

@Preview
@Composable
fun PreviewExportScreen() {
    ExportScreen {}
}