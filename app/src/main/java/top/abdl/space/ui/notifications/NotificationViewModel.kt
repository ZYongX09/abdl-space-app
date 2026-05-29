package top.abdl.space.ui.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import top.abdl.space.data.api.NotificationApi
import top.abdl.space.data.model.Notification
import top.abdl.space.util.ErrorHandler

data class NotificationUiState(
    val notifications: List<Notification> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val currentPage: Int = 1,
    val hasMore: Boolean = true
)

sealed class NotificationEvent {
    data class Error(val message: String) : NotificationEvent()
    data object MarkedAsRead : NotificationEvent()
}

class NotificationViewModel(
    private val notificationApi: NotificationApi
) : ViewModel() {
    private val _uiState = MutableStateFlow(NotificationUiState())
    val uiState: StateFlow<NotificationUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<NotificationEvent>()
    val events: SharedFlow<NotificationEvent> = _events.asSharedFlow()

    init {
        loadNotifications()
    }

    fun loadNotifications(refresh: Boolean = false) {
        if (_uiState.value.isLoading) return
        if (refresh) {
            _uiState.value = _uiState.value.copy(isRefreshing = true, currentPage = 1)
        } else {
            _uiState.value = _uiState.value.copy(isLoading = true)
        }

        viewModelScope.launch {
            try {
                val page = if (refresh) 1 else _uiState.value.currentPage
                val response = notificationApi.getNotifications(page = page)
                val newNotifications = if (refresh) {
                    response.notifications
                } else {
                    _uiState.value.notifications + response.notifications
                }
                _uiState.value = _uiState.value.copy(
                    notifications = newNotifications,
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
                _events.emit(NotificationEvent.Error(message))
            }
        }
    }

    fun loadMore() {
        if (!_uiState.value.hasMore || _uiState.value.isLoading) return
        loadNotifications()
    }

    fun markAsRead(notificationId: Int) {
        viewModelScope.launch {
            try {
                notificationApi.markAsRead(notificationId)
                val updatedNotifications = _uiState.value.notifications.map { notification ->
                    if (notification.id == notificationId) {
                        notification.copy(read = true)
                    } else {
                        notification
                    }
                }
                _uiState.value = _uiState.value.copy(notifications = updatedNotifications)
                _events.emit(NotificationEvent.MarkedAsRead)
            } catch (e: Exception) {
                val message = ErrorHandler.getUserMessage(e)
                _events.emit(NotificationEvent.Error(message))
            }
        }
    }

    fun markAllAsRead() {
        viewModelScope.launch {
            try {
                notificationApi.markAllAsRead()
                val updatedNotifications = _uiState.value.notifications.map { it.copy(read = true) }
                _uiState.value = _uiState.value.copy(notifications = updatedNotifications)
                _events.emit(NotificationEvent.MarkedAsRead)
            } catch (e: Exception) {
                val message = ErrorHandler.getUserMessage(e)
                _events.emit(NotificationEvent.Error(message))
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
