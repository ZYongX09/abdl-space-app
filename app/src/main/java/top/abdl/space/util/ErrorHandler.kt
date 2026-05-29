package top.abdl.space.util

import com.google.gson.Gson
import retrofit2.HttpException
import top.abdl.space.data.model.ApiError
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

sealed class AppException(message: String, cause: Throwable? = null) : Exception(message, cause) {
    class NetworkException(message: String = "网络连接失败", cause: Throwable? = null) : AppException(message, cause)
    class ServerException(val code: Int, message: String) : AppException(message)
    class AuthException(message: String = "登录已过期，请重新登录") : AppException(message)
    class TimeoutException(message: String = "请求超时") : AppException(message)
    class UnknownException(message: String = "未知错误", cause: Throwable? = null) : AppException(message, cause)
}

object ErrorHandler {
    private val gson = Gson()

    fun handle(e: Throwable): AppException {
        return when (e) {
            is AppException -> e
            is HttpException -> handleHttpException(e)
            is UnknownHostException -> AppException.NetworkException("无法连接到服务器")
            is SocketTimeoutException -> AppException.TimeoutException()
            is IOException -> AppException.NetworkException()
            else -> AppException.UnknownException(e.message ?: "未知错误", e)
        }
    }

    private fun handleHttpException(e: HttpException): AppException {
        val code = e.code()
        val errorBody = e.response()?.errorBody()?.string()
        val apiError = try {
            errorBody?.let { gson.fromJson(it, ApiError::class.java) }
        } catch (e: Exception) {
            null
        }

        val message = apiError?.error ?: when (code) {
            400 -> "请求参数错误"
            401 -> "登录已过期，请重新登录"
            403 -> "没有权限"
            404 -> "资源不存在"
            409 -> "数据冲突"
            429 -> "请求过于频繁，请稍后再试"
            in 500..599 -> "服务器错误"
            else -> "未知错误 ($code)"
        }

        return when (code) {
            401 -> AppException.AuthException(message)
            in 500..599 -> AppException.ServerException(code, message)
            else -> AppException.UnknownException(message)
        }
    }

    fun getUserMessage(e: Throwable): String {
        return when (val exception = handle(e)) {
            is AppException.NetworkException -> exception.message ?: "网络连接失败"
            is AppException.ServerException -> exception.message ?: "服务器错误"
            is AppException.AuthException -> exception.message ?: "登录已过期"
            is AppException.TimeoutException -> exception.message ?: "请求超时"
            is AppException.UnknownException -> exception.message ?: "未知错误"
        }
    }
}
