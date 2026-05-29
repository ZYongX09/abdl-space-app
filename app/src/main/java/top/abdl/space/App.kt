package top.abdl.space

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import top.abdl.space.data.api.ApiService
import top.abdl.space.data.datastore.TokenManager
import top.abdl.space.di.appModule

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        val tokenManager = TokenManager.getInstance(this)
        ApiService.init(tokenManager)

        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(appModule)
        }
    }
}
