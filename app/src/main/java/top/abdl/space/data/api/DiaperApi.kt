package top.abdl.space.data.api

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import top.abdl.space.data.model.Diaper
import top.abdl.space.data.model.DiaperDetail
import top.abdl.space.data.model.Pagination

interface DiaperApi {
    @GET("/api/diapers")
    suspend fun getDiapers(
        @Query("search") search: String? = null,
        @Query("brand") brand: String? = null,
        @Query("size") size: String? = null,
        @Query("sort") sort: String = "id",
        @Query("order") order: String = "ASC",
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): DiaperListResponse

    @GET("/api/diapers/{id}")
    suspend fun getDiaperDetail(@Path("id") id: Int): DiaperDetail

    @GET("/api/diapers/brands")
    suspend fun getBrands(): BrandsResponse

    @GET("/api/diapers/sizes")
    suspend fun getSizes(): SizesResponse
}

data class DiaperListResponse(
    val diapers: List<Diaper>,
    val pagination: Pagination
)

data class BrandsResponse(
    val brands: List<String>
)

data class SizesResponse(
    val sizes: List<String>
)
