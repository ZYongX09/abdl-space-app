package top.abdl.space.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class OpenSourceLibrary(
    val name: String,
    val license: String,
    val url: String,
    val description: String = ""
)

val openSourceLibraries = listOf(
    // Jetpack Compose
    OpenSourceLibrary("Jetpack Compose", "Apache 2.0", "https://developer.android.com/jetpack/compose", "声明式 UI 框架"),
    OpenSourceLibrary("Material 3", "Apache 2.0", "https://m3.material.io", "Material Design 3 组件库"),
    OpenSourceLibrary("Miuix", "Apache 2.0", "https://github.com/miuix-kmp/miuix", "小米风格 UI 组件"),
    OpenSourceLibrary("Haze", "Apache 2.0", "https://github.com/chrisbanes/haze", "Compose 毛玻璃效果"),
    OpenSourceLibrary("AndroidLiquidGlass", "MIT", "https://github.com/Kyant0/AndroidLiquidGlass", "液态玻璃效果"),

    // 网络
    OpenSourceLibrary("Retrofit", "Apache 2.0", "https://github.com/square/retrofit", "HTTP 客户端"),
    OpenSourceLibrary("OkHttp", "Apache 2.0", "https://github.com/square/okhttp", "HTTP 引擎"),
    OpenSourceLibrary("Gson", "Apache 2.0", "https://github.com/google/gson", "JSON 序列化"),

    // 图片
    OpenSourceLibrary("Coil", "Apache 2.0", "https://github.com/coil-kt/coil", "Compose 图片加载"),

    // 本地存储
    OpenSourceLibrary("Room", "Apache 2.0", "https://developer.android.com/training/data-storage/room", "SQLite ORM"),
    OpenSourceLibrary("DataStore", "Apache 2.0", "https://developer.android.com/topic/libraries/architecture/datastore", "偏好存储"),
    OpenSourceLibrary("Security Crypto", "Apache 2.0", "https://developer.android.com/topic/security/data", "加密存储"),

    // DI
    OpenSourceLibrary("Koin", "Apache 2.0", "https://github.com/InsertKoinIO/koin", "轻量依赖注入"),

    // 分页
    OpenSourceLibrary("Paging 3", "Apache 2.0", "https://developer.android.com/topic/libraries/architecture/paging", "列表分页"),

    // Shimmer
    OpenSourceLibrary("Compose Shimmer", "MIT", "https://github.com/valentinilk/compose-shimmer", "骨架屏闪烁效果"),

    // AndroidX
    OpenSourceLibrary("AndroidX Navigation", "Apache 2.0", "https://developer.android.com/jetpack/compose/navigation", "导航框架"),
    OpenSourceLibrary("AndroidX Lifecycle", "Apache 2.0", "https://developer.android.com/jetpack/androidx/releases/lifecycle", "生命周期管理"),
    OpenSourceLibrary("Cloudflare Turnstile", "Proprietary", "https://www.cloudflare.com/products/turnstile/", "人机验证"),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OpenSourceLicensesScreen(
    onNavigateBack: () -> Unit
) {
    val uriHandler = LocalUriHandler.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "开源许可证",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            item {
                Text(
                    text = "ABDL Space 使用了以下开源项目：",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )
            }

            items(openSourceLibraries) { lib ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { uriHandler.openUri(lib.url) }
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = lib.name,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (lib.description.isNotEmpty()) {
                        Text(
                            text = lib.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = lib.license,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }
}
