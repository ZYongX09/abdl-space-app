package top.abdl.space.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

/**
 * Stagger 入场动画 — 极轻量版
 * 仅 fadeIn，无位移，最小化 GPU 负担
 */
@Composable
fun StaggerItem(
    index: Int,
    delayMs: Int = 25,
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
                durationMillis = 150,
                delayMillis = index * delayMs
            )
        ),
        modifier = modifier
    ) {
        content()
    }
}
