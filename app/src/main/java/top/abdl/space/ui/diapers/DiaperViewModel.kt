package top.abdl.space.ui.diapers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import top.abdl.space.data.api.DiaperApi
import top.abdl.space.data.api.RatingApi
import top.abdl.space.data.model.CreateRatingRequest
import top.abdl.space.data.model.Diaper
import top.abdl.space.data.model.DiaperDetail
import top.abdl.space.data.model.Rating
import top.abdl.space.data.model.RatingStats
import top.abdl.space.util.ErrorHandler

data class DiaperListUiState(
    val diapers: List<Diaper> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val currentPage: Int = 1,
    val hasMore: Boolean = true,
    val searchQuery: String = "",
    val selectedBrand: String? = null,
    val selectedSize: String? = null,
    val brands: List<String> = emptyList(),
    val sizes: List<String> = emptyList()
)

data class DiaperDetailUiState(
    val diaperDetail: DiaperDetail? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val myRating: Rating? = null,
    val ratingStats: RatingStats? = null
)

sealed class DiaperEvent {
    data class Error(val message: String) : DiaperEvent()
    data object RatingSubmitted : DiaperEvent()
}

class DiaperViewModel(
    private val diaperApi: DiaperApi,
    private val ratingApi: RatingApi
) : ViewModel() {
    private val _listUiState = MutableStateFlow(DiaperListUiState())
    val listUiState: StateFlow<DiaperListUiState> = _listUiState.asStateFlow()

    private val _detailUiState = MutableStateFlow(DiaperDetailUiState())
    val detailUiState: StateFlow<DiaperDetailUiState> = _detailUiState.asStateFlow()

    private val _events = MutableSharedFlow<DiaperEvent>()
    val events: SharedFlow<DiaperEvent> = _events.asSharedFlow()

    init {
        loadDiapers()
        loadFilters()
    }

    fun loadDiapers(refresh: Boolean = false) {
        if (_listUiState.value.isLoading) return
        if (refresh) {
            _listUiState.value = _listUiState.value.copy(isRefreshing = true, currentPage = 1)
        } else {
            _listUiState.value = _listUiState.value.copy(isLoading = true)
        }

        viewModelScope.launch {
            try {
                val page = if (refresh) 1 else _listUiState.value.currentPage
                val response = diaperApi.getDiapers(
                    search = _listUiState.value.searchQuery.takeIf { it.isNotBlank() },
                    brand = _listUiState.value.selectedBrand,
                    size = _listUiState.value.selectedSize,
                    page = page
                )
                val newDiapers = if (refresh) {
                    response.diapers
                } else {
                    _listUiState.value.diapers + response.diapers
                }
                _listUiState.value = _listUiState.value.copy(
                    diapers = newDiapers,
                    isLoading = false,
                    isRefreshing = false,
                    currentPage = page + 1,
                    hasMore = page < response.pagination.totalPages
                )
            } catch (e: Exception) {
                val message = ErrorHandler.getUserMessage(e)
                _listUiState.value = _listUiState.value.copy(
                    isLoading = false,
                    isRefreshing = false,
                    error = message
                )
                _events.emit(DiaperEvent.Error(message))
            }
        }
    }

    fun loadMore() {
        if (!_listUiState.value.hasMore || _listUiState.value.isLoading) return
        loadDiapers()
    }

    private fun loadFilters() {
        viewModelScope.launch {
            try {
                val brandsResponse = diaperApi.getBrands()
                val sizesResponse = diaperApi.getSizes()
                _listUiState.value = _listUiState.value.copy(
                    brands = brandsResponse.brands,
                    sizes = sizesResponse.sizes
                )
            } catch (e: Exception) {
                // Silently fail for filters
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _listUiState.value = _listUiState.value.copy(searchQuery = query)
    }

    fun updateBrandFilter(brand: String?) {
        _listUiState.value = _listUiState.value.copy(selectedBrand = brand)
        loadDiapers(refresh = true)
    }

    fun updateSizeFilter(size: String?) {
        _listUiState.value = _listUiState.value.copy(selectedSize = size)
        loadDiapers(refresh = true)
    }

    fun loadDiaperDetail(diaperId: Int) {
        _detailUiState.value = _detailUiState.value.copy(isLoading = true)
        viewModelScope.launch {
            try {
                val detail = diaperApi.getDiaperDetail(diaperId)
                val ratingsResponse = ratingApi.getDiaperRatings(diaperId)
                _detailUiState.value = _detailUiState.value.copy(
                    diaperDetail = detail,
                    isLoading = false,
                    ratingStats = ratingsResponse.stats
                )
                loadMyRating(diaperId)
            } catch (e: Exception) {
                val message = ErrorHandler.getUserMessage(e)
                _detailUiState.value = _detailUiState.value.copy(
                    isLoading = false,
                    error = message
                )
            }
        }
    }

    private fun loadMyRating(diaperId: Int) {
        viewModelScope.launch {
            try {
                val response = ratingApi.getMyRating(diaperId)
                _detailUiState.value = _detailUiState.value.copy(myRating = response.rating)
            } catch (e: Exception) {
                // Silently fail
            }
        }
    }

    fun submitRating(request: CreateRatingRequest) {
        viewModelScope.launch {
            try {
                ratingApi.createRating(request)
                _events.emit(DiaperEvent.RatingSubmitted)
                loadDiaperDetail(request.diaperId)
            } catch (e: Exception) {
                val message = ErrorHandler.getUserMessage(e)
                _events.emit(DiaperEvent.Error(message))
            }
        }
    }

    fun clearDetail() {
        _detailUiState.value = DiaperDetailUiState()
    }

    fun clearError() {
        _listUiState.value = _listUiState.value.copy(error = null)
    }
}
