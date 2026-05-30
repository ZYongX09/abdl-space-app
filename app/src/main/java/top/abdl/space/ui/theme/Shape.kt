package top.abdl.space.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// ─── iOS 风格圆角规范（参考 BiliPai / Apple HIG）───
object CornerRadius {
    /** 微小 — 标签、小徽章 */
    val Tiny = 4.dp
    /** 超小 — Chip、小按钮 */
    val ExtraSmall = 6.dp
    /** 小 — 输入框、小卡片 */
    val Small = 10.dp
    /** 中 — 普通卡片、按钮 */
    val Medium = 12.dp
    /** 大 — 对话框、ActionSheet */
    val Large = 14.dp
    /** 超大 — 底部弹窗 */
    val ExtraLarge = 20.dp
    /** 悬浮 — 悬浮底栏、浮动按钮 */
    val Floating = 28.dp
    /** 胶囊 — 完全圆角 */
    val Full = 100.dp
}

// ─── Material 3 Shapes 映射 ───
val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(CornerRadius.Tiny),
    small = RoundedCornerShape(CornerRadius.Small),
    medium = RoundedCornerShape(CornerRadius.Medium),
    large = RoundedCornerShape(CornerRadius.Large),
    extraLarge = RoundedCornerShape(CornerRadius.ExtraLarge)
)

// ─── 常用形状快捷访问 ───
object ShapeTokens {
    val Tag = RoundedCornerShape(CornerRadius.Tiny)
    val Chip = RoundedCornerShape(CornerRadius.ExtraSmall)
    val InputField = RoundedCornerShape(CornerRadius.Small)
    val Card = RoundedCornerShape(CornerRadius.Medium)
    val Dialog = RoundedCornerShape(CornerRadius.Large)
    val BottomSheet = RoundedCornerShape(
        topStart = CornerRadius.ExtraLarge,
        topEnd = CornerRadius.ExtraLarge
    )
    val FloatingBar = RoundedCornerShape(CornerRadius.Floating)
    val Pill = RoundedCornerShape(CornerRadius.Full)
    val SearchBar = RoundedCornerShape(CornerRadius.Small)
    val Avatar = RoundedCornerShape(CornerRadius.Full)
}
