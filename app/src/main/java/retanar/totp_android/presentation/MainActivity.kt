@file:Suppress("FunctionName")

package retanar.totp_android.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import retanar.totp_android.presentation.theme.theme.TOTPTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DependencyContainer.initialize(application.applicationContext)

        setContent {
            TOTPTheme {
                Surface(color = MaterialTheme.colors.background) {
                    HomeScreen()
                }
            }
        }
    }
}

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
    val state = viewModel.homeState
    Scaffold {
        TotpCardListView(/*state.value.totpList*/ listOf(
            TotpCardState(1, "name 1", 111111),
            TotpCardState(2, "name 2", 222222),
            TotpCardState(3, "name 3", 333333),
        ))
    }
}

@Composable
fun TotpCardListView(list: List<TotpCardState>) {
    LazyColumn {
        items(items = list) { item ->
            TotpCard(item)
        }
    }

}

@Composable
fun TotpCard(totpCardState: TotpCardState) {
    Card(Modifier.padding(8.dp)) {
        Column {
            Text(text = totpCardState.name)
            Text(fontSize = 28.sp, text = totpCardState.oneTimeCode.toString())
        }
    }
}

@Preview
@Composable
fun ListPreview() {
    TOTPTheme {
        TotpCardListView(listOf(
            TotpCardState(1, "name 1", 111111),
            TotpCardState(2, "name 2", 222222),
            TotpCardState(3, "name 3", 333333),
        ))
    }
}
