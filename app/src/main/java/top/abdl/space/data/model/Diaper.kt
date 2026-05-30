package top.abdl.space.data.model

import com.google.gson.annotations.SerializedName

data class Diaper(
    val id: Int,
    val brand: String,
    val model: String,
    @SerializedName("product_type")
    val productType: String,
    val thickness: Int,
    @SerializedName("absorbency_mfr")
    val absorbencyMfr: String? = null,
    @SerializedName("absorbency_adult")
    val absorbencyAdult: String? = null,
    @SerializedName("is_baby_diaper")
    val isBabyDiaper: Int = 0,
    val comfort: Double? = null,
    val popularity: Int? = null,
    val material: String? = null,
    val features: String? = null,
    @SerializedName("avg_price")
    val avgPrice: String? = null,
    val sizes: List<DiaperSize> = emptyList(),
    @SerializedName("avg_score")
    val avgScore: Double? = null,
    @SerializedName("rating_count")
    val ratingCount: Int = 0,
    @SerializedName("feeling_count")
    val feelingCount: Int = 0,
    val images: List<String> = emptyList()
)

data class DiaperSize(
    val label: String,
    @SerializedName("waist_min")
    val waistMin: Int? = null,
    @SerializedName("waist_max")
    val waistMax: Int? = null,
    @SerializedName("hip_min")
    val hipMin: Int? = null,
    @SerializedName("hip_max")
    val hipMax: Int? = null
)

data class DiaperDetail(
    val diaper: Diaper,
    val reviews: List<Rating> = emptyList(),
    val wiki: Wiki? = null
)

data class Wiki(
    @SerializedName("diaper_id")
    val diaperId: Int,
    val category: String? = null,
    val title: String,
    val content: String,
    @SerializedName("updated_at")
    val updatedAt: String? = null
)
