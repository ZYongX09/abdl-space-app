package top.abdl.space.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import top.abdl.space.data.model.FollowUser
import top.abdl.space.ui.components.ErrorView
import top.abdl.space.ui.components.LoadingAnimation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    userId: Int,
    onNavigateBack: () -> Unit,
    onNavigateToEditProfile: () -> Unit,
    onNavigateToProfile: (Int) -> Unit,
    onNavigateToSettings: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(userId) { viewModel.loadProfile(userId) }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is ProfileEvent.Error -> snackbarHostState.showSnackbar(event.message)
                else -> {}
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("个人主页", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    if (viewModel.isCurrentUser(userId)) {
                        IconButton(onClick = onNavigateToSettings) {
                            Icon(Icons.Default.Settings, contentDescription = "设置", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        when {
            uiState.isLoading -> {
                Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    LoadingAnimation()
                }
            }
            uiState.error != null -> {
                ErrorView(message = uiState.error ?: "未知错误", onRetry = { viewModel.loadProfile(userId) })
            }
            uiState.user != null -> {
                val user = uiState.user!!
                LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)) {
                    // ─── Banner + 头像 ───
                    item {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            // 渐变 Banner
                            Box(
                                modifier = Modifier.fillMaxWidth().height(160.dp).background(
                                    Brush.verticalGradient(listOf(
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                                    ))
                                )
                            )
                            // 头像 + 用户信息
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(modifier = Modifier.offset(y = (-44).dp)) {
                                    AsyncImage(
                                        model = user.avatar,
                                        contentDescription = null,
                                        modifier = Modifier.size(88.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surface, CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                                Text(
                                    text = user.username ?: "",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.offset(y = (-12).dp)
                                )
                                if (!user.region.isNullOrBlank()) {
                                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.offset(y = (-8).dp)) {
                                        Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(user.region, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                                // 操作按钮
                                if (viewModel.isCurrentUser(userId)) {
                                    OutlinedButton(onClick = onNavigateToEditProfile, modifier = Modifier.fillMaxWidth(0.5f).offset(y = (-4).dp), shape = MaterialTheme.shapes.medium) {
                                        Text("编辑资料")
                                    }
                                } else {
                                    if (uiState.isFollowing) {
                                        OutlinedButton(onClick = { viewModel.unfollowUser(userId) }, modifier = Modifier.fillMaxWidth(0.5f), shape = MaterialTheme.shapes.medium) {
                                            Text("已关注")
                                        }
                                    } else {
                                        Button(onClick = { viewModel.followUser(userId) }, modifier = Modifier.fillMaxWidth(0.5f), shape = MaterialTheme.shapes.medium) {
                                            Text("关注")
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // ─── 统计数据 ───
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 40.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            StatItem(count = uiState.followers.size, label = "粉丝", onClick = { viewModel.loadFollowers(userId) })
                            StatItem(count = uiState.following.size, label = "关注", onClick = { viewModel.loadFollowing(userId) })
                        }
                    }

                    // ─── 个人简介 ───
                    if (!user.bio.isNullOrBlank()) {
                        item {
                            Text(
                                text = user.bio,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                                lineHeight = 22.sp
                            )
                        }
                    }

                    // ─── 粉丝列表 ───
                    if (uiState.followers.isNotEmpty()) {
                        item { SectionHeader("粉丝") }
                        items(uiState.followers) { FollowUserItem(it) { onNavigateToProfile(it.id) } }
                    }

                    // ─── 关注列表 ───
                    if (uiState.following.isNotEmpty()) {
                        item { SectionHeader("关注") }
                        items(uiState.following) { FollowUserItem(it) { onNavigateToProfile(it.id) } }
                    }

                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp))
}

@Composable
private fun StatItem(count: Int, label: String, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable(onClick = onClick)) {
        Text("$count", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun FollowUserItem(user: FollowUser, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(model = user.avatar, contentDescription = null, modifier = Modifier.size(40.dp).clip(CircleShape))
        Spacer(modifier = Modifier.width(14.dp))
        Text(user.username ?: "", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, modifier = Modifier.padding(start = 74.dp))
}
