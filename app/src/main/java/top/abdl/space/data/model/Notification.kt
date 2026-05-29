package top.abdl.space.data.model

import com.google.gson.annotations.SerializedName

data class Notification(
    val id: Int,
    @SerializedName("user_id")
    val userId: Int,
    val type: String,
    val message: String,
    val read: Boolean = false,
    @SerializedName("created_at")
    val createdAt: String
)

data class FollowUser(
    val id: Int,
    val username: String,
    val avatar: String? = null
)
