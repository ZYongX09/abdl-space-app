package top.abdl.space.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

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
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.95f else 1f,
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
                onClick = {
                    pressed = true
                    onClick()
                },
                modifier = buttonModifier,
                enabled = enabled,
                colors = colors,
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Text(text = text)
            }
        }
        AppButtonType.Tonal -> {
            FilledTonalButton(
                onClick = {
                    pressed = true
                    onClick()
                },
                modifier = buttonModifier,
                enabled = enabled,
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Text(text = text)
            }
        }
        AppButtonType.Outlined -> {
            OutlinedButton(
                onClick = {
                    pressed = true
                    onClick()
                },
                modifier = buttonModifier,
                enabled = enabled,
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Text(text = text)
            }
        }
        AppButtonType.Text -> {
            TextButton(
                onClick = {
                    pressed = true
                    onClick()
                },
                modifier = buttonModifier,
                enabled = enabled,
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Text(text = text)
            }
        }
    }
}
