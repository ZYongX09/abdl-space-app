package top.abdl.space.util

import top.abdl.space.data.datastore.TokenManager
import top.abdl.space.data.model.User

fun User.isCurrentUser(): Boolean {
    val tokenManager = TokenManager.getInstance()
    return tokenManager.getUserId() == this.id
}
