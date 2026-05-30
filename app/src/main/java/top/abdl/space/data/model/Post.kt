package top.abdl.space.data.model

import com.google.gson.annotations.SerializedName

data class Post(
    val id: Int,
    val user: User,
    val content: String,
    @SerializedName("diaper_id")
    val diaperId: Int? = null,
    val pinned: Boolean = false,
    @SerializedName("repost_id")
    val repostId: Int? = null,
    val repost: Post? = null,
    @SerializedName("like_count")
    val likeCount: Int = 0,
    @SerializedName("has_liked")
    val hasLiked: Boolean = false,
    @SerializedName("comment_count")
    val commentCount: Int = 0,
    val images: List<PostImage> = emptyList(),
    @SerializedName("created_at")
    val createdAt: String
)

data class PostImage(
    @SerializedName("image_url")
    val imageUrl: String,
    @SerializedName("is_nsfw")
    val isNsfw: Boolean = false
)

data class PostDetail(
    val post: Post,
    val comments: List<Comment> = emptyList()
)

data class Comment(
    val id: Int,
    val user: User,
    @SerializedName("post_id")
    val postId: Int,
    @SerializedName("parent_id")
    val parentId: Int? = null,
    val content: String,
    @SerializedName("like_count")
    val likeCount: Int = 0,
    @SerializedName("has_liked")
    val hasLiked: Boolean = false,
    val images: List<PostImage> = emptyList(),
    @SerializedName("created_at")
    val createdAt: String
)

data class CreatePostRequest(
    val content: String,
    @SerializedName("diaper_id")
    val diaperId: Int? = null,
    val images: List<String> = emptyList()
)

data class CreateCommentRequest(
    val content: String,
    @SerializedName("parent_id")
    val parentId: Int? = null,
    val images: List<String> = emptyList()
)

data class LikeRequest(
    @SerializedName("target_type")
    val targetType: String, // "post" or "comment"
    @SerializedName("target_id")
    val targetId: Int
)
