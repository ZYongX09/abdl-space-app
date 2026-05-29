package top.abdl.space.data.model

import com.google.gson.annotations.SerializedName

data class User(
    val id: Int,
    val username: String,
    val avatar: String? = null,
    val role: String = "user"
)

data class UserFull(
    val id: Int,
    val email: String,
    val username: String,
    val avatar: String? = null,
    val role: String = "user",
    val age: Int? = null,
    val region: String? = null,
    val weight: Double? = null,
    val waist: Double? = null,
    val hip: Double? = null,
    @SerializedName("style_preference")
    val stylePreference: String? = null,
    val bio: String? = null,
    @SerializedName("email_verified")
    val emailVerified: Int = 0,
    @SerializedName("created_at")
    val createdAt: String? = null
)

data class LoginRequest(
    val login: String,
    val password: String
)

data class RegisterRequest(
    val email: String,
    val password: String,
    val username: String
)

data class AuthResponse(
    val token: String,
    val user: UserFull
)

data class UpdateProfileRequest(
    val username: String? = null,
    val avatar: String? = null,
    val age: Int? = null,
    val region: String? = null,
    val weight: Double? = null,
    val waist: Double? = null,
    val hip: Double? = null,
    @SerializedName("style_preference")
    val stylePreference: String? = null,
    val bio: String? = null
)
