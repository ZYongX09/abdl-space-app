package top.abdl.space.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import top.abdl.space.ui.auth.AuthViewModel

@Composable
fun HomeScreen(
    onNavigateToForum: () -> Unit = {},
    onNavigateToDiapers: () -> Unit = {},
    onNavigateToSearch: () -> Unit = {},
    authViewModel: AuthViewModel = koinViewModel()
) {
    val authUiState by authViewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "ABDL Space",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (authUiState.currentUser != null) {
            Text(
                text = "欢迎回来，${authUiState.currentUser?.username}！",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
        } else {
            Text(
                text = "欢迎来到 ABDL Space 社区",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onNavigateToForum,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("浏览动态")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onNavigateToDiapers,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("纸尿裤库")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onNavigateToSearch,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("搜索")
        }
    }
}
