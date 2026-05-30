package top.abdl.space.ui.performance

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration

/**
 * MotionTier — 设备性能分级（参考 BiliPai）
 *
 * Reduced: 低端机 / 用户关闭动画 → 无 blur、无实时效果
 * Normal:  普通手机 → 标准效果
 * Enhanced: 平板 / 大屏 → 全部增强效果
 */
enum class MotionTier {
    Reduced,
    Normal,
    Enhanced
}

/**
 * 根据窗口宽度推断设备等级
 */
@Composable
fun rememberMotionTier(): MotionTier {
    val config = LocalConfiguration.current
    val widthDp = config.screenWidthDp

    return remember(widthDp) {
        when {
            widthDp >= 840 -> MotionTier.Enhanced   // 平板
            widthDp >= 600 -> MotionTier.Normal      // 大手机 / 折叠屏
            else -> MotionTier.Normal                 // 普通手机
        }
    }
}
