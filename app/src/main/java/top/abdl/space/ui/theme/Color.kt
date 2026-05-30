package top.abdl.space.ui.theme

import androidx.compose.ui.graphics.Color

// ─── ABDL Space 品牌色 ───
val BrandPrimary = Color(0xFF4A9CC7)        // 主色 — 柔和蓝
val BrandPrimaryDim = Color(0xFF3A8AB5)     // 按压态
val BrandPrimaryLight = Color(0xFFD6ECF6)   // 浅蓝背景
val BrandAccent = Color(0xFFE8909A)         // 强调 — 暖粉
val BrandAccentLight = Color(0xFFFCE8EA)    // 浅粉背景

// ─── iOS 系统色（参考 BiliPai）───
val iOSBlue = Color(0xFF007AFF)
val iOSGreen = Color(0xFF34C759)
val iOSRed = Color(0xFFFF3B30)
val iOSOrange = Color(0xFFFF9500)
val iOSYellow = Color(0xFFFFD60A)
val iOSTeal = Color(0xFF5AC8FA)
val iOSPurple = Color(0xFFAF52DE)
val iOSPink = Color(0xFFFF2D55)

// ─── iOS 灰度色阶 ───
val iOSSystemGray = Color(0xFF8E8E93)
val iOSSystemGray2 = Color(0xFFAEAEB2)
val iOSSystemGray3 = Color(0xFFC7C7CC)
val iOSSystemGray4 = Color(0xFFD1D1D6)
val iOSSystemGray5 = Color(0xFFE5E5EA)
val iOSSystemGray6 = Color(0xFFF2F2F7)

// ─── 浅色主题 ───
val PrimaryLight = BrandPrimary
val OnPrimaryLight = Color.White
val PrimaryContainerLight = BrandPrimaryLight
val OnPrimaryContainerLight = Color(0xFF0A2A3A)

val SecondaryLight = BrandAccent
val OnSecondaryLight = Color.White
val SecondaryContainerLight = BrandAccentLight
val OnSecondaryContainerLight = Color(0xFF3A1015)

val TertiaryLight = Color(0xFF8E7CB8)
val OnTertiaryLight = Color.White
val TertiaryContainerLight = Color(0xFFEDE8F8)

// 背景 — iOS 风格极浅灰
val BackgroundLight = iOSSystemGray6         // #F2F2F7 — iOS 标准背景
val OnBackgroundLight = Color(0xFF1C1C1E)    // iOS 标准深色文字
val SurfaceLight = Color.White
val OnSurfaceLight = Color(0xFF1C1C1E)
val SurfaceVariantLight = iOSSystemGray6
val OnSurfaceVariantLight = iOSSystemGray    // #8E8E93
val OutlineLight = iOSSystemGray4            // #D1D1D6
val OutlineVariantLight = iOSSystemGray5     // #E5E5EA

val ErrorLight = iOSRed
val OnErrorLight = Color.White
val ErrorContainerLight = Color(0xFFFFE8E8)

val SuccessLight = iOSGreen
val WarningLight = iOSOrange

// ─── 深色主题 ───
val PrimaryDark = Color(0xFF72B8D8)
val OnPrimaryDark = Color(0xFF0A2A3A)
val PrimaryContainerDark = Color(0xFF1A3A4A)
val OnPrimaryContainerDark = Color(0xFFB8E0F0)

val SecondaryDark = Color(0xFFF0A0A8)
val OnSecondaryDark = Color(0xFF3A1015)
val SecondaryContainerDark = Color(0xFF4A1820)
val OnSecondaryContainerDark = Color(0xFFFFD8DC)

val TertiaryDark = Color(0xFFB8A0D8)
val OnTertiaryDark = Color(0xFF2A1840)
val TertiaryContainerDark = Color(0xFF3A2850)

val BackgroundDark = Color(0xFF0D0D0D)       // BiliPai 风格深黑
val OnBackgroundDark = Color(0xFFE5E5EA)
val SurfaceDark = Color(0xFF1C1C1E)          // iOS 深色 surface
val OnSurfaceDark = Color(0xFFE5E5EA)
val SurfaceVariantDark = Color(0xFF2C2C2E)   // iOS 深色 secondary
val OnSurfaceVariantDark = Color(0xFF8E8E93)
val OutlineDark = Color(0xFF3A3A3C)
val OutlineVariantDark = Color(0xFF2C2C2E)

val ErrorDark = Color(0xFFFF6B6B)
val OnErrorDark = Color(0xFF3A0A10)
val ErrorContainerDark = Color(0xFF5A1820)

val SuccessDark = Color(0xFF66BB6A)
val WarningDark = Color(0xFFFFB74D)

// ─── 多主题色板（参考 BiliPai ThemeColors）───
val ThemeColors = listOf(
    Color(0xFF4A9CC7),  // 0: ABDL 蓝（默认）
    Color(0xFF007AFF),  // 1: iOS 蓝
    Color(0xFFE8909A),  // 2: 暖粉
    Color(0xFF34C759),  // 3: 薄荷绿
    Color(0xFFAF52DE),  // 4: 梦幻紫
    Color(0xFFFF5722),  // 5: 活力橙
    Color(0xFF607D8B),  // 6: 蓝灰
    Color(0xFFFF6B6B),  // 7: 珊瑚红
    Color(0xFF5856D6),  // 8: 靛蓝
    Color(0xFF00BFA5),  // 9: 翡翠青
)

val ThemeColorNames = listOf(
    "ABDL 蓝", "iOS 蓝", "暖粉", "薄荷绿", "梦幻紫",
    "活力橙", "蓝灰", "珊瑚红", "靛蓝", "翡翠青"
)
