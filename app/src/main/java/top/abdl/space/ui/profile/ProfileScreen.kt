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
import coil.compose.AsyncImage
import top.abdl.space.data.model.FollowUser
import top.abdl.space.ui.components.EmptyState
import top.abdl.space.ui.components.ErrorView
import top.abdl.space.ui.components.LoadingAnimation
import top.abdl.space.ui.components.StaggerItem

/**
 * 个人主页 — 微博风格
 * 顶部封面渐变区 + 头像半浮
 * 统计数据（帖子数/粉丝/关注）
 * Tab 切换（帖子/评分）
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    userId: Int,
    onNavigateBack: () -> Unit,
    onNavigateToEditProfile: () -> Unit,
    onNavigateToProfile: (Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(userId) {
        viewModel.loadProfile(userId)
    }

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
                title = {
                    Text(
                        text = "个人主页",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                },
                actions = {
                    if (viewModel.isCurrentUser(userId)) {
                        IconButton(onClick = onNavigateToEditProfile) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "编辑资料",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    LoadingAnimation()
                }
            }
            uiState.error != null -> {
                ErrorView(
                    message = uiState.error ?: "未知错误",
                    onRetry = { viewModel.loadProfile(userId) }
                )
            }
            uiState.user != null -> {
                val user = uiState.user!!
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    // 封面 + 头像
                    item {
                        ProfileHeader(
                            user = user,
                            isCurrentUser = viewModel.isCurrentUser(userId),
                            isFollowing = uiState.isFollowing,
                            onFollowClick = {
                                if (uiState.isFollowing) {
                                    viewModel.unfollowUser(userId)
                                } else {
                                    viewModel.followUser(userId)
                                }
                            },
                            onEditClick = onNavigateToEditProfile
                        )
                    }

                    // 统计
                    item {
                        StaggerItem(index = 1) {
                            ProfileStats(
                                followersCount = uiState.followers.size,
                                followingCount = uiState.following.size,
                                onFollowersClick = { viewModel.loadFollowers(userId) },
                                onFollowingClick = { viewModel.loadFollowing(userId) }
                            )
                        }
                    }

                    // 个人简介
                    if (user.bio != null) {
                        item {
                            StaggerItem(index = 2) {
                                Text(
                                    text = user.bio,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                                )
                            }
                        }
                    }

                    // 粉丝列表
                    if (uiState.followers.isNotEmpty()) {
                        item {
                            SectionHeader(title = "粉丝")
                        }
                        items(uiState.followers) { follower ->
                            FollowUserItem(
                                user = follower,
                                onClick = { onNavigateToProfile(follower.id) }
                            )
                        }
                    }

                    // 关注列表
                    if (uiState.following.isNotEmpty()) {
                        item {
                            SectionHeader(title = "关注")
                        }
                        items(uiState.following) { following ->
                            FollowUserItem(
                                user = following,
                                onClick = { onNavigateToProfile(following.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
    )
}

/**
 * 封面 + 头像 — 头像半浮在封面底部
 */
@Composable
private fun ProfileHeader(
    user: top.abdl.space.data.model.UserFull,
    isCurrentUser: Boolean,
    isFollowing: Boolean,
    onFollowClick: () -> Unit,
    onEditClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // 封面渐变区
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                        )
                    )
                )
        )

        // 头像 + 用户信息 — 头像半浮
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 头像上移覆盖封面
            Box(
                modifier = Modifier.offset(y = (-40).dp)
            ) {
                AsyncImage(
                    model = user.avatar,
                    contentDescription = null,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(
                            MaterialTheme.colorScheme.surface,
                            CircleShape
                        ),
                    contentScale = ContentScale.Crop
                )
            }

            // 用户名
            Column(
                modifier = Modifier.offset(y = (-16).dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = user.username,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                if (user.region != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = user.region,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (isCurrentUser) {
                    OutlinedButton(
                        onClick = onEditClick,
                        modifier = Modifier.fillMaxWidth(0.5f),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text("编辑资料")
                    }
                } else {
                    Button(
                        onClick = onFollowClick,
                        modifier = Modifier.fillMaxWidth(0.5f),
                        shape = MaterialTheme.shapes.medium,
                        colors = if (isFollowing) {
                            ButtonDefaults.outlinedButtonColors()
                        } else {
                            ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        }
                    ) {
                        Text(if (isFollowing) "已关注" else "关注")
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileStats(
    followersCount: Int,
    followingCount: Int,
    onFollowersClick: () -> Unit,
    onFollowingClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        StatItem(
            count = followersCount,
            label = "粉丝",
            onClick = onFollowersClick
        )
        StatItem(
            count = followingCount,
            label = "关注",
            onClick = onFollowingClick
        )
    }
}

@Composable
private fun StatItem(
    count: Int,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = "$count",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun FollowUserItem(
    user: FollowUser,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = user.avatar,
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.width(14.dp))

        Text(
            text = user.username,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )
    }

    HorizontalDivider(
        color = MaterialTheme.colorScheme.outlineVariant,
        modifier = Modifier.padding(start = 70.dp)
    )
}
