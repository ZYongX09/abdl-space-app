package top.abdl.space.data.model

data class Pagination(
    val page: Int,
    val limit: Int,
    val total: Int,
    val totalPages: Int
)

data class PaginatedResponse<T>(
    val data: List<T>,
    val pagination: Pagination
)
