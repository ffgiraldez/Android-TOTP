package retanar.totp_android.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun AddTotpDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onAdd: (name: String, secret: String) -> Unit,
    validateSecret: (String) -> Boolean,
) {
    TotpDialog(
        title = "Add new TOTP",
        showDialog = showDialog,
        actionText = "ADD",
        onAction = onAdd,
        onDismiss = onDismiss,
        onValidateSecret = validateSecret,
    )
}

@Composable
fun EditTotpDialog(
    totpState: EditTotpState?,
    onDismiss: () -> Unit,
    onEdit: (EditTotpState) -> Unit,
    onValidateSecret: (String) -> Boolean,
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
            onValidateSecret = onValidateSecret,
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
    onValidateSecret: (String) -> Boolean,
) {
    if (!showDialog) return

    var nameField by rememberSaveable { mutableStateOf(name) }
    var secretField by rememberSaveable { mutableStateOf(secret) }
    var secretIsError by remember { mutableStateOf(false) }

    Dialog(onDismiss) {
        Card {
            Column {
                if (title.isNotBlank())
                    Text(
                        title,
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp),
                        style = MaterialTheme.typography.h5,
                        maxLines = 1,
                    )
                TextField(
                    value = nameField,
                    onValueChange = { nameField = it },
                    label = { Text("Name") },
                    singleLine = true,
                    modifier = Modifier
                        .padding(all = 8.dp)
                        .fillMaxWidth(),
                    textStyle = MaterialTheme.typography.h6,
                    colors = TextFieldDefaults.textFieldColors(
                        textColor = MaterialTheme.colors.onSurface,
                        backgroundColor = MaterialTheme.colors.surface,
                    ),
                )
                TextField(
                    value = secretField,
                    onValueChange = {
                        secretIsError = !onValidateSecret(it)
                        secretField = it
                    },
                    label = { Text("Secret") },
                    singleLine = true,
                    isError = secretIsError,
                    modifier = Modifier
                        .padding(all = 8.dp)
                        .fillMaxWidth(),
                    textStyle = MaterialTheme.typography.h6,
                    colors = TextFieldDefaults.textFieldColors(
                        textColor = MaterialTheme.colors.onBackground,
                        backgroundColor = MaterialTheme.colors.background,
                    ),
                )
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 4.dp),
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(dismissText)
                    }
                    TextButton(
                        enabled = !secretIsError,
                        onClick = {
                            if (!onValidateSecret(secretField)) {
                                secretIsError = true
                                return@TextButton
                            }
                            onAction(nameField, secretField)
                            onDismiss()

                        },
                    ) {
                        Text(actionText)
                    }
                }
            }
        }
    }
}