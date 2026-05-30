package top.abdl.space.ui.performance

/**
 * Blur Budget — 动态模糊预算系统（参考 BiliPai）
 *
 * 根据设备状态动态调整模糊效果，避免低端机卡顿。
 * 滚动时降级、转场时降级、低端机完全关闭。
 */

enum class BlurSurfaceType {
    BOTTOM_BAR,      // 底栏
    TOP_BAR,         // 顶栏
    DIALOG,          // 弹窗 / 底部弹窗
    OVERLAY,         // 浮层
    GENERIC          // 通用
}

data class BlurBudget(
    val maxBlurLevel: Int,              // 0=关闭, 1=轻量, 2=完整
    val backgroundAlphaMultiplier: Float, // 背景色 alpha 缩放
    val allowRealtime: Boolean,          // 是否允许实时模糊
    val inputScale: Float                // blur 输入缩放比例（降采样）
)

/**
 * 根据设备状态计算 blur 预算
 */
fun resolveBlurBudget(
    surfaceType: BlurSurfaceType,
    motionTier: MotionTier,
    isScrolling: Boolean = false,
    isTransitionRunning: Boolean = false
): BlurBudget {
    // 基础级别
    var maxBlurLevel = when (surfaceType) {
        BlurSurfaceType.TOP_BAR -> 2
        BlurSurfaceType.DIALOG -> 2
        BlurSurfaceType.BOTTOM_BAR -> 1
        BlurSurfaceType.OVERLAY -> 1
        BlurSurfaceType.GENERIC -> 1
    }
    var alphaMultiplier = when (surfaceType) {
        BlurSurfaceType.TOP_BAR -> 1.0f
        BlurSurfaceType.DIALOG -> 1.0f
        BlurSurfaceType.BOTTOM_BAR -> 0.95f
        BlurSurfaceType.OVERLAY -> 0.92f
        BlurSurfaceType.GENERIC -> 0.95f
    }
    var allowRealtime = true
    var inputScale = 1f

    // MotionTier 降级
    when (motionTier) {
        MotionTier.Reduced -> {
            maxBlurLevel = 0
            alphaMultiplier *= 0.9f
            allowRealtime = false
        }
        MotionTier.Normal -> {}
        MotionTier.Enhanced -> {}
    }

    // 滚动中降级（顶栏除外）
    if (isScrolling) {
        if (surfaceType != BlurSurfaceType.TOP_BAR) {
            maxBlurLevel = minOf(maxBlurLevel, 0)
            alphaMultiplier *= 0.92f
        }
        allowRealtime = false
    }

    // 转场中降级
    if (isTransitionRunning) {
        if (surfaceType != BlurSurfaceType.TOP_BAR) {
            maxBlurLevel = minOf(maxBlurLevel, 0)
            alphaMultiplier *= 0.92f
        }
        allowRealtime = false
    }

    // 降采样比例
    inputScale = if (allowRealtime) 1f else when (surfaceType) {
        BlurSurfaceType.TOP_BAR -> 0.88f
        BlurSurfaceType.DIALOG -> 0.84f
        BlurSurfaceType.BOTTOM_BAR -> 0.82f
        BlurSurfaceType.OVERLAY -> 0.84f
        BlurSurfaceType.GENERIC -> 0.84f
    }

    return BlurBudget(
        maxBlurLevel = maxBlurLevel.coerceIn(0, 2),
        backgroundAlphaMultiplier = alphaMultiplier.coerceIn(0.70f, 1.10f),
        allowRealtime = allowRealtime,
        inputScale = inputScale
    )
}
