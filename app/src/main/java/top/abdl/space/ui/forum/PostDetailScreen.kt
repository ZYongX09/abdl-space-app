package top.abdl.space.ui.forum

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.Comment
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import top.abdl.space.data.model.Comment
import top.abdl.space.data.model.Post
import top.abdl.space.ui.components.ErrorView
import top.abdl.space.ui.components.ImageGrid
import top.abdl.space.ui.components.LoadingAnimation
import top.abdl.space.ui.components.StaggerItem
import top.abdl.space.util.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailScreen(
    viewModel: ForumViewModel,
    postId: Int,
    onNavigateBack: () -> Unit,
    onNavigateToProfile: (Int) -> Unit
) {
    val detailUiState by viewModel.detailUiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var commentText by remember { mutableStateOf("") }

    LaunchedEffect(postId) {
        viewModel.loadPostDetail(postId)
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is ForumEvent.CommentCreated -> commentText = ""
                is ForumEvent.Error -> snackbarHostState.showSnackbar(event.message)
                else -> {}
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "帖子详情",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.clearDetail()
                        onNavigateBack()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回"
                        )
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
            detailUiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    LoadingAnimation()
                }
            }
            detailUiState.error != null -> {
                ErrorView(
                    message = detailUiState.error ?: "未知错误",
                    onRetry = { viewModel.loadPostDetail(postId) }
                )
            }
            detailUiState.postDetail != null -> {
                val detail = detailUiState.postDetail!!
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    LazyColumn(
                        modifier = Modifier.weight(1f)
                    ) {
                        item {
                            PostContent(
                                post = detail.post,
                                onUserClick = { onNavigateToProfile(detail.post.user.id) },
                                onLikeClick = { viewModel.toggleLike("post", detail.post.id) }
                            )
                        }

                        if (detail.comments.isNotEmpty()) {
                            item {
                                Text(
                                    text = "评论 (${detail.comments.size})",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                                )
                            }

                            items(
                                items = detail.comments,
                                key = { it.id }
                            ) { comment ->
                                CommentItem(
                                    comment = comment,
                                    onUserClick = { onNavigateToProfile(comment.user.id) },
                                    onLikeClick = { viewModel.toggleLike("comment", comment.id) }
                                )
                            }
                        }
                    }

                    CommentInput(
                        value = commentText,
                        onValueChange = { commentText = it },
                        onSend = {
                            if (commentText.isNotBlank()) {
                                viewModel.createComment(postId, commentText)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun PostContent(
    post: Post,
    onUserClick: () -> Unit,
    onLikeClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = post.user.avatar,
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onUserClick
                    )
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = post.user.username,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = DateUtils.formatRelativeTime(post.createdAt),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = post.content,
            style = MaterialTheme.typography.bodyLarge,
            lineHeight = 26.sp
        )

        // 图片
        if (post.images.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            ImageGrid(images = post.images.map { it.imageUrl })
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            PostAction(
                icon = if (post.hasLiked) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                count = post.likeCount,
                isActive = post.hasLiked,
                activeColor = MaterialTheme.colorScheme.error,
                onClick = onLikeClick
            )
            PostAction(
                icon = Icons.Outlined.Comment,
                count = post.commentCount,
                isActive = false,
                activeColor = MaterialTheme.colorScheme.primary,
                onClick = {}
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
    }
}

@Composable
private fun CommentItem(
    comment: Comment,
    onUserClick: () -> Unit,
    onLikeClick: () -> Unit
) {
    val dividerColor = MaterialTheme.colorScheme.outlineVariant

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                drawLine(
                    color = dividerColor,
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = 0.5.dp.toPx()
                )
            }
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = comment.user.avatar,
                contentDescription = null,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onUserClick
                    )
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = comment.user.username,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = DateUtils.formatRelativeTime(comment.createdAt),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            PostAction(
                icon = if (comment.hasLiked) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                count = comment.likeCount,
                isActive = comment.hasLiked,
                activeColor = MaterialTheme.colorScheme.error,
                onClick = onLikeClick
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = comment.content,
            style = MaterialTheme.typography.bodyMedium,
            lineHeight = 22.sp
        )
    }
}

@Composable
private fun PostAction(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    count: Int,
    isActive: Boolean,
    activeColor: Color,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(vertical = 2.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isActive) activeColor else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(18.dp)
        )
        if (count > 0) {
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "$count",
                style = MaterialTheme.typography.bodySmall,
                color = if (isActive) activeColor else MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
private fun CommentInput(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.weight(1f),
            placeholder = {
                Text(
                    "写评论...",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            maxLines = 3,
            shape = MaterialTheme.shapes.large,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(
            onClick = onSend,
            enabled = value.isNotBlank()
        ) {
            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = "发送",
                tint = if (value.isNotBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
