package top.abdl.space.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import top.abdl.space.data.api.AuthApi
import top.abdl.space.data.api.CaptchaApi
import top.abdl.space.data.datastore.TokenManager
import top.abdl.space.data.model.LoginRequest
import top.abdl.space.data.model.RegisterRequest
import top.abdl.space.data.model.UserFull
import top.abdl.space.util.ErrorHandler

data class AuthUiState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val currentUser: UserFull? = null,
    val error: String? = null
)

sealed class AuthEvent {
    data object LoginSuccess : AuthEvent()
    data object RegisterSuccess : AuthEvent()
    data object PasswordResetSuccess : AuthEvent()
    data class Error(val message: String) : AuthEvent()
}

class AuthViewModel(
    private val authApi: AuthApi,
    private val captchaApi: CaptchaApi,
    private val tokenManager: TokenManager
) : ViewModel() {
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<AuthEvent>()
    val events: SharedFlow<AuthEvent> = _events.asSharedFlow()

    init {
        checkLoginStatus()
    }

    private fun checkLoginStatus() {
        val isLoggedIn = tokenManager.getToken() != null
        _uiState.value = _uiState.value.copy(isLoggedIn = isLoggedIn)
        if (isLoggedIn) {
            loadCurrentUser()
        }
    }

    fun login(login: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val risk = captchaApi.assessRisk()
                if (risk.risk == "high") {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    _events.emit(AuthEvent.Error("需要验证码，请稍后重试"))
                    return@launch
                }

                val response = authApi.login(LoginRequest(login, password))
                tokenManager.saveToken(response.token)
                tokenManager.saveUserId(response.user.id)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isLoggedIn = true,
                    currentUser = response.user
                )
                _events.emit(AuthEvent.LoginSuccess)
            } catch (e: Exception) {
                val message = ErrorHandler.getUserMessage(e)
                _uiState.value = _uiState.value.copy(isLoading = false, error = message)
                _events.emit(AuthEvent.Error(message))
            }
        }
    }

    fun register(email: String, username: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val risk = captchaApi.assessRisk()
                if (risk.risk == "high") {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    _events.emit(AuthEvent.Error("需要验证码，请稍后重试"))
                    return@launch
                }

                val response = authApi.register(RegisterRequest(email, password, username))
                tokenManager.saveToken(response.token)
                tokenManager.saveUserId(response.user.id)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isLoggedIn = true,
                    currentUser = response.user
                )
                _events.emit(AuthEvent.RegisterSuccess)
            } catch (e: Exception) {
                val message = ErrorHandler.getUserMessage(e)
                _uiState.value = _uiState.value.copy(isLoading = false, error = message)
                _events.emit(AuthEvent.Error(message))
            }
        }
    }

    fun sendResetCode(email: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                authApi.sendCode(mapOf("email" to email, "type" to "reset"))
                _uiState.value = _uiState.value.copy(isLoading = false)
            } catch (e: Exception) {
                val message = ErrorHandler.getUserMessage(e)
                _uiState.value = _uiState.value.copy(isLoading = false, error = message)
                _events.emit(AuthEvent.Error(message))
            }
        }
    }

    fun resetPassword(email: String, code: String, newPassword: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                authApi.resetPassword(
                    mapOf(
                        "email" to email,
                        "code" to code,
                        "password" to newPassword
                    )
                )
                _uiState.value = _uiState.value.copy(isLoading = false)
                _events.emit(AuthEvent.PasswordResetSuccess)
            } catch (e: Exception) {
                val message = ErrorHandler.getUserMessage(e)
                _uiState.value = _uiState.value.copy(isLoading = false, error = message)
                _events.emit(AuthEvent.Error(message))
            }
        }
    }

    fun loadCurrentUser() {
        viewModelScope.launch {
            try {
                val user = authApi.getCurrentUser()
                _uiState.value = _uiState.value.copy(currentUser = user)
            } catch (e: Exception) {
                if (ErrorHandler.handle(e) is top.abdl.space.util.AppException.AuthException) {
                    tokenManager.clearAll()
                    _uiState.value = _uiState.value.copy(isLoggedIn = false, currentUser = null)
                }
            }
        }
    }

    fun logout() {
        tokenManager.clearAll()
        _uiState.value = AuthUiState(isLoggedIn = false)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
