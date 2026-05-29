package top.abdl.space.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.collectLatest

enum class AppButtonType {
    Filled, Tonal, Outlined, Text
}

@Composable
fun AppButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String,
    type: AppButtonType = AppButtonType.Filled,
    enabled: Boolean = true,
    fillMaxWidth: Boolean = true,
    containerColor: Color = Color.Unspecified,
    contentColor: Color = Color.Unspecified
) {
    val interactionSource = remember { MutableInteractionSource() }
    var isPressed by remember { mutableStateOf(false) }

    // 正确监听按压状态，自动释放
    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collectLatest { interaction ->
            when (interaction) {
                is PressInteraction.Press -> isPressed = true
                is PressInteraction.Release, is PressInteraction.Cancel -> isPressed = false
            }
        }
    }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = 0.65f,
            stiffness = 280f
        ),
        label = "button_scale"
    )

    val buttonModifier = modifier
        .then(if (fillMaxWidth) Modifier.fillMaxWidth() else Modifier)
        .height(48.dp)
        .scale(scale)

    val colors = when {
        containerColor != Color.Unspecified && contentColor != Color.Unspecified -> {
            ButtonDefaults.buttonColors(
                containerColor = containerColor,
                contentColor = contentColor
            )
        }
        containerColor != Color.Unspecified -> {
            ButtonDefaults.buttonColors(containerColor = containerColor)
        }
        else -> ButtonDefaults.buttonColors()
    }

    when (type) {
        AppButtonType.Filled -> {
            Button(
                onClick = onClick,
                modifier = buttonModifier,
                enabled = enabled,
                colors = colors,
                interactionSource = interactionSource,
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Text(text = text)
            }
        }
        AppButtonType.Tonal -> {
            FilledTonalButton(
                onClick = onClick,
                modifier = buttonModifier,
                enabled = enabled,
                interactionSource = interactionSource,
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Text(text = text)
            }
        }
        AppButtonType.Outlined -> {
            OutlinedButton(
                onClick = onClick,
                modifier = buttonModifier,
                enabled = enabled,
                interactionSource = interactionSource,
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Text(text = text)
            }
        }
        AppButtonType.Text -> {
            TextButton(
                onClick = onClick,
                modifier = buttonModifier,
                enabled = enabled,
                interactionSource = interactionSource,
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Text(text = text)
            }
        }
    }
}
