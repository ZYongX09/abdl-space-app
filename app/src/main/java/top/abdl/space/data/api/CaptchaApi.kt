package top.abdl.space.data.api

import retrofit2.http.Body
import retrofit2.http.POST

interface CaptchaApi {
    @POST("/api/captcha/risk")
    suspend fun assessRisk(): RiskResponse

    @POST("/api/captcha/challenge")
    suspend fun createChallenge(@Body request: Map<String, String>): ChallengeResponse

    @POST("/api/captcha/verify")
    suspend fun verifyCaptcha(@Body request: Map<String, String>): Map<String, String>

    @POST("/api/captcha/turnstile/verify")
    suspend fun verifyTurnstile(@Body request: Map<String, String>): Map<String, String>
}

data class RiskResponse(
    val risk: String, // "low" or "high"
    val flow: String  // "turnstile", "quantum", or "both"
)

data class ChallengeResponse(
    val sessionId: String,
    val nodes: List<ChallengeNode>
)

data class ChallengeNode(
    val id: Int,
    val x: Float,
    val y: Float,
    val label: String
)
