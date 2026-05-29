package top.abdl.space.data.api

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import top.abdl.space.data.model.CreateRatingRequest
import top.abdl.space.data.model.Rating
import top.abdl.space.data.model.RatingStats

interface RatingApi {
    @POST("/api/ratings")
    suspend fun createRating(@Body request: CreateRatingRequest): Map<String, Any>

    @GET("/api/diapers/{id}/ratings")
    suspend fun getDiaperRatings(@Path("id") diaperId: Int): DiaperRatingsResponse

    @GET("/api/ratings/me/{diaperId}")
    suspend fun getMyRating(@Path("diaperId") diaperId: Int): MyRatingResponse

    @DELETE("/api/ratings/{id}")
    suspend fun deleteRating(@Path("id") id: Int): Map<String, String>
}

data class DiaperRatingsResponse(
    val reviews: List<Rating>,
    val stats: RatingStats
)

data class MyRatingResponse(
    val rating: Rating?
)
