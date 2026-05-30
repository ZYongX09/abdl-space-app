package top.abdl.space.data.api

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import top.abdl.space.data.model.FollowUser
import top.abdl.space.data.model.Pagination
import top.abdl.space.data.model.UpdateProfileRequest
import top.abdl.space.data.model.UserFull

interface UserApi {
    @GET("/api/users/{id}")
    suspend fun getUser(@Path("id") id: Int): UserResponse

    @PATCH("/api/users/me")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): UserResponse

    @POST("/api/follows/{userId}")
    suspend fun followUser(@Path("userId") userId: Int): Map<String, String>

    @DELETE("/api/follows/{userId}")
    suspend fun unfollowUser(@Path("userId") userId: Int): Map<String, String>

    @GET("/api/follows/{userId}/followers")
    suspend fun getFollowers(
        @Path("userId") userId: Int,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): FollowListResponse

    @GET("/api/follows/{userId}/following")
    suspend fun getFollowing(
        @Path("userId") userId: Int,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): FollowListResponse
}

data class FollowListResponse(
    val users: List<FollowUser>,
    val pagination: Pagination
)

data class UserResponse(
    val user: UserFull
)
