package top.abdl.space.ui.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import top.abdl.space.ui.components.AppButton
import top.abdl.space.ui.components.AppTextField
import top.abdl.space.ui.components.LoadingAnimation

@Composable
fun ForgotPasswordScreen(
    viewModel: AuthViewModel,
    onNavigateBack: () -> Unit,
    onResetSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val focusManager = LocalFocusManager.current
    val codeFocusRequester = remember { FocusRequester() }
    val newPasswordFocusRequester = remember { FocusRequester() }

    var email by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }
    var codeError by remember { mutableStateOf<String?>(null) }
    var newPasswordError by remember { mutableStateOf<String?>(null) }
    var codeSent by remember { mutableStateOf(false) }
    var countdown by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is AuthEvent.PasswordResetSuccess -> onResetSuccess()
                is AuthEvent.Error -> snackbarHostState.showSnackbar(event.message)
                else -> {}
            }
        }
    }

    LaunchedEffect(countdown) {
        if (countdown > 0) {
            delay(1000)
            countdown--
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
                .imePadding()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "重置密码",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = if (codeSent) "请输入验证码和新密码" else "输入邮箱获取验证码",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(48.dp))

            AppTextField(
                value = email,
                onValueChange = {
                    email = it
                    emailError = null
                },
                label = "邮箱",
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Email,
                        contentDescription = null
                    )
                },
                isError = emailError != null,
                errorMessage = emailError,
                enabled = !codeSent,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = if (codeSent) ImeAction.Next else ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onNext = { codeFocusRequester.requestFocus() },
                    onDone = {
                        focusManager.clearFocus()
                        if (email.isNotBlank() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                            viewModel.sendResetCode(email)
                            codeSent = true
                            countdown = 60
                        } else {
                            emailError = "请输入有效的邮箱地址"
                        }
                    }
                )
            )

            if (codeSent) {
                Spacer(modifier = Modifier.height(16.dp))

                AppTextField(
                    value = code,
                    onValueChange = {
                        code = it
                        codeError = null
                    },
                    label = "验证码",
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Security,
                            contentDescription = null
                        )
                    },
                    isError = codeError != null,
                    errorMessage = codeError,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { newPasswordFocusRequester.requestFocus() }
                    ),
                    modifier = Modifier.focusRequester(codeFocusRequester)
                )

                Spacer(modifier = Modifier.height(16.dp))

                AppTextField(
                    value = newPassword,
                    onValueChange = {
                        newPassword = it
                        newPasswordError = null
                    },
                    label = "新密码",
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Lock,
                            contentDescription = null
                        )
                    },
                    isError = newPasswordError != null,
                    errorMessage = newPasswordError,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                            if (validateReset(email, code, newPassword, { emailError = it }, { codeError = it }, { newPasswordError = it })) {
                                viewModel.resetPassword(email, code, newPassword)
                            }
                        }
                    ),
                    modifier = Modifier.focusRequester(newPasswordFocusRequester)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            AnimatedVisibility(
                visible = uiState.isLoading,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()
            ) {
                LoadingAnimation()
            }

            AnimatedVisibility(
                visible = !uiState.isLoading,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                if (codeSent) {
                    AppButton(
                        onClick = {
                            focusManager.clearFocus()
                            if (validateReset(email, code, newPassword, { emailError = it }, { codeError = it }, { newPasswordError = it })) {
                                viewModel.resetPassword(email, code, newPassword)
                            }
                        },
                        text = "重置密码"
                    )
                } else {
                    AppButton(
                        onClick = {
                            focusManager.clearFocus()
                            if (email.isNotBlank() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                                viewModel.sendResetCode(email)
                                codeSent = true
                                countdown = 60
                            } else {
                                emailError = "请输入有效的邮箱地址"
                            }
                        },
                        text = if (countdown > 0) "重新发送 (${countdown}s)" else "发送验证码",
                        enabled = countdown == 0
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = onNavigateBack) {
                Text(
                    text = "返回登录",
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

private fun validateReset(
    email: String,
    code: String,
    newPassword: String,
    onEmailError: (String?) -> Unit,
    onCodeError: (String?) -> Unit,
    onNewPasswordError: (String?) -> Unit
): Boolean {
    var isValid = true

    if (email.isBlank()) {
        onEmailError("请输入邮箱")
        isValid = false
    } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
        onEmailError("请输入有效的邮箱地址")
        isValid = false
    }

    if (code.isBlank()) {
        onCodeError("请输入验证码")
        isValid = false
    }

    if (newPassword.isBlank()) {
        onNewPasswordError("请输入新密码")
        isValid = false
    } else if (newPassword.length < 8) {
        onNewPasswordError("密码长度至少 8 位")
        isValid = false
    }

    return isValid
}
