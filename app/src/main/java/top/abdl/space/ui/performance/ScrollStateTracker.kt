package top.abdl.space.ui.performance

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce

/**
 * 滚动状态追踪器
 * 检测列表是否正在滚动，用于 BlurBudget 动态降级
 */
@OptIn(FlowPreview::class)
@Composable
fun rememberScrollingState(
    isScrollInProgress: Boolean
): Boolean {
    var isScrolling by remember { mutableStateOf(false) }

    LaunchedEffect(isScrollInProgress) {
        if (isScrollInProgress) {
            isScrolling = true
        } else {
            // 停止滚动后延迟 150ms 才标记为非滚动，避免 blur 闪烁
            kotlinx.coroutines.delay(150)
            isScrolling = false
        }
    }

    return isScrolling
}
