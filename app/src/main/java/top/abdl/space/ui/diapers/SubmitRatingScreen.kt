package top.abdl.space.ui.diapers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import top.abdl.space.data.model.CreateRatingRequest
import top.abdl.space.ui.components.LoadingAnimation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubmitRatingScreen(
    viewModel: DiaperViewModel,
    diaperId: Int,
    onNavigateBack: () -> Unit,
    onRatingSubmitted: () -> Unit
) {
    val detailUiState by viewModel.detailUiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var absorptionScore by remember { mutableFloatStateOf(5f) }
    var fitScore by remember { mutableFloatStateOf(5f) }
    var comfortScore by remember { mutableFloatStateOf(5f) }
    var thicknessScore by remember { mutableFloatStateOf(5f) }
    var appearanceScore by remember { mutableFloatStateOf(5f) }
    var valueScore by remember { mutableFloatStateOf(5f) }
    var review by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }

    LaunchedEffect(detailUiState.myRating) {
        detailUiState.myRating?.let { rating ->
            absorptionScore = rating.absorptionScore.toFloat()
            fitScore = rating.fitScore.toFloat()
            comfortScore = rating.comfortScore.toFloat()
            thicknessScore = rating.thicknessScore.toFloat()
            appearanceScore = rating.appearanceScore.toFloat()
            valueScore = rating.valueScore.toFloat()
            review = rating.review ?: ""
        }
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is DiaperEvent.RatingSubmitted -> {
                    isSubmitting = false
                    onRatingSubmitted()
                }
                is DiaperEvent.Error -> {
                    isSubmitting = false
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (detailUiState.myRating != null) "修改评分" else "提交评分") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "请为纸尿裤评分（1-10分）",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            RatingSlider(
                label = "吸收性",
                value = absorptionScore,
                onValueChange = { absorptionScore = it }
            )

            RatingSlider(
                label = "贴合度",
                value = fitScore,
                onValueChange = { fitScore = it }
            )

            RatingSlider(
                label = "舒适度",
                value = comfortScore,
                onValueChange = { comfortScore = it }
            )

            RatingSlider(
                label = "厚度",
                value = thicknessScore,
                onValueChange = { thicknessScore = it }
            )

            RatingSlider(
                label = "外观",
                value = appearanceScore,
                onValueChange = { appearanceScore = it }
            )

            RatingSlider(
                label = "性价比",
                value = valueScore,
                onValueChange = { valueScore = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = review,
                onValueChange = { review = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                label = { Text("评价（可选）") },
                textStyle = MaterialTheme.typography.bodyLarge,
                maxLines = 5
            )

            Spacer(modifier = Modifier.height(32.dp))

            if (isSubmitting) {
                LoadingAnimation()
            } else {
                Button(
                    onClick = {
                        isSubmitting = true
                        viewModel.submitRating(
                            CreateRatingRequest(
                                diaperId = diaperId,
                                absorptionScore = absorptionScore.toInt(),
                                fitScore = fitScore.toInt(),
                                comfortScore = comfortScore.toInt(),
                                thicknessScore = thicknessScore.toInt(),
                                appearanceScore = appearanceScore.toInt(),
                                valueScore = valueScore.toInt(),
                                review = review.takeIf { it.isNotBlank() }
                            )
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (detailUiState.myRating != null) "更新评分" else "提交评分")
                }
            }
        }
    }
}

@Composable
private fun RatingSlider(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "${value.toInt()}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 1f..10f,
            steps = 8
        )
    }
}
