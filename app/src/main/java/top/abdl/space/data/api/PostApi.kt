package top.abdl.space.data.api

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import top.abdl.space.data.model.Comment
import top.abdl.space.data.model.CreateCommentRequest
import top.abdl.space.data.model.CreatePostRequest
import top.abdl.space.data.model.LikeRequest
import top.abdl.space.data.model.Pagination
import top.abdl.space.data.model.Post
import top.abdl.space.data.model.PostDetail

interface PostApi {
    @GET("/api/posts")
    suspend fun getPosts(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20,
        @Query("search") search: String? = null
    ): PostListResponse

    @GET("/api/posts/{id}")
    suspend fun getPostDetail(@Path("id") id: Int): PostDetail

    @POST("/api/posts")
    suspend fun createPost(@Body request: CreatePostRequest): Post

    @DELETE("/api/posts/{id}")
    suspend fun deletePost(@Path("id") id: Int): Map<String, String>

    @POST("/api/posts/{id}/comments")
    suspend fun createComment(
        @Path("id") postId: Int,
        @Body request: CreateCommentRequest
    ): Comment

    @POST("/api/likes")
    suspend fun toggleLike(@Body request: LikeRequest): Map<String, Any>
}

data class PostListResponse(
    val posts: List<Post>,
    val pagination: Pagination
)
