package top.abdl.space.data.model

import com.google.gson.annotations.SerializedName

data class Rating(
    val id: Int,
    val user: User,
    @SerializedName("diaper_id")
    val diaperId: Int,
    @SerializedName("absorption_score")
    val absorptionScore: Int,
    @SerializedName("fit_score")
    val fitScore: Int,
    @SerializedName("comfort_score")
    val comfortScore: Int,
    @SerializedName("thickness_score")
    val thicknessScore: Int,
    @SerializedName("appearance_score")
    val appearanceScore: Int,
    @SerializedName("value_score")
    val valueScore: Int,
    val review: String? = null,
    @SerializedName("review_status")
    val reviewStatus: String = "approved",
    @SerializedName("created_at")
    val createdAt: String
)

data class CreateRatingRequest(
    @SerializedName("diaper_id")
    val diaperId: Int,
    @SerializedName("absorption_score")
    val absorptionScore: Int,
    @SerializedName("fit_score")
    val fitScore: Int,
    @SerializedName("comfort_score")
    val comfortScore: Int,
    @SerializedName("thickness_score")
    val thicknessScore: Int,
    @SerializedName("appearance_score")
    val appearanceScore: Int,
    @SerializedName("value_score")
    val valueScore: Int,
    val review: String? = null
)

data class RatingStats(
    val composite: Double = 0.0,
    val count: Int = 0,
    val dimensions: RatingDimensions = RatingDimensions()
)

data class RatingDimensions(
    @SerializedName("absorption_score")
    val absorptionScore: DimensionStat? = null,
    @SerializedName("fit_score")
    val fitScore: DimensionStat? = null,
    @SerializedName("comfort_score")
    val comfortScore: DimensionStat? = null,
    @SerializedName("thickness_score")
    val thicknessScore: DimensionStat? = null,
    @SerializedName("appearance_score")
    val appearanceScore: DimensionStat? = null,
    @SerializedName("value_score")
    val valueScore: DimensionStat? = null
)

data class DimensionStat(
    val avg: Double = 0.0,
    val count: Int = 0
)
