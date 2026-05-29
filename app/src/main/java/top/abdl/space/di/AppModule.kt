package top.abdl.space.di

import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import top.abdl.space.data.api.AuthApi
import top.abdl.space.data.api.ApiService
import top.abdl.space.data.api.CaptchaApi
import top.abdl.space.data.api.DiaperApi
import top.abdl.space.data.api.NotificationApi
import top.abdl.space.data.api.PostApi
import top.abdl.space.data.api.RatingApi
import top.abdl.space.data.api.UserApi
import top.abdl.space.data.datastore.TokenManager
import top.abdl.space.ui.auth.AuthViewModel
import top.abdl.space.ui.diapers.DiaperViewModel
import top.abdl.space.ui.forum.ForumViewModel
import top.abdl.space.ui.notifications.NotificationViewModel
import top.abdl.space.ui.profile.ProfileViewModel
import top.abdl.space.ui.search.SearchViewModel
import top.abdl.space.ui.settings.SettingsViewModel

val appModule = module {
    single { TokenManager.getInstance(androidContext()) }

    single { ApiService.create<AuthApi>() }
    single { ApiService.create<DiaperApi>() }
    single { ApiService.create<PostApi>() }
    single { ApiService.create<UserApi>() }
    single { ApiService.create<NotificationApi>() }
    single { ApiService.create<RatingApi>() }
    single { ApiService.create<CaptchaApi>() }

    viewModel { AuthViewModel(get(), get(), get()) }
    viewModel { ForumViewModel(get()) }
    viewModel { DiaperViewModel(get(), get()) }
    viewModel { SearchViewModel(get(), get()) }
    viewModel { NotificationViewModel(get()) }
    viewModel { ProfileViewModel(get(), get()) }
    viewModel { SettingsViewModel(androidContext()) }
}
