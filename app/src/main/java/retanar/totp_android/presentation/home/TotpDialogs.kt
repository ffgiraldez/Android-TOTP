package retanar.totp_android.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

val notBase32Regex = Regex("[^A-Za-z2-7=]")

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
                    onValueChange = {
                        if (it.isEmpty()) {
                            secretIsError = true
                            return@TextField
                        } else {
                            secretIsError = false
                        }
                        secretField = validateBase32(it)
                    },
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
                        if (secretField.isEmpty()) {
                            secretIsError = true
                            return@TextButton
                        }
//                        secretIsError = false
                        onAction(nameField, validateBase32(secretField.uppercase()))
                        onDismiss()
                    }) {
                        Text(actionText)
                    }
                }
            }
        }
    }
}

fun validateBase32(text: String) = text.replace(notBase32Regex, "")