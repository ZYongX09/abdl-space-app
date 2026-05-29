package top.abdl.space.ui.diapers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import top.abdl.space.data.model.Diaper
import top.abdl.space.data.model.Rating
import top.abdl.space.data.model.RatingStats
import top.abdl.space.ui.components.ErrorView
import top.abdl.space.ui.components.LoadingAnimation
import top.abdl.space.ui.components.RatingBar
import top.abdl.space.ui.components.StaggerItem
import top.abdl.space.util.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaperDetailScreen(
    viewModel: DiaperViewModel,
    diaperId: Int,
    onNavigateBack: () -> Unit,
    onNavigateToSubmitRating: (Int) -> Unit
) {
    val detailUiState by viewModel.detailUiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(diaperId) {
        viewModel.loadDiaperDetail(diaperId)
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is DiaperEvent.Error -> snackbarHostState.showSnackbar(event.message)
                is DiaperEvent.RatingSubmitted -> snackbarHostState.showSnackbar("评分提交成功")
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "纸尿裤详情",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.clearDetail()
                        onNavigateBack()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        when {
            detailUiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    LoadingAnimation()
                }
            }
            detailUiState.error != null -> {
                ErrorView(
                    message = detailUiState.error ?: "未知错误",
                    onRetry = { viewModel.loadDiaperDetail(diaperId) }
                )
            }
            detailUiState.diaperDetail != null -> {
                val detail = detailUiState.diaperDetail!!
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    item {
                        StaggerItem(index = 0) {
                            DiaperInfo(diaper = detail.diaper)
                        }
                    }

                    if (detailUiState.ratingStats != null) {
                        item {
                            StaggerItem(index = 1) {
                                RatingStatsSection(stats = detailUiState.ratingStats!!)
                            }
                        }
                    }

                    if (detailUiState.myRating != null) {
                        item {
                            StaggerItem(index = 2) {
                                MyRatingSection(rating = detailUiState.myRating!!)
                            }
                        }
                    }

                    item {
                        StaggerItem(index = 3) {
                            Button(
                                onClick = { onNavigateToSubmitRating(diaperId) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                shape = MaterialTheme.shapes.medium,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Text(
                                    text = if (detailUiState.myRating != null) "修改评分" else "提交评分",
                                    style = MaterialTheme.typography.titleSmall
                                )
                            }
                        }
                    }

                    if (detail.reviews.isNotEmpty()) {
                        item {
                            Text(
                                text = "用户评价",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                            )
                        }

                        items(detail.reviews) { rating ->
                            RatingItem(rating = rating)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DiaperInfo(diaper: Diaper) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Text(
            text = "${diaper.brand} ${diaper.model}",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = diaper.productType,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        if (diaper.avgScore != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    modifier = Modifier.size(22.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = String.format("%.1f", diaper.avgScore),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "(${diaper.ratingCount} 人评分)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (diaper.avgPrice != null) {
            Spacer(modifier = Modifier.height(10.dp))
            InfoRow(label = "参考价", value = diaper.avgPrice)
        }

        if (diaper.material != null) {
            Spacer(modifier = Modifier.height(4.dp))
            InfoRow(label = "材质", value = diaper.material)
        }

        if (diaper.features != null) {
            Spacer(modifier = Modifier.height(4.dp))
            InfoRow(label = "特点", value = diaper.features)
        }

        if (diaper.sizes.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "尺码信息",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            diaper.sizes.forEach { size ->
                Text(
                    text = "${size.label}: 腰围 ${size.waistMin}-${size.waistMax}cm, 臀围 ${size.hipMin}-${size.hipMax}cm",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }
        }
    }

    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row {
        Text(
            text = "$label: ",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun RatingStatsSection(stats: RatingStats) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Text(
            text = "评分统计",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "综合评分",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = String.format("%.1f", stats.composite),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        RatingBar(label = "吸收性", score = stats.dimensions.absorptionScore?.avg ?: 0.0)
        RatingBar(label = "贴合度", score = stats.dimensions.fitScore?.avg ?: 0.0)
        RatingBar(label = "舒适度", score = stats.dimensions.comfortScore?.avg ?: 0.0)
        RatingBar(label = "厚度", score = stats.dimensions.thicknessScore?.avg ?: 0.0)
        RatingBar(label = "外观", score = stats.dimensions.appearanceScore?.avg ?: 0.0)
        RatingBar(label = "性价比", score = stats.dimensions.valueScore?.avg ?: 0.0)
    }

    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
}

@Composable
private fun MyRatingSection(rating: Rating) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = "我的评分",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        RatingBar(label = "吸收性", score = rating.absorptionScore.toDouble())
        RatingBar(label = "贴合度", score = rating.fitScore.toDouble())
        RatingBar(label = "舒适度", score = rating.comfortScore.toDouble())
        RatingBar(label = "厚度", score = rating.thicknessScore.toDouble())
        RatingBar(label = "外观", score = rating.appearanceScore.toDouble())
        RatingBar(label = "性价比", score = rating.valueScore.toDouble())

        if (rating.review != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "评价: ${rating.review}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
}

@Composable
private fun RatingItem(rating: Rating) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = rating.user.username,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = DateUtils.formatRelativeTime(rating.createdAt),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        RatingBar(label = "吸收", score = rating.absorptionScore.toDouble())
        RatingBar(label = "贴合", score = rating.fitScore.toDouble())
        RatingBar(label = "舒适", score = rating.comfortScore.toDouble())
        RatingBar(label = "厚度", score = rating.thicknessScore.toDouble())
        RatingBar(label = "外观", score = rating.appearanceScore.toDouble())
        RatingBar(label = "性价比", score = rating.valueScore.toDouble())

        if (rating.review != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = rating.review,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant,
            modifier = Modifier.padding(top = 14.dp)
        )
    }
}
