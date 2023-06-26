package retanar.totp_android.presentation.importexport

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    val state = viewModel.importScreenState
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
                }
            }
        }
    }
    LaunchedEffect(Unit) {
        getInputStreamLauncher.launch(arrayOf("application/json"))
    }

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
            if (!state.errorText.isNullOrEmpty()) {
                Text(text = state.errorText)
                Button(onClick = {
                    getInputStreamLauncher.launch(arrayOf("application/json"))
                }) {
                    Text(text = "TRY AGAIN", fontWeight = FontWeight.Bold)
                }
            } else if (!state.importedKeys.isNullOrEmpty()) {
                Text("Choose keys to import:", fontSize = 22.sp, modifier = Modifier.padding(8.dp))
                ImportedKeysList(
                    state.importedKeys,
                    onCheckedChange = { viewModel.changeCheck(it) },
                    Modifier.weight(1f)
                )
                Button(onClick = {
                    coroutineScope.launch {
                        viewModel.addSelected()
                        onPopBack()
                    }
                }) {
                    Text("ADD SELECTED", fontWeight = FontWeight.Bold)
                }
            } else {
                Text(text = "Import in progress", style = MaterialTheme.typography.h5)
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
                }
            },
        )

        LaunchedSnackbar(
            text = state.errorText ?: "",
            snackbarHostState = scaffoldState.snackbarHostState,
            showSnackbar = !state.errorText.isNullOrEmpty(),
            onDismiss = { /*TODO: because I don't use showSnackbar as a separate var, I have nothing to do here*/ }
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

@Composable
fun ImportedKeysList(keys: List<ImportedItemState>, onCheckedChange: (Int) -> Unit, modifier: Modifier = Modifier) {
    LazyColumn(modifier.fillMaxSize()) {
        items(keys.size) { index ->
            val item = keys[index]
            Card(
                Modifier
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = item.checked, onCheckedChange = { onCheckedChange(index) })
                    Column {
                        Text(item.name, fontSize = 20.sp)
                        Text(
                            if (!item.secretSimilarity.isNullOrEmpty())
                                "Secret value is similar to ${item.secretSimilarity}"
                            else if (!item.nameSimilarity.isNullOrEmpty())
                                "Name is similar to ${item.nameSimilarity}"
                            else
                                "",
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}