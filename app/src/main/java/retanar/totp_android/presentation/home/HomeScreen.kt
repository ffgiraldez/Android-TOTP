@file:Suppress("FunctionName")

package retanar.totp_android.presentation.home

import android.os.Build
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import retanar.totp_android.R
import retanar.totp_android.presentation.DependencyContainer
import retanar.totp_android.presentation.composables.LaunchedSnackbar
import retanar.totp_android.presentation.composables.PopupMenuDialog
import retanar.totp_android.presentation.composables.PopupMenuTextItem

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(
            DependencyContainer.totpKeyRepository,
            DependencyContainer.secretEncryptor,
            DependencyContainer.totpCodeGenerator,
        )
    )
) {
    val state by viewModel.homeState
    var showAddDialog by remember { mutableStateOf(false) }
    var showCopiedSnackbar by remember { mutableStateOf(false) }
    val scaffoldState = rememberScaffoldState()
    val clipboard = LocalClipboardManager.current

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.app_name), fontWeight = FontWeight.Bold) })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Filled.Add, "Add TOTP", tint = Color.White)
            }
        },
    ) {
        TotpCardListView(
            list = state.totpList,
            onRemove = viewModel::removeTotpById,
            onCopy = { code ->
                clipboard.setText(AnnotatedString(code))
                showCopiedSnackbar = true
            },
            onEdit = viewModel::requestEdit,
        )

        AddTotpDialog(showAddDialog, { showAddDialog = false }, viewModel::addTotp)
        EditTotpDialog(state.editingTotp, { viewModel.requestEdit(-1) }, viewModel::editTotp)
    }

    // Android 13 (API 33) shows system confirmation on copy
    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2)
        LaunchedSnackbar("Copied", scaffoldState.snackbarHostState, showCopiedSnackbar, { showCopiedSnackbar = false })
}

@Composable
fun TotpCardListView(
    list: List<TotpCardState>,
    onRemove: (id: Int) -> Unit,
    onCopy: (code: String) -> Unit,
    onEdit: (id: Int) -> Unit
) {
    LazyColumn {
        items(items = list) { item ->
            TotpCard(item, onRemove, onCopy, onEdit)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TotpCard(
    totpCardState: TotpCardState,
    onRemove: (id: Int) -> Unit,
    onCopy: (code: String) -> Unit,
    onEdit: (id: Int) -> Unit
) {
    var showPopupMenu by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .combinedClickable(onClick = {}, onLongClick = {
                showPopupMenu = true
            }),
        elevation = 2.dp,
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val codeString = totpCardState.oneTimeCode.toString().padStart(6, '0')
            Column(Modifier.padding(8.dp)) {
                Text(text = totpCardState.name)
                Text(fontSize = 28.sp, text = codeString)
            }
            IconButton(onClick = { onCopy(codeString) }) {
                Icon(painterResource(R.drawable.ic_content_copy), "Copy to clipboard")
            }
        }
    }
    if (showPopupMenu) {
        PopupMenuDialog(
            { showPopupMenu = false },
            PopupMenuTextItem("Edit") {
                onEdit(totpCardState.id)
                showPopupMenu = false
            },
            PopupMenuTextItem("Remove") { onRemove(totpCardState.id) },
        )
    }
}

@Composable
fun AddTotpDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onAdd: (name: String, secret: String) -> Unit,
) {
    TotpDialog(
        title = "Add new TOTP",
        showDialog = showDialog,
        actionText = "ADD",
        onAction = onAdd,
        onDismiss = onDismiss,
    )
}

@Composable
fun EditTotpDialog(
    totpState: EditTotpState?,
    onDismiss: () -> Unit,
    onEdit: (EditTotpState) -> Unit,
) {
    totpState?.let {
        TotpDialog(
            showDialog = true,
            title = "",
            name = totpState.name,
            secret = totpState.base32Secret,
            actionText = "EDIT",
            onAction = { name, secret -> onEdit(totpState.copy(name = name, base32Secret = secret)) },
            onDismiss = onDismiss,
        )
    }
}

@Composable
fun TotpDialog(
    showDialog: Boolean,
    title: String,
    name: String = "",
    secret: String = "",
    actionText: String,
    onAction: (name: String, secret: String) -> Unit,
    dismissText: String = "CANCEL",
    onDismiss: () -> Unit,
) {
    if (!showDialog) return

    var nameField by remember { mutableStateOf(name) }
    var secretField by remember { mutableStateOf(secret) }
    var secretIsError by remember { mutableStateOf(false) }

    Dialog(onDismiss) {
        Card {
            Column {
                if (title.isNotBlank())
                    Text(
                        title,
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp),
                        fontSize = 22.sp,
                        color = MaterialTheme.colors.primary,
                        maxLines = 1,
                    )
                TextField(
                    value = nameField,
                    onValueChange = { nameField = it },
                    label = { Text("Name") },
                    singleLine = true,
                    modifier = Modifier.padding(all = 8.dp).fillMaxWidth(),
                    textStyle = TextStyle(fontSize = 22.sp),
                    colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White),
                )
                TextField(
                    value = secretField,
                    onValueChange = { secretField = it },
                    label = { Text("Secret") },
                    singleLine = true,
                    isError = secretIsError,
                    modifier = Modifier.padding(all = 8.dp).fillMaxWidth(),
                    textStyle = TextStyle(fontSize = 22.sp),
                    colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White),
                )
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(dismissText)
                    }
                    TextButton(onClick = {
                        // TODO put check if all symbols are valid Base32
                        if (secretField.isEmpty()) {
                            secretIsError = true
                            return@TextButton
                        }
                        secretIsError = false
                        onAction(nameField, secretField)
                        onDismiss()
                    }) {
                        Text(actionText)
                    }
                }
            }
        }
    }
}
