package top.abdl.space.data.api

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import top.abdl.space.data.model.AuthResponse
import top.abdl.space.data.model.LoginRequest
import top.abdl.space.data.model.RegisterRequest
import top.abdl.space.data.model.UserFull

interface AuthApi {
    @POST("/api/auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("/api/auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @GET("/api/auth/me")
    suspend fun getCurrentUser(): UserFull

    @POST("/api/auth/send-code")
    suspend fun sendCode(@Body request: Map<String, String>): Map<String, String>

    @POST("/api/auth/reset-password")
    suspend fun resetPassword(@Body request: Map<String, String>): Map<String, String>
}
