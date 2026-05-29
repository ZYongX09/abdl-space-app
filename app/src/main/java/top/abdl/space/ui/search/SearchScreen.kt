package top.abdl.space.ui.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import top.abdl.space.data.model.Diaper
import top.abdl.space.data.model.Post
import top.abdl.space.ui.components.EmptyState
import top.abdl.space.ui.components.ErrorView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onNavigateToPostDetail: (Int) -> Unit,
    onNavigateToDiaperDetail: (Int) -> Unit,
    onNavigateToProfile: (Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    TextField(
                        value = uiState.query,
                        onValueChange = { viewModel.updateQuery(it) },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("搜索...") },
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        ),
                        trailingIcon = {
                            if (uiState.query.isNotEmpty()) {
                                IconButton(onClick = { viewModel.clearResults() }) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "清除"
                                    )
                                }
                            }
                        }
                    )
                },
                actions = {
                    IconButton(onClick = { viewModel.search() }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "搜索"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                FilterChip(
                    selected = uiState.searchType == SearchType.POSTS,
                    onClick = { viewModel.updateSearchType(SearchType.POSTS) },
                    label = { Text("帖子") },
                    modifier = Modifier.padding(end = 8.dp)
                )
                FilterChip(
                    selected = uiState.searchType == SearchType.DIAPERS,
                    onClick = { viewModel.updateSearchType(SearchType.DIAPERS) },
                    label = { Text("纸尿裤") }
                )
            }

            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                uiState.error != null -> {
                    ErrorView(
                        message = uiState.error ?: "未知错误",
                        onRetry = { viewModel.search() }
                    )
                }
                uiState.query.isBlank() -> {
                    EmptyState(
                        title = "搜索 ABDL Space",
                        description = "输入关键词搜索帖子或纸尿裤"
                    )
                }
                uiState.searchType == SearchType.POSTS && uiState.posts.isEmpty() -> {
                    EmptyState(
                        title = "未找到帖子",
                        description = "尝试其他关键词"
                    )
                }
                uiState.searchType == SearchType.DIAPERS && uiState.diapers.isEmpty() -> {
                    EmptyState(
                        title = "未找到纸尿裤",
                        description = "尝试其他关键词"
                    )
                }
                else -> {
                    when (uiState.searchType) {
                        SearchType.POSTS -> {
                            LazyColumn {
                                items(uiState.posts) { post ->
                                    PostSearchItem(
                                        post = post,
                                        onClick = { onNavigateToPostDetail(post.id) },
                                        onUserClick = { onNavigateToProfile(post.user.id) }
                                    )
                                }
                            }
                        }
                        SearchType.DIAPERS -> {
                            LazyColumn {
                                items(uiState.diapers) { diaper ->
                                    DiaperSearchItem(
                                        diaper = diaper,
                                        onClick = { onNavigateToDiaperDetail(diaper.id) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PostSearchItem(
    post: Post,
    onClick: () -> Unit,
    onUserClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = post.user.avatar,
                    contentDescription = null,
                    modifier = Modifier
                        .size(32.dp)
                        .clickable(onClick = onUserClick)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = post.user.username,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable(onClick = onUserClick)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = post.content,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun DiaperSearchItem(
    diaper: Diaper,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = "${diaper.brand} ${diaper.model}",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = diaper.productType,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (diaper.avgScore != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "评分: ${diaper.avgScore}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
