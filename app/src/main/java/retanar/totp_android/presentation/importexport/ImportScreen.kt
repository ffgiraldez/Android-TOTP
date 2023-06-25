package retanar.totp_android.presentation.importexport

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.launch
import retanar.totp_android.presentation.composables.LaunchedSnackbar
import retanar.totp_android.presentation.composables.PasswordTextField

@Composable
fun ImportScreen(
    viewModel: ImportViewModel,
    onPopBack: () -> Unit,
) {
    var showPasswordDialog by remember { mutableStateOf(false) }

    val scaffoldState = rememberScaffoldState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val getInputStreamLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { content ->
        if (content == null)
            return@rememberLauncherForActivityResult
        coroutineScope.launch {
            context.contentResolver.openInputStream(content)?.use { importStream ->
                val isPasswordNeeded = viewModel.prepareAndCheckPassword(importStream)
                showPasswordDialog = isPasswordNeeded
                if (isPasswordNeeded.not()) {
                    viewModel.import()
                    if (viewModel.errorText.isEmpty())
                        onPopBack()
                }
            }
        }
    }
    LaunchedEffect(Unit) {
        getInputStreamLauncher.launch(arrayOf("application/json"))
    }
    // TODO: allow user to choose what codes to import, and show duplicates
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Export", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onPopBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
        scaffoldState = scaffoldState,
    ) { paddingValues ->
        Column(
            Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            if (viewModel.errorText.isEmpty()) {
                Text(text = "Import in progress", style = MaterialTheme.typography.h5)
            } else {
                Text(text = viewModel.errorText)
                Button(onClick = {
                    getInputStreamLauncher.launch(arrayOf("application/json"))
                }) {
                    Text(text = "Try again")
                }
            }
        }

        AskPasswordDialog(
            showDialog = showPasswordDialog,
            onDismiss = {
                showPasswordDialog = false
                onPopBack()
            },
            onSuccess = { password ->
                showPasswordDialog = false
                coroutineScope.launch {
                    viewModel.import(password)
                    if (viewModel.errorText.isEmpty())
                        onPopBack()
                }
            },
        )

        LaunchedSnackbar(
            text = viewModel.errorText,
            snackbarHostState = scaffoldState.snackbarHostState,
            showSnackbar = viewModel.errorText.isNotEmpty(),
            onDismiss = { viewModel.errorText }
        )
    }
}

@Composable
fun AskPasswordDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onSuccess: (String) -> Unit,
) {
    if (!showDialog) return
    var password by remember { mutableStateOf("") }
    Dialog(onDismissRequest = onDismiss) {
        Card {
            Column {
                Text(
                    "Enter export password",
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp),
                    fontSize = 22.sp,
                    color = MaterialTheme.colors.primary,
                    maxLines = 1,
                )
                PasswordTextField(
                    label = "Password",
                    value = password,
                    onValueChange = { password = it },
                    modifier = Modifier
                        .padding(all = 16.dp)
                        .fillMaxWidth()
                )
                Row(Modifier.fillMaxWidth(), Arrangement.End) {
                    TextButton(onClick = { onSuccess(password) }) {
                        Text("OK")
                    }
                    TextButton(onClick = onDismiss) {
                        Text("CANCEL")
                    }
                }
            }
        }
    }
}