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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import top.abdl.space.data.model.Diaper
import top.abdl.space.data.model.DiaperDetail
import top.abdl.space.data.model.Rating
import top.abdl.space.data.model.RatingStats
import top.abdl.space.ui.components.ErrorView
import top.abdl.space.ui.components.LoadingAnimation
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
                title = { Text("纸尿裤详情") },
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
                }
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
                        DiaperInfo(diaper = detail.diaper)
                    }

                    if (detailUiState.ratingStats != null) {
                        item {
                            RatingStatsSection(stats = detailUiState.ratingStats!!)
                        }
                    }

                    if (detailUiState.myRating != null) {
                        item {
                            MyRatingSection(rating = detailUiState.myRating!!)
                        }
                    }

                    item {
                        Button(
                            onClick = { onNavigateToSubmitRating(diaperId) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(if (detailUiState.myRating != null) "修改评分" else "提交评分")
                        }
                    }

                    if (detail.reviews.isNotEmpty()) {
                        item {
                            Text(
                                text = "用户评价",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(16.dp)
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
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "${diaper.brand} ${diaper.model}",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = diaper.productType,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (diaper.avgScore != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = String.format("%.1f", diaper.avgScore),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "(${diaper.ratingCount} 人评分)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (diaper.avgPrice != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "参考价: ${diaper.avgPrice}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (diaper.material != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "材质: ${diaper.material}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (diaper.features != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "特点: ${diaper.features}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (diaper.sizes.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "尺码:",
                    style = MaterialTheme.typography.titleSmall
                )
                diaper.sizes.forEach { size ->
                    Text(
                        text = "${size.label}: 腰围 ${size.waistMin}-${size.waistMax}cm, 臀围 ${size.hipMin}-${size.hipMax}cm",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun RatingStatsSection(stats: RatingStats) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "评分统计",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "综合评分",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = String.format("%.1f", stats.composite),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            RatingDimension("吸收性", stats.dimensions.absorptionScore.avg)
            RatingDimension("贴合度", stats.dimensions.fitScore.avg)
            RatingDimension("舒适度", stats.dimensions.comfortScore.avg)
            RatingDimension("厚度", stats.dimensions.thicknessScore.avg)
            RatingDimension("外观", stats.dimensions.appearanceScore.avg)
            RatingDimension("性价比", stats.dimensions.valueScore.avg)
        }
    }
}

@Composable
private fun RatingDimension(name: String, score: Double) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = String.format("%.1f", score),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun MyRatingSection(rating: Rating) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "我的评分",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                RatingDimension("吸收性", rating.absorptionScore.toDouble())
                RatingDimension("贴合度", rating.fitScore.toDouble())
                RatingDimension("舒适度", rating.comfortScore.toDouble())
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                RatingDimension("厚度", rating.thicknessScore.toDouble())
                RatingDimension("外观", rating.appearanceScore.toDouble())
                RatingDimension("性价比", rating.valueScore.toDouble())
            }

            if (rating.review != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "评价: ${rating.review}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
private fun RatingItem(rating: Rating) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = rating.user.username,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = DateUtils.formatRelativeTime(rating.createdAt),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                RatingDimension("吸收", rating.absorptionScore.toDouble())
                RatingDimension("贴合", rating.fitScore.toDouble())
                RatingDimension("舒适", rating.comfortScore.toDouble())
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                RatingDimension("厚度", rating.thicknessScore.toDouble())
                RatingDimension("外观", rating.appearanceScore.toDouble())
                RatingDimension("性价比", rating.valueScore.toDouble())
            }

            if (rating.review != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = rating.review,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
