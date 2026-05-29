package top.abdl.space.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import top.abdl.space.data.api.DiaperApi
import top.abdl.space.data.api.PostApi
import top.abdl.space.data.api.UserApi
import top.abdl.space.data.model.Diaper
import top.abdl.space.data.model.Post
import top.abdl.space.util.ErrorHandler

data class SearchUiState(
    val query: String = "",
    val posts: List<Post> = emptyList(),
    val diapers: List<Diaper> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchType: SearchType = SearchType.POSTS
)

enum class SearchType {
    POSTS, DIAPERS
}

class SearchViewModel(
    private val postApi: PostApi,
    private val diaperApi: DiaperApi
) : ViewModel() {
    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    fun updateQuery(query: String) {
        _uiState.value = _uiState.value.copy(query = query)
    }

    fun updateSearchType(type: SearchType) {
        _uiState.value = _uiState.value.copy(searchType = type)
        if (_uiState.value.query.isNotBlank()) {
            search()
        }
    }

    fun search() {
        val query = _uiState.value.query
        if (query.isBlank()) return

        _uiState.value = _uiState.value.copy(isLoading = true)

        viewModelScope.launch {
            try {
                when (_uiState.value.searchType) {
                    SearchType.POSTS -> {
                        val response = postApi.getPosts(search = query)
                        _uiState.value = _uiState.value.copy(
                            posts = response.posts,
                            isLoading = false
                        )
                    }
                    SearchType.DIAPERS -> {
                        val response = diaperApi.getDiapers(search = query)
                        _uiState.value = _uiState.value.copy(
                            diapers = response.diapers,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                val message = ErrorHandler.getUserMessage(e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = message
                )
            }
        }
    }

    fun clearResults() {
        _uiState.value = _uiState.value.copy(
            posts = emptyList(),
            diapers = emptyList(),
            query = ""
        )
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
