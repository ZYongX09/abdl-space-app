package top.abdl.space.ui.forum

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import top.abdl.space.data.api.PostApi
import top.abdl.space.data.model.Comment
import top.abdl.space.data.model.CreateCommentRequest
import top.abdl.space.data.model.CreatePostRequest
import top.abdl.space.data.model.LikeRequest
import top.abdl.space.data.model.Post
import top.abdl.space.data.model.PostDetail
import top.abdl.space.util.ErrorHandler

data class ForumUiState(
    val posts: List<Post> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val currentPage: Int = 1,
    val hasMore: Boolean = true
)

data class PostDetailUiState(
    val postDetail: PostDetail? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class ForumEvent {
    data class Error(val message: String) : ForumEvent()
    data object PostCreated : ForumEvent()
    data object CommentCreated : ForumEvent()
}

class ForumViewModel(
    private val postApi: PostApi
) : ViewModel() {
    private val _uiState = MutableStateFlow(ForumUiState())
    val uiState: StateFlow<ForumUiState> = _uiState.asStateFlow()

    private val _detailUiState = MutableStateFlow(PostDetailUiState())
    val detailUiState: StateFlow<PostDetailUiState> = _detailUiState.asStateFlow()

    private val _events = MutableSharedFlow<ForumEvent>()
    val events: SharedFlow<ForumEvent> = _events.asSharedFlow()

    init {
        loadPosts()
    }

    fun loadPosts(refresh: Boolean = false) {
        if (_uiState.value.isLoading) return
        if (refresh) {
            _uiState.value = _uiState.value.copy(isRefreshing = true, currentPage = 1)
        } else {
            _uiState.value = _uiState.value.copy(isLoading = true)
        }

        viewModelScope.launch {
            try {
                val page = if (refresh) 1 else _uiState.value.currentPage
                val response = postApi.getPosts(page = page)
                val newPosts = if (refresh) {
                    response.posts
                } else {
                    _uiState.value.posts + response.posts
                }
                _uiState.value = _uiState.value.copy(
                    posts = newPosts,
                    isLoading = false,
                    isRefreshing = false,
                    currentPage = page + 1,
                    hasMore = page < response.pagination.totalPages
                )
            } catch (e: Exception) {
                val message = ErrorHandler.getUserMessage(e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isRefreshing = false,
                    error = message
                )
                _events.emit(ForumEvent.Error(message))
            }
        }
    }

    fun loadMore() {
        if (!_uiState.value.hasMore || _uiState.value.isLoading) return
        loadPosts()
    }

    fun loadPostDetail(postId: Int) {
        _detailUiState.value = _detailUiState.value.copy(isLoading = true)
        viewModelScope.launch {
            try {
                val detail = postApi.getPostDetail(postId)
                _detailUiState.value = _detailUiState.value.copy(
                    postDetail = detail,
                    isLoading = false
                )
            } catch (e: Exception) {
                val message = ErrorHandler.getUserMessage(e)
                _detailUiState.value = _detailUiState.value.copy(
                    isLoading = false,
                    error = message
                )
            }
        }
    }

    fun createPost(content: String, diaperId: Int? = null) {
        viewModelScope.launch {
            try {
                postApi.createPost(CreatePostRequest(content, diaperId))
                _events.emit(ForumEvent.PostCreated)
                loadPosts(refresh = true)
            } catch (e: Exception) {
                val message = ErrorHandler.getUserMessage(e)
                _events.emit(ForumEvent.Error(message))
            }
        }
    }

    fun createComment(postId: Int, content: String, parentId: Int? = null) {
        viewModelScope.launch {
            try {
                postApi.createComment(postId, CreateCommentRequest(content, parentId))
                _events.emit(ForumEvent.CommentCreated)
                loadPostDetail(postId)
            } catch (e: Exception) {
                val message = ErrorHandler.getUserMessage(e)
                _events.emit(ForumEvent.Error(message))
            }
        }
    }

    fun toggleLike(targetType: String, targetId: Int) {
        viewModelScope.launch {
            try {
                postApi.toggleLike(LikeRequest(targetType, targetId))
                when (targetType) {
                    "post" -> {
                        val updatedPosts = _uiState.value.posts.map { post ->
                            if (post.id == targetId) {
                                post.copy(
                                    hasLiked = !post.hasLiked,
                                    likeCount = if (post.hasLiked) post.likeCount - 1 else post.likeCount + 1
                                )
                            } else {
                                post
                            }
                        }
                        _uiState.value = _uiState.value.copy(posts = updatedPosts)
                    }
                    "comment" -> {
                        _detailUiState.value.postDetail?.let { detail ->
                            val updatedComments = detail.comments.map { comment ->
                                if (comment.id == targetId) {
                                    comment.copy(
                                        hasLiked = !comment.hasLiked,
                                        likeCount = if (comment.hasLiked) comment.likeCount - 1 else comment.likeCount + 1
                                    )
                                } else {
                                    comment
                                }
                            }
                            _detailUiState.value = _detailUiState.value.copy(
                                postDetail = detail.copy(comments = updatedComments)
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                val message = ErrorHandler.getUserMessage(e)
                _events.emit(ForumEvent.Error(message))
            }
        }
    }

    fun deletePost(postId: Int) {
        viewModelScope.launch {
            try {
                postApi.deletePost(postId)
                loadPosts(refresh = true)
            } catch (e: Exception) {
                val message = ErrorHandler.getUserMessage(e)
                _events.emit(ForumEvent.Error(message))
            }
        }
    }

    fun clearDetail() {
        _detailUiState.value = PostDetailUiState()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
