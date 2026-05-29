package top.abdl.space.data.api

import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import top.abdl.space.data.model.Notification
import top.abdl.space.data.model.Pagination

interface NotificationApi {
    @GET("/api/notifications")
    suspend fun getNotifications(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): NotificationListResponse

    @POST("/api/notifications/{id}/read")
    suspend fun markAsRead(@Path("id") id: Int): Map<String, String>

    @POST("/api/notifications/read-all")
    suspend fun markAllAsRead(): Map<String, String>
}

data class NotificationListResponse(
    val notifications: List<Notification>,
    val pagination: Pagination
)
