package top.abdl.space.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import top.abdl.space.data.api.UserApi
import top.abdl.space.data.datastore.TokenManager
import top.abdl.space.data.model.FollowUser
import top.abdl.space.data.model.UpdateProfileRequest
import top.abdl.space.data.model.UserFull
import top.abdl.space.util.ErrorHandler

data class ProfileUiState(
    val user: UserFull? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isFollowing: Boolean = false,
    val followers: List<FollowUser> = emptyList(),
    val following: List<FollowUser> = emptyList()
)

sealed class ProfileEvent {
    data class Error(val message: String) : ProfileEvent()
    data object ProfileUpdated : ProfileEvent()
    data object FollowSuccess : ProfileEvent()
}

class ProfileViewModel(
    private val userApi: UserApi,
    private val tokenManager: TokenManager
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<ProfileEvent>()
    val events: SharedFlow<ProfileEvent> = _events.asSharedFlow()

    fun loadProfile(userId: Int) {
        _uiState.value = _uiState.value.copy(isLoading = true)
        viewModelScope.launch {
            try {
                val response = userApi.getUser(userId)
                val user = response.user
                val currentUserId = tokenManager.getUserId()
                _uiState.value = _uiState.value.copy(
                    user = user,
                    isLoading = false
                )
                // 检查关注状态
                if (currentUserId > 0 && currentUserId != userId) {
                    try {
                        val followers = userApi.getFollowers(userId)
                        _uiState.value = _uiState.value.copy(
                            isFollowing = followers.users.any { it.id == currentUserId }
                        )
                    } catch (_: Exception) {}
                }
            } catch (e: Exception) {
                val message = ErrorHandler.getUserMessage(e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = message
                )
            }
        }
    }

    fun loadFollowers(userId: Int) {
        viewModelScope.launch {
            try {
                val response = userApi.getFollowers(userId)
                _uiState.value = _uiState.value.copy(followers = response.users)
            } catch (e: Exception) {
                val message = ErrorHandler.getUserMessage(e)
                _events.emit(ProfileEvent.Error(message))
            }
        }
    }

    fun loadFollowing(userId: Int) {
        viewModelScope.launch {
            try {
                val response = userApi.getFollowing(userId)
                _uiState.value = _uiState.value.copy(following = response.users)
            } catch (e: Exception) {
                val message = ErrorHandler.getUserMessage(e)
                _events.emit(ProfileEvent.Error(message))
            }
        }
    }

    fun followUser(userId: Int) {
        viewModelScope.launch {
            try {
                userApi.followUser(userId)
                _uiState.value = _uiState.value.copy(isFollowing = true)
                _events.emit(ProfileEvent.FollowSuccess)
            } catch (e: Exception) {
                val message = ErrorHandler.getUserMessage(e)
                _events.emit(ProfileEvent.Error(message))
            }
        }
    }

    fun unfollowUser(userId: Int) {
        viewModelScope.launch {
            try {
                userApi.unfollowUser(userId)
                _uiState.value = _uiState.value.copy(isFollowing = false)
                _events.emit(ProfileEvent.FollowSuccess)
            } catch (e: Exception) {
                val message = ErrorHandler.getUserMessage(e)
                _events.emit(ProfileEvent.Error(message))
            }
        }
    }

    fun updateProfile(request: UpdateProfileRequest) {
        viewModelScope.launch {
            try {
                val response = userApi.updateProfile(request)
                _uiState.value = _uiState.value.copy(user = response.user)
                _events.emit(ProfileEvent.ProfileUpdated)
            } catch (e: Exception) {
                val message = ErrorHandler.getUserMessage(e)
                _events.emit(ProfileEvent.Error(message))
            }
        }
    }

    fun isCurrentUser(userId: Int): Boolean {
        return tokenManager.getUserId() == userId
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
