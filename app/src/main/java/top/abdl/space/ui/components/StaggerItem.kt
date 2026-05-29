package top.abdl.space.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

/**
 * Stagger 入场动画 — 每个子项延迟 [delayMs] 毫秒依次入场
 * 参考 MIUI 设置页风格
 */
@Composable
fun StaggerItem(
    index: Int,
    delayMs: Int = 40,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(
            animationSpec = tween(
                durationMillis = 300,
                delayMillis = index * delayMs
            )
        ) + slideInVertically(
            initialOffsetY = { it / 6 },
            animationSpec = tween(
                durationMillis = 300,
                delayMillis = index * delayMs
            )
        ),
        modifier = modifier
    ) {
        content()
    }
}
